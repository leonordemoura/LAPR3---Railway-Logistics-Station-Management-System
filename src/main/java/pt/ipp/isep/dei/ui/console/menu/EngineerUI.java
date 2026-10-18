package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.ui.console.menu.MenuItem;
import pt.ipp.isep.dei.ui.console.menu.actions.BuildTreeUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * User Interface class for the Engineer Menu.
 * Provides menu options related to engineer actions such as build a balanced tree.
 * Implements Runnable to allow the menu to be executed in a thread or main program flow.
 */
public class EngineerUI implements Runnable{
    /**
     * Default constructor for DispatcherUI.
     */
    public EngineerUI() {}

    /**
     * Runs the dispatcher menu loop, displaying options and processing user input until exit.
     * Available options include building a 2D Balanced Tree.
     */
    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Build a Balanced 2D-Tree", new BuildTreeUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- Engineer Menu -------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
