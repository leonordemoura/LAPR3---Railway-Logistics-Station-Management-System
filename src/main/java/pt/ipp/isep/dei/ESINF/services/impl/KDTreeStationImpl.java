package pt.ipp.isep.dei.ESINF.services.impl;

import pt.ipp.isep.dei.ESINF.dataStruc.AVL;
import pt.ipp.isep.dei.ESINF.dataStruc.KDNode;
import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.dataStruc.MixedComparator;
import pt.ipp.isep.dei.ESINF.domain.GeoCoordinate;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.KDTreeStationService;

import java.util.*;

/**
 * Implementation of a balanced 2D KD-Tree for {@link TrainStation} objects, indexed by geographic coordinates (latitude and longitude).
 * <p>
 * This class builds a perfectly balanced KD-Tree by leveraging pre-sorted AVL trees (latitude and longitude indexes) from the repository and selecting median points alternately along each axis.
 * </p>
 * <p>
 * Multiple stations can share the same coordinate. The tree stores all stations at the exact splitting coordinate in the node, and partitions the remaining points correctly using tie-breaking on the secondary axis.
 * </p>
 * <p>
 * The construction uses a coordinate cache for O(1) lookups and ensures no duplicate processing of coordinates.
 * </p>
 *
 */
public class KDTreeStationImpl extends KDTree<TrainStation> implements KDTreeStationService {
    WarehouseRepository repository = Repositories.getInstance().getWarehouseRepository();

    /** Cache mapping each unique coordinate to the list of stations located there. */
    private Map<GeoCoordinate, List<TrainStation>> coordinateCache;

    /**
     * Builds a balanced KD-Tree from the current data in the warehouse repository.
     * <p>
     * The tree is constructed using the latitude and longitude AVL indexes.
     * Stations sharing the same coordinate are grouped together in the same node.
     * After construction, the internal cache is cleared to free memory.
     * </p>
     *
     * @return this KD-Tree instance (for method chaining)
     */
    @Override
    public KDTree<TrainStation> buildBalancedTree() {
        // Extract AVLs from repository
        AVL<Double> latitudeAVL = repository.getLatitudeIndex();
        AVL<Double> longitudeAVL = repository.getLongitudeIndex();

        // Build coordinate cache once for O(1) lookups
        buildCoordinateCache();

        // Build the 2D-tree recursively using AVLs
        root = buildKDTree(latitudeAVL, longitudeAVL, 0);

        // Clear cache after building to free memory
        coordinateCache = null;

        return this;
    }

    /**
     * Recursively builds the KD-Tree by alternately splitting on latitude and longitude.
     *
     * @param latitudeAVL  AVL tree indexed by latitude
     * @param longitudeAVL AVL tree indexed by longitude
     * @param depth        current depth in the tree (0 = root)
     * @return the root node of the constructed subtree, or null if no elements
     */
    private KDNode<TrainStation> buildKDTree(AVL<Double> latitudeAVL, AVL<Double> longitudeAVL, int depth) {
        // Sees if the tree is empty
        if(latitudeAVL.size() == 0 || longitudeAVL.size() == 0) {
            return null;
        }

        // Determine split axis: even depth = latitude, odd depth = longitude
        boolean splitByLatitude = (depth % 2 == 0);

        if (splitByLatitude) {
            return insertByLatitude(latitudeAVL, depth);
        } else {
            return insertByLongitude(longitudeAVL, depth);
        }
    }

