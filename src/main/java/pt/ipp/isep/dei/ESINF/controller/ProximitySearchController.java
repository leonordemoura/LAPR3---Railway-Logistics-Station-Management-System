package pt.ipp.isep.dei.ESINF.controller;

import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.dataStruc.ProximitySearch;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.ProximitySearchService;
import pt.ipp.isep.dei.ESINF.services.impl.ProximitySearchImpl;

import java.util.List;

/**
 * Controller for USEI09 - Proximity Search (Nearest-N with Filters)
 *
 * Mediates between UI and Service layer, orchestrating proximity search operations
 */
public class ProximitySearchController {

    private final WarehouseRepository repository;
    private final ProximitySearchService service;

    /**
     * Constructor initializing repository and service
     */
    public ProximitySearchController() {
        this.repository = Repositories.getInstance().getWarehouseRepository();
        this.service = new ProximitySearchImpl();
    }

    /**
     * Gets the KD-tree for train stations.
     * The tree is built from the AVLMap (USEI06) and list of stations (USEI07).
     *
     * @return the KDTreeStation instance
     */
    public KDTree<TrainStation> getKDTree() {
        return repository.getKDTreeStation();
    }

    /**
     * Validate coordinates are within valid ranges
     *
     * @param lat latitude (-90 to 90)
     * @param lon longitude (-180 to 180)
     * @return true if valid, false otherwise
     */
    public boolean validateCoordinates(double lat, double lon) {
        return lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180;
    }

    /**
     * Find N nearest stations to target coordinate using KD-tree proximity search
     *
     * @param targetLat Target latitude
     * @param targetLon Target longitude
     * @param n Number of nearest neighbors to find
     * @param timeZoneFilter Optional timezone filter (null = no filter)
     * @return List of N nearest stations sorted by distance (ascending)
     */
    public List<ProximitySearch.StationDistance> findNearestN(
            double targetLat,
            double targetLon,
            int n,
            String timeZoneFilter) {

        KDTree<TrainStation> kdTree = getKDTree();
        return service.findNearestN(kdTree, targetLat, targetLon, n, timeZoneFilter);
    }
}