package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.LAPR3.services.impl.DatabaseImportServiceImpl;
import pt.ipp.isep.dei.ui.console.menu.actions.*;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyst menu for accessing analysis features
 */
public class AnalystUI implements Runnable {

    /**
     * Default constructor for AnalystUI.
     */
    public AnalystUI() {}

    /**
     * Runs the analyst menu loop, displaying options and processing user input until exit.
     * Available options include proximity search and maximum flow calculations.
     */
    @Override
    public void run() {
        // Import data from database first
        try {
            DatabaseImportServiceImpl dbImportService = new DatabaseImportServiceImpl();
            dbImportService.importAllFromDatabase();
        } catch (Exception e) {
            System.out.println("ERROR: Failed to import data - " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Create menu options
        List<MenuItem> options = new ArrayList<>();

        try {
            options.add(new MenuItem("Proximity Search (USEI09)", new ProximitySearchUI()));
            options.add(new MenuItem("Maximum throughput between two Hubs (USEI14)", new MaxFlowUI()));
        } catch (Exception e) {
            System.out.println("Failed to initialize menu options: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Menu loop
        int option;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- Analyst Menu -------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}