    /**
     * Builds a subtree where the splitting axis is latitude.
     * <p>
     * Selects the median latitude, then among all points with that latitude,
     * chooses the one with median longitude as the splitting point.
     * </p>
     *
     * @param latitudeAVL AVL tree containing stations sorted by latitude
     * @param depth       current recursion depth
     * @return root node of the latitude-split subtree
     */
    private KDNode<TrainStation> insertByLatitude(AVL<Double> latitudeAVL, int depth) {
        // Extract unique coordinates
        Set<GeoCoordinate> uniqueCoordinates = extractUniqueCoordinates(latitudeAVL);

        if (uniqueCoordinates.isEmpty()) {
            return null;
        }

        // Build list of unique latitudes (sorted)
        Set<Double> uniqueLats = new TreeSet<>();
        for (GeoCoordinate c : uniqueCoordinates) {
            uniqueLats.add(c.getLatitude());
        }
        List<Double> sortedUniqueLats = new ArrayList<>(uniqueLats);

        // Find median latitude
        int medianIndex = sortedUniqueLats.size() / 2;
        Double medianLat = sortedUniqueLats.get(medianIndex);

        // Get all unique coordinates at this latitude
        List<GeoCoordinate> coordsAtLat = new ArrayList<>();
        for (GeoCoordinate c : uniqueCoordinates) {
            if (c.getLatitude().equals(medianLat)) {
                coordsAtLat.add(c);
            }
        }

        // Among them, choose the one with median longitude
        coordsAtLat.sort(Comparator.comparingDouble(GeoCoordinate::getLongitude));
        GeoCoordinate splitCoord = coordsAtLat.get(coordsAtLat.size() / 2);

        // Collect all stations at exact same coordinate using cache
        List<TrainStation> nodeStations = getAllStationsAtCoordinate(splitCoord);
        nodeStations.sort(Comparator.comparing(TrainStation::getStation));

        KDNode<TrainStation> node = new KDNode<>(splitCoord, nodeStations, null, null);

        // Portion into smaller AVLs
        AVL<Double> leftLat = new AVL<>(MixedComparator.MIXED_COMPARATOR);
        AVL<Double> leftLon = new AVL<>(MixedComparator.MIXED_COMPARATOR);
        AVL<Double> rightLat = new AVL<>(MixedComparator.MIXED_COMPARATOR);
        AVL<Double> rightLon = new AVL<>(MixedComparator.MIXED_COMPARATOR);

        // Process each unique coordinate exactly once
        for (GeoCoordinate c : uniqueCoordinates) {
            if (c.equals(splitCoord)) continue;

            // Get all stations at this coordinate
            List<TrainStation> stationsAtCoord = getAllStationsAtCoordinate(c);

            // Partitioning: Simple split by latitude only
            // Left: lat < medianLat
            // Right: lat >= medianLat (but not the split coordinate itself)
            if (c.getLatitude() < medianLat) {
                // Left subtree
                for (TrainStation s : stationsAtCoord) {
                    Object[] hierarchy = {c.getLatitude(), c.getLongitude()};
                    leftLat.insertAVL(hierarchy, s);
                    leftLon.insertAVL(hierarchy, s);
                }
            } else if (c.getLatitude() > medianLat) {
                // Right subtree
                for (TrainStation s : stationsAtCoord) {
                    Object[] hierarchy = {c.getLatitude(), c.getLongitude()};
                    rightLat.insertAVL(hierarchy, s);
                    rightLon.insertAVL(hierarchy, s);
                }
            } else {
                // c.getLatitude() == medianLat (same latitude as split)
                // Split by longitude to break ties
                if (c.getLongitude() < splitCoord.getLongitude()) {
                    for (TrainStation s : stationsAtCoord) {
                        Object[] hierarchy = {c.getLatitude(), c.getLongitude()};
                        leftLat.insertAVL(hierarchy, s);
                        leftLon.insertAVL(hierarchy, s);
                    }
                } else {
                    // c.getLongitude() > splitCoord.getLongitude()
                    for (TrainStation s : stationsAtCoord) {
                        Object[] hierarchy = {c.getLatitude(), c.getLongitude()};
                        rightLat.insertAVL(hierarchy, s);
                        rightLon.insertAVL(hierarchy, s);
                    }
                }
            }
        }

        node.setLeft(buildKDTree(leftLat, leftLon, depth + 1));
        node.setRight(buildKDTree(rightLat, rightLon, depth + 1));

        return node;
    }

