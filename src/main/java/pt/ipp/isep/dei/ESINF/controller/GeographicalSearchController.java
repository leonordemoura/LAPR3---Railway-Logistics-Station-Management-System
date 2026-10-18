package pt.ipp.isep.dei.ESINF.controller;

import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.GeographicalSearchService;
import pt.ipp.isep.dei.ESINF.services.impl.GeographicalSearchServiceImpl;

import java.util.List;

/**
 * Controller for USEI08 - Search by Geographical Area.
 * Handles geographical range searches on train stations using KD-tree.
 */
public class GeographicalSearchController {

    private final GeographicalSearchService service;
    private final WarehouseRepository repository;

    /**
     * Constructor that initializes the service and repository.
     */
    public GeographicalSearchController() {
        this.service = new GeographicalSearchServiceImpl();
        this.repository = Repositories.getInstance().getWarehouseRepository();
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
     * Searches for stations within a geographical range with optional filters.
     *
     * @param latMin minimum latitude (inclusive)
     * @param latMax maximum latitude (inclusive)
     * @param lonMin minimum longitude (inclusive)
     * @param lonMax maximum longitude (inclusive)
     * @param countryFilter country filter: "PT", "ES", "all", or null for all countries
     * @param isCityFilter filter by isCity: true, false, or null for all
     * @param isMainStationFilter filter by isMainStation: true, false, or null for all
     * @return list of stations matching the criteria
     */
    public List<TrainStation> searchInRange(
            double latMin, double latMax,
            double lonMin, double lonMax,
            String countryFilter,
            Boolean isCityFilter,
            Boolean isMainStationFilter) {

        KDTree<TrainStation> kdTree = getKDTree();

        if (kdTree == null || kdTree.isEmpty()) {
            return List.of();
        }

        return service.searchInRange(
                kdTree,
                latMin, latMax,
                lonMin, lonMax,
                countryFilter,
                isCityFilter,
                isMainStationFilter);
    }

    /**
     * Validates that the geographical range parameters are valid.
     *
     * @param latMin minimum latitude
     * @param latMax maximum latitude
     * @param lonMin minimum longitude
     * @param lonMax maximum longitude
     * @return true if parameters are valid, false otherwise
     */
    public boolean validateRange(double latMin, double latMax, double lonMin, double lonMax) {
        if (latMin < -90 || latMin > 90 || latMax < -90 || latMax > 90) {
            return false;
        }
        if (lonMin < -180 || lonMin > 180 || lonMax < -180 || lonMax > 180) {
            return false;
        }
        if (latMin > latMax || lonMin > lonMax) {
            return false;
        }
        return true;
    }
}


