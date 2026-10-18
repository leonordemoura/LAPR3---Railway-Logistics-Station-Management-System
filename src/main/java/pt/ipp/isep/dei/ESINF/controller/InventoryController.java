package pt.ipp.isep.dei.ESINF.controller;

import pt.ipp.isep.dei.ESINF.domain.Box;
import pt.ipp.isep.dei.ESINF.repository.AuthenticationRepository;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.impl.InventoryServiceImpl;
import pt.ipp.isep.dei.ESINF.repository.Repositories;

/**
 * Controller responsible for performing inventory operations.
 * <p>
 * Acts as a bridge between the UI and the {@link InventoryServiceImpl} service,
 * providing methods to manage boxes within warehouses, including relocation.
 * </p>
 */
public class InventoryController {
    WarehouseRepository warehouseRepo = Repositories.getInstance().getWarehouseRepository();
    private final InventoryServiceImpl service = new InventoryServiceImpl(warehouseRepo);

    /**
     * Relocates a box to a different bay in the same or another warehouse.
     * <p>
     * This method delegates the actual relocation logic to the
     * {@link InventoryServiceImpl#relocate(String, String, int, int)} method.
     * </p>
     *
     * @param boxId          the unique identifier of the box to relocate
     * @param newWarehouseId the ID of the target warehouse
     * @param newAisle       the target aisle number
     * @param newBay         the target bay number
     */
    public void relocateBox(String boxId, String newWarehouseId, int newAisle, int newBay) {
        service.relocate(boxId, newWarehouseId, newAisle, newBay);
    }
}
