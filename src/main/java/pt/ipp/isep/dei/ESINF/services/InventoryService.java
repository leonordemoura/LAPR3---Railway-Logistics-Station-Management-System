package pt.ipp.isep.dei.ESINF.services;

import pt.ipp.isep.dei.ESINF.domain.Box;
import java.util.List;

/**
 * Service interface that defines operations for managing warehouse inventory.
 * <p>
 * Provides methods for unloading boxes into storage bays, dispatching items,
 * relocating boxes between bays or warehouses, and retrieving the contents of a bay.
 * </p>
 *
 * <p>
 * This interface abstracts the inventory logic from the rest of the application,
 * allowing different implementations (e.g., in-memory, database).
 * </p>
 */
public interface InventoryService {

    /**
     * Unloads a box into the specified bay of a warehouse.
     * <p>
     * This method places a given {@link Box} into the indicated warehouse location
     * (defined by warehouse ID, aisle number, and bay number). Implementations should
     * validate that there is enough capacity in the target bay.
     * </p>
     *
     * @param box          the box to unload
     * @param warehouseId  the ID of the target warehouse
     * @param aisle        the aisle number within the warehouse
     * @param bay          the bay number within the aisle
     * @return {@code true} if the box was successfully unloaded, {@code false} otherwise
     */
    boolean unloadBox(Box box, String warehouseId, int aisle, int bay);

    /**
     * Dispatches a specified quantity of items identified by SKU from a warehouse.
     * <p>
     * This method removes a certain number of units from the warehouse’s stock.
     * Implementations need to determine from which boxes the items should be taken.
     * </p>
     *
     * @param sku          the SKU of the item to dispatch
     * @param qty          the quantity to dispatch
     * @param warehouseId  the ID of the warehouse from which items will be dispatched
     * @return the total quantity successfully dispatched (may be less than {@code qty} if insufficient stock)
     */
    int dispatch(String sku, int qty, String warehouseId);

    /**
     * Relocates a box to a new warehouse location.
     * <p>
     * Moves an existing box identified by its ID to a new warehouse, aisle, and bay.
     * The target bay must have available capacity to receive the box.
     * </p>
     *
     * @param boxId          the unique ID of the box to relocate
     * @param newWarehouseId the ID of the destination warehouse
     * @param newAisle       the aisle number in the destination warehouse
     * @param newBay         the bay number in the destination aisle
     */
    void relocate(String boxId, String newWarehouseId, int newAisle, int newBay);

    /**
     * Retrieves all boxes currently stored in a specific bay.
     *
     * @param warehouseId  the ID of the warehouse
     * @param aisle        the aisle number
     * @param bay          the bay number
     * @return a list of {@link Box} objects stored in the specified bay,
     *         or an empty list if the bay is empty or does not exist
     */
    List<Box> getBoxesInBay(String warehouseId, int aisle, int bay);
}
