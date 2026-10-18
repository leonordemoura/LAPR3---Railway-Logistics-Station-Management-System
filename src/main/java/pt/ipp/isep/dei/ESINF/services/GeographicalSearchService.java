package pt.ipp.isep.dei.ESINF.services;

import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;

import java.util.List;

/**
 * Service interface for USEI08 - Search by Geographical Area.
 * Provides methods to search train stations within a geographical range using KD-tree.
 */
public interface GeographicalSearchService {

    /**
     * Searches for all stations within a geographical range with optional filters.
     *
     * @param kdTree the KD-tree containing train stations
     * @param latMin minimum latitude (inclusive)
     * @param latMax maximum latitude (inclusive)
     * @param lonMin minimum longitude (inclusive)
     * @param lonMax maximum longitude (inclusive)
     * @param countryFilter country filter: "PT", "ES", "all", or null for all countries
     * @param isCityFilter filter by isCity: true, false, or null for all
     * @param isMainStationFilter filter by isMainStation: true, false, or null for all
     * @return list of stations matching the criteria
     */
    List<TrainStation> searchInRange(
            KDTree<TrainStation> kdTree,
            double latMin, double latMax,
            double lonMin, double lonMax,
            String countryFilter,
            Boolean isCityFilter,
            Boolean isMainStationFilter);
}


