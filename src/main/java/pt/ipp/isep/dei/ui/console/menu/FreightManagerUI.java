package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.LAPR3.services.impl.DatabaseImportServiceImpl;
import pt.ipp.isep.dei.ui.console.menu.actions.USLP07.CreateFreightUI;
import pt.ipp.isep.dei.ui.console.menu.actions.USLP07.CreateRouteUI;
import pt.ipp.isep.dei.ui.console.menu.actions.USLP07.CreateTrainUI;
import pt.ipp.isep.dei.ui.console.menu.actions.USLP07.DispatchSchedulerUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User Interface class for the Freight Manager Menu.
 * Provides menu options related to freight manager actions such as create a route, define paths and dispatch trains.
 * Implements Runnable to allow the menu to be executed in a thread or main program flow.
 */
public class FreightManagerUI implements Runnable {

    public FreightManagerUI() {
    }

    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        try {
            options.add(new MenuItem("Create a Route", new CreateRouteUI()));
            options.add(new MenuItem("Create a Freight", new CreateFreightUI()));
            options.add(new MenuItem("Create a Train", new CreateTrainUI()));
            options.add(new MenuItem("Dispatch Trains (Scheduler)", new DispatchSchedulerUI()));
        } catch (IOException e) {
            System.out.println("Error initializing route/path creation flows: " + e.getMessage());
        }

        try {
            DatabaseImportServiceImpl dbImportService = new DatabaseImportServiceImpl();
            dbImportService.importAllFromDatabase();
        } catch (Exception e) {
            System.out.println("ERROR: Failed to import data - " + e.getMessage());
            e.printStackTrace();
            return;
        }

        int option;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- Freight Manager Menu -------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
