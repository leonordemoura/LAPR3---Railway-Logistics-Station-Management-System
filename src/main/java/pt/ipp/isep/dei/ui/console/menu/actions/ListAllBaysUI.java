package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.controller.ListAllBaysController;
import pt.ipp.isep.dei.ESINF.domain.Aisle;
import pt.ipp.isep.dei.ESINF.domain.Bay;
import pt.ipp.isep.dei.ESINF.domain.Box;
import pt.ipp.isep.dei.ESINF.domain.Warehouse;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Console-based UI action for listing all bays and their current inventory.
 * <p>
 * Implements {@link Runnable} to allow execution as part of a menu option.
 * </p>
 * <p>
 * This UI iterates through all warehouses, aisles, and bays, displaying for each bay:
 * <ul>
 *     <li>Bay number and capacity</li>
 *     <li>Number of boxes stored</li>
 *     <li>Details of each box (box ID, SKU, available quantity, expiry date, received date)</li>
 * </ul>
 * If a bay is empty, it prints "(empty)".
 * <p>
 * Data is retrieved via {@link ListAllBaysController}, which accesses the warehouse repository.
 * </p>
 */
public class ListAllBaysUI implements Runnable {

    /**
     * Executes the action to display all bays and their inventory.
     * <p>
     * Prints the structured inventory view to the console, grouped by warehouse and aisle.
     * If no warehouses are available, it prints a message and exits.
     * </p>
     */
    @Override
    public void run() {
        ListAllBaysController controller = new ListAllBaysController();

        System.out.println(Utils.createHeader("Inventory per bay (FEFO/FIFO)"));

        // Get all warehouses and iterate through their structure
        Map<String, Warehouse> warehouses = controller.getWarehouses();
        
        if (warehouses.isEmpty()) {
            System.out.println("No warehouses available.");
            return;
        }

        for (Warehouse warehouse : warehouses.values()) {
            System.out.println("\n=== Warehouse: " + warehouse.getWarehouseId() + " ===");
            
            for (Aisle aisle : warehouse.getAisles().values()) {
                System.out.println("\n--- Aisle: " + aisle.getAisleNumber() + " ---");
                
                for (Bay bay : aisle.getBays().values()) {
                    List<Box> boxes = controller.getBoxesInBay(warehouse.getWarehouseId(), aisle.getAisleNumber(), bay.getBay());
                    System.out.printf("Bay %d (cap=%d) -> %d boxes\n", bay.getBay(), bay.getCapacityBoxes(), boxes.size());
                    
                    if (boxes.isEmpty()) {
                        System.out.println("  (empty)");
                    } else {
                        for (Box bx : boxes) {
                            System.out.printf("  boxId=%s sku=%s qtyAvail=%d exp=%s recv=%s\n",
                                    bx.getBoxId(), bx.getSku(), bx.getQtyAvailable(), bx.getExpiryDate(), bx.getReceivedAt());
                        }
                    }
                }
            }
        }
    }
}



