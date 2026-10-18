package pt.ipp.isep.dei.ESINF.dataStruc;

import pt.ipp.isep.dei.ESINF.domain.GeoCoordinate;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import java.util.*;

public class ProximitySearch {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculate Haversine distance between two coordinates (in km)
     * @param lat1 Latitude of point 1
     * @param lon1 Longitude of point 1
     * @param lat2 Latitude of point 2
     * @param lon2 Longitude of point 2
     * @return Distance in kilometers
     */
    public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Find N nearest stations to target coordinate using 2D-tree
     * Optional filter by time zone group
     *
     * @param kdTree KD-tree of stations
     * @param targetLat Target latitude
     * @param targetLon Target longitude
     * @param n Number of nearest neighbors to find
     * @param timeZoneFilter Optional time zone filter (null = no filter)
     * @return List of N nearest stations sorted by distance (ASC)
     * @complexity O(n) worst case (unbalanced tree) | O(log n) average case (balanced tree)
     */
    public static List<StationDistance> findNearestN(
            KDTree<TrainStation> kdTree,
            double targetLat,
            double targetLon,
            int n,
            String timeZoneFilter) {

        if (kdTree == null || kdTree.isEmpty() || n <= 0) {
            return new ArrayList<>();
        }

        PriorityQueue<StationDistance> maxHeap = new PriorityQueue<>(
                (a, b) -> Double.compare(b.distance, a.distance)
        );

        KDNode<TrainStation> root = (KDNode<TrainStation>) kdTree.root;
        searchNearestN(root, targetLat, targetLon, n, timeZoneFilter, maxHeap, 0);

        List<StationDistance> result = new ArrayList<>(maxHeap);
        result.sort(Comparator.comparingDouble(a -> a.distance));

        return result;
    }

    /**
     * Recursive helper to search K-D tree for nearest N neighbors
     * Adapted for KDNode structure with List<E> info
     *
     * @param node Current node in tree
     * @param targetLat Target latitude
     * @param targetLon Target longitude
     * @param n Number of neighbors to find
     * @param timeZoneFilter Optional timezone filter
     * @param maxHeap Max-heap storing current N closest stations
     * @param depth Current depth in tree (determines split axis)
     */
    private static void searchNearestN(
            KDNode<TrainStation> node,
            double targetLat,
            double targetLon,
            int n,
            String timeZoneFilter,
            PriorityQueue<StationDistance> maxHeap,
            int depth) {

        if (node == null) {
            return;
        }

        // Get coordinate from node
        GeoCoordinate nodeCoord = node.getCoordinate();
        double nodeLat = nodeCoord.getLatitude();
        double nodeLon = nodeCoord.getLongitude();

        // Process all stations in this node's info list
        List<TrainStation> stations = node.getInfo();
        if (stations != null) {
            for (TrainStation station : stations) {
                double distance = haversineDistance(
                        targetLat,
                        targetLon,
                        station.getCoordinates().getLatitude(),
                        station.getCoordinates().getLongitude()
                );

                boolean passesFilter = (timeZoneFilter == null) ||
                        station.getTimeZoneGroupAndCountry().equalsIgnoreCase(timeZoneFilter);

                // If station passes filter, consider it for the heap
                if (passesFilter) {
                    if (maxHeap.size() < n) {
                        // Heap not full yet, add directly
                        maxHeap.offer(new StationDistance(station, distance));
                    } else if (distance < maxHeap.peek().distance) {
                        // This station is closer than the farthest one in heap
                        maxHeap.poll();
                        maxHeap.offer(new StationDistance(station, distance));
                    }
                }
            }
        }

        // Determine which axis to split (0 = latitude, 1 = longitude)
        int axis = depth % 2;
        double targetValue = (axis == 0) ? targetLat : targetLon;
        double nodeValue = (axis == 0) ? nodeLat : nodeLon;

        // Decide which subtree to visit first
        KDNode<TrainStation> nearSubtree = (targetValue < nodeValue) ? node.getLeft() : node.getRight();
        KDNode<TrainStation> farSubtree = (targetValue < nodeValue) ? node.getRight() : node.getLeft();

        // Search the closer subtree
        searchNearestN(nearSubtree, targetLat, targetLon, n, timeZoneFilter, maxHeap, depth + 1);

        double axisDistanceKm = (axis == 0)
                ? haversineDistance(targetLat, targetLon, nodeLat, targetLon)  // Só varia latitude
                : haversineDistance(targetLat, targetLon, targetLat, nodeLon); // Só varia longitude

        if (maxHeap.size() < n || axisDistanceKm <= maxHeap.peek().distance) {
            searchNearestN(farSubtree, targetLat, targetLon, n, timeZoneFilter, maxHeap, depth + 1);
        }
    }

    /**
     * Helper class to store station with distance
     */
    public static class StationDistance {
        public TrainStation station;
        public double distance;

        public StationDistance(TrainStation station, double distance) {
            this.station = station;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return String.format("%s - %.2f km", station.getStation(), distance);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StationDistance)) return false;
            StationDistance that = (StationDistance) o;
            return Double.compare(that.distance, distance) == 0 &&
                    Objects.equals(station, that.station);
        }

        @Override
        public int hashCode() {
            return Objects.hash(station, distance);
        }
    }
}