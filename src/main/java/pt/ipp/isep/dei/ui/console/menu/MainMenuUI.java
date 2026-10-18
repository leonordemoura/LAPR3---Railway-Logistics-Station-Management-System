package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.LAPR3.services.impl.DatabaseImportServiceImpl;
import pt.ipp.isep.dei.ui.console.DevTeamUI;
import pt.ipp.isep.dei.ui.console.authorization.AuthenticationUI;
import pt.ipp.isep.dei.ui.console.menu.actions.LoadDataUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * User Interface class for the Main Menu.
 * Provides options to perform login and view the development team information.
 * Implements Runnable to allow execution in a separate thread or main program flow.
 */
public class MainMenuUI implements Runnable {

    /**
     * Default constructor for MainMenuUI.
     */
    public MainMenuUI() {
    }

    /**
     * Runs the main menu loop, displaying options and handling user input until exit.
     * The menu options include loading CSV data, user login and viewing the development team.
     */
    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Load Data (CSV Files)", new LoadDataUI()));
        options.add(new MenuItem("Do Login", new AuthenticationUI()));
        options.add(new MenuItem("Know the Development Team", new DevTeamUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- MAIN MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
