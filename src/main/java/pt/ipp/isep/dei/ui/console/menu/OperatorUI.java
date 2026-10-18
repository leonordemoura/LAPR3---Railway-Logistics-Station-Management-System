package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.ui.console.menu.actions.ReturnedGoodsUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import pt.ipp.isep.dei.ui.console.menu.actions.DispatchUI;
import pt.ipp.isep.dei.ui.console.menu.actions.RelocateUI;
import pt.ipp.isep.dei.ui.console.menu.actions.StockInitializationUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Console-based UI class representing the operator menu in the system.
 * <p>
 * Provides a list of actions that an operator can perform, such as:
 * <ul>
 *     <li>Stock initialization and unloading wagons</li>
 *     <li>Dispatching items (SKU) from the warehouse</li>
 *     <li>Relocating boxes to other bays</li>
 *     <li>Managing returned goods</li>
 * </ul>
 * <p>
 * Implements {@link Runnable} so it can be executed in a separate thread or
 * called directly to display the menu and handle user interactions.
 * </p>
 */
public class OperatorUI implements Runnable {

    /**
     * Default constructor for OperatorUI.
     */
    public OperatorUI() {}

    /**
     * Displays the operator menu and handles user interaction.
     * <p>
     * Continuously shows the available menu options and executes the selected
     * action until the user exits. Uses {@link Utils#showAndSelectIndex(List, String)}
     * to prompt the user for input.
     * </p>
     * <p>
     * Each menu option corresponds to a specific {@link Runnable} action class:
     * <ul>
     *     <li>{@link StockInitializationUI} - Stock initialization / unload wagons</li>
     *     <li>{@link DispatchUI} - Dispatch SKU from warehouse</li>
     *     <li>{@link RelocateUI} - Relocate a box to another bay</li>
     *     <li>{@link ReturnedGoodsUI} - Manage returned goods</li>
     * </ul>
     */
    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Stock initialization / Unload wagons", new StockInitializationUI()));
        options.add(new MenuItem("Dispatch SKU from warehouse", new DispatchUI()));
        options.add(new MenuItem("Relocate a box to another bay", new RelocateUI()));
        options.add(new MenuItem("Manage Returned Goods", new ReturnedGoodsUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- Operator MENU -------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
