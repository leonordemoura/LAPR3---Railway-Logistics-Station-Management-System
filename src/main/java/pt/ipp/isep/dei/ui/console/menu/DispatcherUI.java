package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.LAPR3.services.impl.DatabaseImportServiceImpl;
import pt.ipp.isep.dei.ui.console.menu.actions.TravelTimeUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * User Interface class for the Dispatcher Menu.
 * Provides menu options related to dispatcher actions such as travel time estimation.
 * Implements Runnable to allow the menu to be executed in a thread or main program flow.
 */
public class DispatcherUI implements Runnable {

    /**
     * Default constructor for DispatcherUI.
     */
    public DispatcherUI() {}

    /**
     * Runs the dispatcher menu loop, displaying options and processing user input until exit.
     * Available options include travel time estimation between station.
     */
    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Travel Time", new TravelTimeUI()));

        try {
            DatabaseImportServiceImpl dbImportService = new DatabaseImportServiceImpl();
            dbImportService.importAllFromDatabase();
        } catch (Exception e) {
            System.out.println("ERROR: Failed to import data - " + e.getMessage());
            e.printStackTrace();
            return;
        }


        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- Dispatcher Menu -------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
