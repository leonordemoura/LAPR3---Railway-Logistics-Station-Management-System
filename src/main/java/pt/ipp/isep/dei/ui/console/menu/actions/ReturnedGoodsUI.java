package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.controller.ReturnedGoodsController;
import pt.ipp.isep.dei.ESINF.domain.ReturnedGoods;

import java.util.List;

/**
 * Console-based UI class responsible for displaying the list of returned goods.
 *
 * This class serves as a simple text interface that:
 *  - Calls the ReturnedGoodsController to process all returned goods.
 *  - Displays the result (each returned item) in the console.
 *
 * It implements the Runnable interface so it can be executed as a separate
 * task or thread from a menu or command execution system.
 */
public class ReturnedGoodsUI implements Runnable {

    // Controller responsible for handling business logic related to returned goods
    private final ReturnedGoodsController controller = new ReturnedGoodsController();

    /**
     * This method is called when the UI action is executed.
     * It retrieves processed returned goods from the controller and prints
     * their details to the console in a formatted way.
     */
    @Override
    public void run() {

        // Get the list of processed returned goods from the controller
        List<ReturnedGoods> returns = controller.manageReturnedGoods();

        // Loop through each returned item and display its information
        for (ReturnedGoods ret : returns) {

            // Print basic return details: timestamp, return ID, SKU
            System.out.printf(" %s | returnId=%s | sku=%s | action=",
                    ret.getTimestamp(), ret.getReturnId(), ret.getSku());

            // Print whether the item was restocked or discarded
            if (ret.getActionTaken()) {
                System.out.print("Restocked |");
            } else {
                System.out.print("Discarded |");
            }

            // Print the quantity of the returned item
            System.out.printf(" quantity=%d\n", ret.getQty());
        }
    }
}
