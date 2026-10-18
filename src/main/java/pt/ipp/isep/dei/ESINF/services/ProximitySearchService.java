package pt.ipp.isep.dei.ESINF.services;

import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.dataStruc.ProximitySearch;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;

import java.util.List;

/**
 * Service interface for USEI09 - Proximity Search (Nearest-N with Filters)
 */
public interface ProximitySearchService {

    /**
     * Find N nearest stations to target coordinate using KD-tree
     *
     * @param kdTree KD-tree of stations
     * @param targetLat Target latitude
     * @param targetLon Target longitude
     * @param n Number of nearest neighbors to find
     * @param timeZoneFilter Optional timezone filter (null = no filter)
     * @return List of N nearest stations sorted by distance (ascending)
     */
    List<ProximitySearch.StationDistance> findNearestN(
            KDTree<TrainStation> kdTree,
            double targetLat,
            double targetLon,
            int n,
            String timeZoneFilter);
}