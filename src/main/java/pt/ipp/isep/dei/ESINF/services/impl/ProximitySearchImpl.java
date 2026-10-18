package pt.ipp.isep.dei.ESINF.services.impl;

import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.dataStruc.ProximitySearch;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ESINF.services.ProximitySearchService;

import java.util.List;

/**
 * USEI09 - Proximity Search (Nearest-N with Filters)
 *
 * Service implementation for finding N nearest train stations to a target coordinate
 * using KD-tree with optional timezone filters.
 */
public class ProximitySearchImpl implements ProximitySearchService {

    /**
     * Find N nearest stations to target coordinate using KD-tree
     * Delegates to ProximitySearch.findNearestN() static method
     *
     * @param kdTree KD-tree of stations
     * @param targetLat Target latitude
     * @param targetLon Target longitude
     * @param n Number of nearest neighbors to find
     * @param timeZoneFilter Optional timezone filter (null = no filter)
     * @return List of N nearest stations sorted by distance (ascending)
     */
    @Override
    public List<ProximitySearch.StationDistance> findNearestN(
            KDTree<TrainStation> kdTree,
            double targetLat,
            double targetLon,
            int n,
            String timeZoneFilter) {

        return ProximitySearch.findNearestN(kdTree, targetLat, targetLon, n, timeZoneFilter);
    }
}