package pt.ipp.isep.dei.ESINF.services.impl;

import pt.ipp.isep.dei.ESINF.domain.Location;
import pt.ipp.isep.dei.ESINF.domain.PickingItem;
import pt.ipp.isep.dei.ESINF.domain.Trolley;
import pt.ipp.isep.dei.ESINF.services.PickingPathService;

import java.util.*;

/**
 * USEI04 — Pick Path Sequencing
 * <p>
 * Service implementation that creates a picking path sequence.
 * </p>
 */
public class PickingPathServiceImpl implements PickingPathService {

    /**
     * Calculates the total distance traveled between all given locations,
     * starting from the entrance point (0,0).
     *
     * @param locations list of locations to visit
     * @return total distance traveled
     */
    @Override
    public double calculateTotalDistance(List<Location> locations) {
        double total = 0;
        Location current = new Location("MAIN", 0, 0); // Entrance (0,0)

        for (Location next : locations) {
            double stepDistance = current.distanceTo(next);

            // Print each step
            System.out.printf(
                    "(%d,%d) → (%d,%d) = %.0f%n",
                    current.getAisle(), current.getBay(),
                    next.getAisle(), next.getBay(),
                    stepDistance
            );

            total += stepDistance;
            current = next;
        }
        return total;
    }

    /**
     * Finds an optimized picking path by always moving
     * to the closest remaining location from the current position.
     *
     * @param locations list of locations to optimize
     * @return list of locations ordered by the nearest-neighbor approach
     */
    public List<Location> findOptimalPickingPath(List<Location> locations) {
        List<Location> remaining = new ArrayList<>(locations);
        List<Location> path = new ArrayList<>();
        Location current = new Location("MAIN", 0, 0);

        while (!remaining.isEmpty()) {
            Location nearest = null;
            double minDist = Double.MAX_VALUE;

            for (Location l : remaining) {
                double d = current.distanceTo(l);
                if (d < minDist) {
                    minDist = d;
                    nearest = l;
                }
            }

            path.add(nearest);
            current = nearest;
            remaining.remove(nearest);
        }

        return path;
    }

    /**
     * Sorts all locations in ascending order based on aisle and bay.
     *
     * @param locations list of locations to sort
     * @return a new list of locations sorted by aisle and bay
     */

    public List<Location> getAscendingPath(List<Location> locations) {
        List<Location> ascendingPath = new ArrayList<>(locations);

        for (int i = 0; i < ascendingPath.size() - 1; i++) {
            for (int j = 0; j < ascendingPath.size() - 1 - i; j++) {
                Location l1 = ascendingPath.get(j);
                Location l2 = ascendingPath.get(j + 1);
                if (l1.getAisle() > l2.getAisle() ||
                        (l1.getAisle() == l2.getAisle() && l1.getBay() > l2.getBay())) {
                    ascendingPath.set(j, l2);
                    ascendingPath.set(j + 1, l1);
                }
            }
        }

        return ascendingPath;
    }

    /**
     * Extracts all unique picking locations from a single trolley.
     * Each location corresponds to a unique (aisle, bay) pair,
     * ensuring that no duplicate coordinates are included in the result.
     *
     * @param trolley the trolley containing picking items
     * @return list of unique locations derived from the trolley's picking items
     */
    @Override
    public List<Location> extractLocationsFromTrolley(Trolley trolley) {
        Set<String> uniqueKeys = new HashSet<>();
        List<Location> locations = new ArrayList<>();

        if (trolley == null || trolley.getItems() == null) return locations;

        for (PickingItem item : trolley.getItems()) {
            String key = item.getAisle() + "-" + item.getBay();
            if (uniqueKeys.add(key)) {
                locations.add(new Location("MAIN", item.getAisle(), item.getBay()));
            }
        }
        return locations;
    }

}
