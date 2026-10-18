package pt.ipp.isep.dei.ESINF.services;

import pt.ipp.isep.dei.ESINF.domain.Bay;
import pt.ipp.isep.dei.ESINF.domain.Location;
import pt.ipp.isep.dei.ESINF.domain.PickingItem;
import pt.ipp.isep.dei.ESINF.domain.Trolley;

import java.util.List;

/**
 * Interface for Picking Path Service (US4)
 */
public interface PickingPathService {

    /**
     * Calculates the total distance of a picking path based on a list of locations.
     *
     * @param locations List of locations to visit in order
     * @return total distance traveled
     */
    double calculateTotalDistance(List<Location> locations);

    /**
     * Determines an optimized picking path using the nearest-neighbor approach.
     * Starting from the entrance (0,0), the algorithm repeatedly selects
     * the closest remaining location until all have been visited.
     *
     * @param locations list of locations to optimize
     * @return a new list of locations ordered by the nearest-neighbor strategy
     */
    List<Location> findOptimalPickingPath(List<Location> locations);

    /**
     * Sorts a list of locations in ascending order by aisle and bay.
     * @param locations list of locations to sort
     * @return a new list of locations sorted in ascending order by aisle and bay
     */
    List<Location> getAscendingPath(List<Location> locations);

    /**
     * Extracts all unique picking locations from a single trolley.
     * Each location corresponds to a unique (aisle, bay) pair,
     * ensuring that no duplicate coordinates are included in the result.
     *
     * @param trolley the trolley containing picking items
     * @return list of unique locations derived from the trolley's picking items
     */
    List<Location> extractLocationsFromTrolley(Trolley trolley);
}
