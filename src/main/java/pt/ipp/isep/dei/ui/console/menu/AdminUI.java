package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.ui.console.menu.EngineerUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * User Interface class for the Admin Menu.
 * Provides menu options related to dispatcher, operator and planner actions.
 * Implements Runnable to allow the menu to be executed in a thread or main program flow.
 */
public class AdminUI implements Runnable{
    /**
     * Default constructor for AdminUI.
     */
    public AdminUI() {}

    /**
     * Runs the Admin menu loop, displaying options and processing user input until exit.
     * Available options include operator, planner and dispatcher menu.
     */
    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Operator Menu", new OperatorUI()));
        options.add(new MenuItem("Planner Menu", new PlannerUI()));
        options.add(new MenuItem("Dispatcher Menu", new DispatcherUI()));
        options.add(new MenuItem("Freight Manager Menu", new FreightManagerUI()));
        options.add(new MenuItem("Engineer Menu", new EngineerUI()));
        options.add(new MenuItem("Analyst Menu", new AnalystUI()));


        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- Product Owner Menu -------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
