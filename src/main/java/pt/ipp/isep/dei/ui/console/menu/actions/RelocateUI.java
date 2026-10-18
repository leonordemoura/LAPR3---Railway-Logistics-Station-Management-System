package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.controller.InventoryController;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.InventoryService;
import pt.ipp.isep.dei.ESINF.services.impl.InventoryServiceImpl;
import pt.ipp.isep.dei.ui.console.utils.Utils;

public class RelocateUI implements Runnable {
    @Override
    public void run() {
        WarehouseRepository repo = Repositories.getInstance().getWarehouseRepository();
        InventoryController controller = new InventoryController();
        String boxId;
        while (true) {
            boxId = Utils.readLineFromConsole("BoxId: ");
            if (repo.boxIdExists(boxId)) break;
            System.out.println("BoxId does not exist. Please enter a valid box id.");
        }

        String newWarehouseId;
        while (true) {
            newWarehouseId = Utils.readLineFromConsole("New WarehouseId: ");
            if (repo.warehouseExists(newWarehouseId)) break;
            System.out.println("Warehouse does not exist. Please enter a valid warehouse id.");
        }

        int newAisle;
        while (true) {
            newAisle = Utils.readIntFromConsole("New Aisle: ", 0, Integer.MAX_VALUE);
            if (repo.aisleExists(newWarehouseId, newAisle)) break;
            System.out.println("Aisle does not exist in this warehouse. Please enter a valid aisle.");
        }
        int newBay;
        while (true) {
            newBay = Utils.readIntFromConsole("New Bay: ", 0, Integer.MAX_VALUE);
            if (repo.bayExists(newWarehouseId, newAisle, newBay)) break;
            System.out.println("Bay does not exist in this aisle. Please enter a valid bay.");
        }

        if (repo.isBayFull(newWarehouseId, newAisle, newBay)) {
            System.out.println("Destination bay is full. Relocation aborted.");
            return;
        }
        try {
            controller.relocateBox(boxId, newWarehouseId, newAisle, newBay);
            System.out.println("Relocation completed.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}



