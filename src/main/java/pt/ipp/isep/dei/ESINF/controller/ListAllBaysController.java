package pt.ipp.isep.dei.ESINF.controller;

import pt.ipp.isep.dei.ESINF.domain.Aisle;
import pt.ipp.isep.dei.ESINF.domain.Bay;
import pt.ipp.isep.dei.ESINF.domain.Box;
import pt.ipp.isep.dei.ESINF.domain.Warehouse;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.I_WarehouseRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller responsible for listing and accessing bays and boxes across warehouses.
 * <p>
 * Provides methods to retrieve all bays, boxes within a bay, and the list of warehouses.
 * Acts as an abstraction layer over the {@link I_WarehouseRepository}.
 * </p>
 */
public class ListAllBaysController {
    I_WarehouseRepository warehouseRepo;

    /**
     * Constructs a new {@code ListAllBaysController}.
     * Initializes the controller with the singleton warehouse repository instance.
     */
    public ListAllBaysController() {
        this.warehouseRepo = Repositories.getInstance().getWarehouseRepository();
    }

    /**
     * Returns all boxes in a specific bay of a specific aisle and warehouse.
     *
     * @param warehouseId the ID of the warehouse
     * @param aisle       the aisle number
     * @param bay         the bay number
     * @return a list of {@link Box} objects in the specified bay; may be empty if no boxes exist
     */
    public List<Box> getBoxesInBay(String warehouseId, int aisle, int bay) {
        return warehouseRepo.getBoxesInBay(warehouseId, aisle, bay);
    }

    /**
     * Returns all bays from all warehouses.
     * Uses the hierarchical structure Warehouse -> Aisle -> Bay.
     */
    public Map<String, Bay> getBays() {
        Map<String, Bay> allBays = new HashMap<>();
        Map<String, Warehouse> warehouses = warehouseRepo.getWarehouses();
        
        for (Warehouse warehouse : warehouses.values()) {
            for (Aisle aisle : warehouse.getAisles().values()) {
                for (Bay bay : aisle.getBays().values()) {
                    String key = warehouse.getWarehouseId() + "-" + aisle.getAisleNumber() + "-" + bay.getBay();
                    allBays.put(key, bay);
                }
            }
        }
        
        return allBays;
    }


    /**
     * Returns all warehouses in the system.
     *
     * @return a map of warehouse IDs to {@link Warehouse} objects
     */
    public Map<String, Warehouse> getWarehouses() {
        return warehouseRepo.getWarehouses();
    }
}
