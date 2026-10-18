package pt.ipp.isep.dei.ESINF.services;

import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;

import java.util.List;

/**
 * Service responsible for constructing and managing a K-D Tree that stores
 * {@link TrainStation} objects organized by their geographic coordinates
 * (latitude and longitude).
 * <p>
 * The K-D Tree enables efficient spatial queries such as nearest-neighbor
 * searches, range searches, and other location-based operations on railway stations.
 */
public interface KDTreeStationService {

    /**
     * Builds a <strong>balanced</strong> K-D Tree from a list of train stations.
     * <p>
     * A balanced tree is constructed by sorting the stations alternately by latitude
     * and longitude at each level and choosing the median element as the splitting
     * node results in O(log n) average time for most spatial queries.
     * <p>
     * Stations with identical coordinates are stored together in the same node
     * (bucket) to handle duplicates correctly.
     *
     * @return a balanced {@link KDTree} containing all the provided stations
     * @throws IllegalArgumentException if the list is {@code null}
     */
    KDTree<TrainStation> buildBalancedTree();
}
