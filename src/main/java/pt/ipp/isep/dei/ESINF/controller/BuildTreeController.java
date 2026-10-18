package pt.ipp.isep.dei.ESINF.controller;

import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.KDTreeStationService;
import pt.ipp.isep.dei.ESINF.services.impl.KDTreeStationImpl;

import java.util.List;

/**
 * Controller responsible for orchestrating the construction of a balanced K-D Tree
 * containing all train stations in the system.
 * <p>
 * This controller acts as a bridge between the data layer (repository) and the
 * service layer that builds the spatial index (K-D Tree). It is typically used
 * during application startup or when the station dataset is updated.
 */
public class BuildTreeController {
    /** Service that builds the balanced K-D Tree */
    private KDTreeStationService service;
    /** Repository providing access to the persisted train station data */
    private WarehouseRepository warehouseRepository;

    /**
     * Constructs a new controller instance.
     * <p>
     * Initializes the service and repository using dependency lookup via the
     * singleton {@link Repositories} class.
     */
    public BuildTreeController() {
        this.service = new KDTreeStationImpl();
        this.warehouseRepository = Repositories.getInstance().getWarehouseRepository();
    }

    /**
     * Retrieves the complete list of train stations currently stored in the system.
     *
     * @return an unmodifiable or copied list of all {@link TrainStation} objects
     */
    public List<TrainStation> getTrainStations() {
        return warehouseRepository.getTrainStations();
    }

    /**
     * Builds and returns a balanced K-D Tree containing the given train stations.
     * <p>
     * The tree is constructed using a median-of-medians approach (or sorting + median selection)
     * to ensure O(log n) average depth, which is crucial for efficient spatial queries
     * such as nearest station search or range queries.
     *
     * @return a balanced {@link KDTree} with all provided stations
     * @throws IllegalArgumentException if {@code stations} is {@code null}
     */
    public KDTree<TrainStation> buildBalancedTree() {
        return service.buildBalancedTree();
    }
}
