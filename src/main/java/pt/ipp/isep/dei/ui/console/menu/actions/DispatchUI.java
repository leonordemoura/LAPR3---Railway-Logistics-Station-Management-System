package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.impl.InventoryServiceImpl;
import pt.ipp.isep.dei.ui.console.utils.Utils;

/**
 * Console-based UI action for dispatching items (SKU) from a warehouse.
 * <p>
 * Implements {@link Runnable} so it can be executed as part of a menu option.
 * </p>
 * <p>
 * This action prompts the user to enter:
 * <ul>
 *     <li>Warehouse ID - must exist in the system</li>
 *     <li>SKU - must exist in the catalog</li>
 *     <li>Quantity - a positive integer to dispatch</li>
 * </ul>
 * <p>
 * Once valid input is received, it uses {@link InventoryServiceImpl#dispatch(String, int, String)}
 * to perform the dispatch operation.
 * </p>
 */
public class DispatchUI implements Runnable {
    /**
     * Executes the dispatch action.
     * <p>
     * Continuously prompts the user for a valid warehouse ID and SKU.
     * Then reads the quantity to dispatch and calls the inventory service to update stock.
     * </p>
     */
    @Override
    public void run() {
        WarehouseRepository repo = Repositories.getInstance().getWarehouseRepository();
        InventoryServiceImpl inv = new InventoryServiceImpl(repo); 

        String warehouseId;
        while (true) {
            warehouseId = Utils.readLineFromConsole("WarehouseId: ");
            if (repo.warehouseExists(warehouseId)) break;
            System.out.println("Warehouse does not exist. Please enter a valid warehouse id.");
        }

        String sku;
        while (true) {
            sku = Utils.readLineFromConsole("SKU: ");
            if (repo.itemExists(sku)) break;
            System.out.println("SKU does not exist. Please enter a valid SKU.");
        }
        int qty = Utils.readIntFromConsole("Quantity to dispatch: ", 1, Integer.MAX_VALUE);

        inv.dispatch(sku, qty, warehouseId, true);

    }
}