    /**
     * Builds a subtree where the splitting axis is longitude.
     * <p>
     * Symmetric to {@link #insertByLatitude(AVL, int)} but splits first on longitude.
     * </p>
     *
     * @param longitudeAVL AVL tree containing stations sorted by longitude
     * @param depth        current recursion depth
     * @return root node of the longitude-split subtree
     */
    private KDNode<TrainStation> insertByLongitude(AVL<Double> longitudeAVL, int depth) {
        // Extract unique coordinates
        Set<GeoCoordinate> uniqueCoordinates = extractUniqueCoordinates(longitudeAVL);

        if (uniqueCoordinates.isEmpty()) {
            return null;
        }

        // Build list of unique longitudes
        Set<Double> uniqueLons = new TreeSet<>();
        for (GeoCoordinate c : uniqueCoordinates) {
            uniqueLons.add(c.getLongitude());
        }
        List<Double> sortedUniqueLons = new ArrayList<>(uniqueLons);

        // Find median longitude
        int medianIndex = sortedUniqueLons.size() / 2;
        Double medianLon = sortedUniqueLons.get(medianIndex);

        // Get all unique coordinates at this longitude
        List<GeoCoordinate> coordsAtLon = new ArrayList<>();
        for (GeoCoordinate c : uniqueCoordinates) {
            if (c.getLongitude().equals(medianLon)) {
                coordsAtLon.add(c);
            }
        }

        // Among them, sort by latitude to choose the median point
        coordsAtLon.sort(Comparator.comparingDouble(GeoCoordinate::getLatitude));
        GeoCoordinate splitCoord = coordsAtLon.get(coordsAtLon.size() / 2);

        // Collect all stations at this exact coordinate using cache
        List<TrainStation> nodeStations = getAllStationsAtCoordinate(splitCoord);
        nodeStations.sort(Comparator.comparing(TrainStation::getStation));

        KDNode<TrainStation> node = new KDNode<>(splitCoord, nodeStations, null, null);

        // Prepare subtrees
        AVL<Double> leftLat = new AVL<>(MixedComparator.MIXED_COMPARATOR);
        AVL<Double> leftLon = new AVL<>(MixedComparator.MIXED_COMPARATOR);
        AVL<Double> rightLat = new AVL<>(MixedComparator.MIXED_COMPARATOR);
        AVL<Double> rightLon = new AVL<>(MixedComparator.MIXED_COMPARATOR);

        // Process each unique coordinate exactly once
        for (GeoCoordinate c : uniqueCoordinates) {
            if (c.equals(splitCoord)) continue;

            // Get all stations at this coordinate
            List<TrainStation> stationsAtCoord = getAllStationsAtCoordinate(c);

            // Partitioning: Simple split by longitude only
            // Left: lon < medianLon
            // Right: lon >= medianLon (but not the split coordinate itself)
            if (c.getLongitude() < medianLon) {
                // Left subtree
                for (TrainStation s : stationsAtCoord) {
                    Object[] hierarchy = {c.getLatitude(), c.getLongitude()};
                    leftLat.insertAVL(hierarchy, s);
                    leftLon.insertAVL(hierarchy, s);
                }
            } else if (c.getLongitude() > medianLon) {
                // Right subtree
                for (TrainStation s : stationsAtCoord) {
                    Object[] hierarchy = {c.getLatitude(), c.getLongitude()};
                    rightLat.insertAVL(hierarchy, s);
                    rightLon.insertAVL(hierarchy, s);
                }
            } else {
                // c.getLongitude() == medianLon (same longitude as split)
                // Split by latitude to break ties
                if (c.getLatitude() < splitCoord.getLatitude()) {
                    for (TrainStation s : stationsAtCoord) {
                        Object[] hierarchy = {c.getLatitude(), c.getLongitude()};
                        leftLat.insertAVL(hierarchy, s);
                        leftLon.insertAVL(hierarchy, s);
                    }
                } else {
                    // c.getLatitude() > splitCoord.getLatitude()
                    for (TrainStation s : stationsAtCoord) {
                        Object[] hierarchy = {c.getLatitude(), c.getLongitude()};
                        rightLat.insertAVL(hierarchy, s);
                        rightLon.insertAVL(hierarchy, s);
                    }
                }
            }
        }

        // Build SubTrees
        node.setLeft(buildKDTree(leftLat, leftLon, depth + 1));
        node.setRight(buildKDTree(rightLat, rightLon, depth + 1));

        return node;
    }

    /**
     * Extracts unique coordinates from AVL tree.
     * This prevents processing duplicate stations multiple times.
     *
     * @param avl AVL tree containing stations (can be lat or lon index)
     * @return Set of unique coordinates
     */
    private Set<GeoCoordinate> extractUniqueCoordinates(AVL<Double> avl) {
        Set<GeoCoordinate> uniqueCoords = new HashSet<>();

        // Iterate through all elements in AVL
        for (int i = 0; i < avl.size(); i++) {
            Double value = avl.findKElement(i);
            List<TrainStation> stations = avl.searchExact(value);

            // Add each station's coordinate to the set (duplicates automatically filtered)
            for (TrainStation s : stations) {
                uniqueCoords.add(s.getCoordinates());
            }
        }

        return uniqueCoords;
    }

    /**
     * Returns a defensive copy of all stations located at the given coordinate.
     *
     * @param coord the coordinate to query
     * @return list of stations at that location (never null)
     */
    private List<TrainStation> getAllStationsAtCoordinate(GeoCoordinate coord) {
        List<TrainStation> stations = coordinateCache.get(coord);
        return stations != null ? new ArrayList<>(stations) : new ArrayList<>();
    }

    /**
     * Builds a HashMap that maps each coordinate to all stations at that coordinate.
     * This allows O(1) lookup instead of O(n) linear search.
     * Time complexity: O(n) where n is the number of stations.
     */
    private void buildCoordinateCache() {
        coordinateCache = new HashMap<>();

        for (TrainStation station : repository.getTrainStations()) {
            GeoCoordinate coord = station.getCoordinates();
            coordinateCache.computeIfAbsent(coord, k -> new ArrayList<>()).add(station);
        }
    }
}