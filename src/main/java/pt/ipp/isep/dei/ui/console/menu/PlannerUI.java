package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.ui.console.menu.actions.*;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * User Interface class for the Planner Menu.
 * Provides menu options related to planner actions such as ordering and allocating operations, elaborate a picking plan and design a picking path.
 * Implements Runnable to allow the menu to be executed in a thread or main program flow.
 */
public class PlannerUI implements Runnable {

    /**
     * Default constructor for PlannerUI.
     */
    public PlannerUI() {}

    /**
     * Runs the player menu loop, displaying options and processing user input until exit.
     * Available options include order eligibility and allocation, design of picking plans and paths.
     */
    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Order Eligibility & Allocation", new OrderAllocationUI()));
        options.add(new MenuItem("Picking Plans", new PickingPlanUI()));
        options.add(new MenuItem("Pick Path Sequencing", new PickPathUI()));
        options.add(new MenuItem("Insert Train Stations", new AVLMenu()));
        options.add(new MenuItem("Geographical Search (USEI08)", new GeographicalSearchUI()));
        options.add(new MenuItem("Radius Search - Stations Within Distance (USEI10)", new RadiusSearchUI()));
        options.add(new MenuItem("Directed Line Upgrade Plan (USEI11)", new DirectedLineUpgradePlanUI()));
        options.add(new MenuItem("Minimal Backbone (USEI12)", new MinimalBackBoneUI()));
        options.add(new MenuItem("Rail hub Centrality Analysis (USEI13)", new RailHubAnalysisUI()));
        options.add(new MenuItem("Risk-Aware Shortest Path between Two Stations (USEI15)", new RiskAwareShortestPathMenuUI()));


        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- Planner Menu -------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
