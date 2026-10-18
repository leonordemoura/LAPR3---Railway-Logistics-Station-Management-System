package pt.ipp.isep.dei.ESINF.controller;

import pt.ipp.isep.dei.ESINF.domain.Location;
import pt.ipp.isep.dei.ESINF.domain.Trolley;
import pt.ipp.isep.dei.ESINF.services.PickingPathService;
import pt.ipp.isep.dei.ESINF.services.impl.PickingPathServiceImpl;

import java.util.List;

/**
 * Controller for USEI04 - Picking Path functionality.
 * Handles the creation of picking paths.
 */
public class PickingPathController {
    private final PickingPathService pickingPathService;

    public PickingPathController() {
        this.pickingPathService = new PickingPathServiceImpl();
    }

    /**
     * Extracts all unique picking locations from a list of trolleys.
     *
     * @param trolley list of trolleys
     * @return list of unique locations
     */
    public List<Location> getLocationsForTrolley(Trolley trolley) {
        return pickingPathService.extractLocationsFromTrolley(trolley);
    }

    /**
     * Returns locations sorted in ascending order (by aisle and bay).
     *
     * @param locations list of locations to sort
     * @return sorted list
     */
    public List<Location> getAscendingPath(List<Location> locations) {
        return pickingPathService.getAscendingPath(locations);
    }

    /**
     * Returns an optimized picking path using nearest-neighbor strategy.
     *
     * @param locations list of locations to optimize
     * @return optimized list
     */
    public List<Location> getOptimalPath(List<Location> locations) {
        return pickingPathService.findOptimalPickingPath(locations);
    }

    /**
     * Calculates total distance for a given path of locations.
     *
     * @param locations list of locations
     * @return total distance
     */
    public double calculateTotalDistance(List<Location> locations) {
        return pickingPathService.calculateTotalDistance(locations);
    }



}
