package pt.ipp.isep.dei.ESINF.services.impl;

import pt.ipp.isep.dei.ESINF.dataStruc.KDNode;
import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ESINF.services.GeographicalSearchService;

import java.util.ArrayList;
import java.util.List;

/**
 * USEI08 - Search by Geographical Area
 * <p>
 * Service implementation for searching train stations within a geographical range
 * using KD-tree range search with optional filters.
 * </p>
 */
public class GeographicalSearchServiceImpl implements GeographicalSearchService {

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
    @Override
    public List<TrainStation> searchInRange(
            KDTree<TrainStation> kdTree,
            double latMin, double latMax,
            double lonMin, double lonMax,
            String countryFilter,
            Boolean isCityFilter,
            Boolean isMainStationFilter) {

        if (kdTree == null || kdTree.isEmpty()) {
            return new ArrayList<>();
        }

        List<TrainStation> result = new ArrayList<>();
        rangeSearch(
                kdTree.getRootNode(),
                latMin, latMax,
                lonMin, lonMax,
                countryFilter,
                isCityFilter,
                isMainStationFilter,
                0,
                result);

        return result;
    }

    /**
     * Recursive range search in the KD-tree.
     * Implements the standard KD-tree range search algorithm with pruning.
     *
     * @param node current node in the KD-tree
     * @param latMin minimum latitude (inclusive)
     * @param latMax maximum latitude (inclusive)
     * @param lonMin minimum longitude (inclusive)
     * @param lonMax maximum longitude (inclusive)
     * @param countryFilter country filter: "PT", "ES", "all", or null
     * @param isCityFilter filter by isCity: true, false, or null
     * @param isMainStationFilter filter by isMainStation: true, false, or null
     * @param depth current depth in the tree (determines split dimension)
     * @param output list to collect matching stations
     */
    private void rangeSearch(
            KDNode<TrainStation> node,
            double latMin, double latMax,
            double lonMin, double lonMax,
            String countryFilter,
            Boolean isCityFilter,
            Boolean isMainStationFilter,
            int depth,
            List<TrainStation> output) {

        if (node == null) {
            return;
        }

        var coordinate = node.getCoordinate();
        double lat = coordinate.getLatitude();
        double lon = coordinate.getLongitude();

        // Determine split dimension: even depth = latitude, odd depth = longitude
        boolean splitByLatitude = (depth % 2 == 0);

        // Check if current node's coordinate is within the range
        if (lat >= latMin && lat <= latMax &&
            lon >= lonMin && lon <= lonMax) {

            // Node is in range, check all stations at this coordinate
            for (TrainStation station : node.getInfo()) {
                if (passesFilters(station, countryFilter, isCityFilter, isMainStationFilter)) {
                    output.add(station);
                }
            }
        }

        // Decide which subtrees to explore based on split dimension
        if (splitByLatitude) {
            // Splitting by latitude
            // Left subtree: lat < current lat
            if (latMin < lat) {
                rangeSearch(node.getLeft(), latMin, latMax, lonMin, lonMax,
                            countryFilter, isCityFilter, isMainStationFilter,
                            depth + 1, output);
            }
            // Right subtree: lat >= current lat
            if (latMax >= lat) {
                rangeSearch(node.getRight(), latMin, latMax, lonMin, lonMax,
                            countryFilter, isCityFilter, isMainStationFilter,
                            depth + 1, output);
            }
        } else {
            // Splitting by longitude
            // Left subtree: lon < current lon
            if (lonMin < lon) {
                rangeSearch(node.getLeft(), latMin, latMax, lonMin, lonMax,
                            countryFilter, isCityFilter, isMainStationFilter,
                            depth + 1, output);
            }
            // Right subtree: lon >= current lon
            if (lonMax >= lon) {
                rangeSearch(node.getRight(), latMin, latMax, lonMin, lonMax,
                            countryFilter, isCityFilter, isMainStationFilter,
                            depth + 1, output);
            }
        }
    }

    /**
     * Applies optional filters to a station.
     * Filters are applied according to USEI08 acceptance criteria:
     * - isCity (true | false)
     * - isMainStation (true | false)
     * - country (PT | ES | all)
     *
     * @param station the station to check
     * @param countryFilter country filter: "PT", "ES", "all", or null for all
     * @param isCityFilter filter by isCity: true, false, or null for all
     * @param isMainStationFilter filter by isMainStation: true, false, or null for all
     * @return true if station passes all filters, false otherwise
     */
    private boolean passesFilters(
            TrainStation station,
            String countryFilter,
            Boolean isCityFilter,
            Boolean isMainStationFilter) {

        // Country filter: "PT", "ES", "all", or null (all countries)
        if (countryFilter != null && !countryFilter.equalsIgnoreCase("all")) {
            if (!station.getCountry().equalsIgnoreCase(countryFilter)) {
                return false;
            }
        }

        // isCity filter: true, false, or null (all)
        if (isCityFilter != null) {
            if (station.isCity() != isCityFilter) {
                return false;
            }
        }

        // isMainStation filter: true, false, or null (all)
        if (isMainStationFilter != null) {
            if (station.isMainStation() != isMainStationFilter) {
                return false;
            }
        }

        return true;
    }
}

