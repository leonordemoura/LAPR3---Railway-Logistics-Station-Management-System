package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.controller.GeographicalSearchController;
import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;
import java.util.Scanner;

/**
 * User Interface for USEI08 - Search by Geographical Area.
 * Allows users to search train stations within a geographical range using KD-tree range search.
 */
public class GeographicalSearchUI implements Runnable {

    private final GeographicalSearchController controller;
    private final Scanner scanner;

    /**
     * Constructor that initializes the controller and scanner.
     */
    public GeographicalSearchUI() {
        this.controller = new GeographicalSearchController();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Runs the geographical search interface.
     */
    @Override
    public void run() {
        System.out.println(Utils.createHeader("USEI08: Search by Geographical Area"));

        // Check if KD-tree is available
        KDTree<TrainStation> kdTree = controller.getKDTree();
        if (kdTree == null) {
            System.out.println("\nERROR: KD-tree not built.");
            System.out.println("Please run USEI07 (Build 2D-Tree) before searching.\n");
            return;
        }
        
        if (kdTree.isEmpty()) {
            System.out.println("\nERROR: KD-tree is empty.");
            System.out.println("Run USEI07 to build a balanced KD-tree first.\n");
            return;
        }

        System.out.println("KD-Tree Statistics:");
        System.out.println("  Size: " + kdTree.size() + " nodes");
        System.out.println("  Height: " + kdTree.height());
        System.out.println();

        // Get geographical range
        double latMin = readDouble("Enter minimum latitude (-90 to 90): ", -90.0, 90.0);
        double latMax = readDouble("Enter maximum latitude (-90 to 90): ", -90.0, 90.0);
        double lonMin = readDouble("Enter minimum longitude (-180 to 180): ", -180.0, 180.0);
        double lonMax = readDouble("Enter maximum longitude (-180 to 180): ", -180.0, 180.0);

        // Validate range
        if (latMin > latMax) {
            System.out.println("Error: Minimum latitude cannot be greater than maximum latitude.");
            return;
        }
        if (lonMin > lonMax) {
            System.out.println("Error: Minimum longitude cannot be greater than maximum longitude.");
            return;
        }

        if (!controller.validateRange(latMin, latMax, lonMin, lonMax)) {
            System.out.println("Error: Invalid range parameters.");
            return;
        }

        // Get optional filters
        String countryFilter = readCountryFilter();
        Boolean isCityFilter = readBooleanFilter("Filter by isCity (true/false/Enter for all): ");
        Boolean isMainStationFilter = readBooleanFilter("Filter by isMainStation (true/false/Enter for all): ");

        // Perform search
        System.out.println("\nSearching stations...");
        long startTime = System.nanoTime();
        List<TrainStation> results = controller.searchInRange(
                latMin, latMax,
                lonMin, lonMax,
                countryFilter,
                isCityFilter,
                isMainStationFilter);
        long endTime = System.nanoTime();
        double elapsedTime = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds

        // Display results
        displayResults(results, latMin, latMax, lonMin, lonMax, elapsedTime);
    }

    /**
     * Reads a double value from the console within the specified range.
     *
     * @param prompt the prompt message
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @return the validated double value
     */
    private double readDouble(String prompt, double min, double max) {
        double value;
        while (true) {
            System.out.print(prompt);
            try {
                value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.printf("Error: Value must be between %.2f and %.2f. Please try again.\n", min, max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number format. Please try again.");
            }
        }
    }

    /**
     * Reads country filter from the console.
     *
     * @return country filter: "PT", "ES", "all", or null
     */
    private String readCountryFilter() {
        System.out.print("Filter by country (PT/ES/all/Enter for all): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }
        if (input.equalsIgnoreCase("PT") || input.equalsIgnoreCase("ES") || input.equalsIgnoreCase("all")) {
            return input.toUpperCase();
        }
        System.out.println("Invalid country filter. Using 'all'.");
        return "all";
    }

    /**
     * Reads a boolean filter from the console.
     *
     * @param prompt the prompt message
     * @return Boolean value or null if no filter
     */
    private Boolean readBooleanFilter(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }
        if (input.equalsIgnoreCase("true")) {
            return true;
        }
        if (input.equalsIgnoreCase("false")) {
            return false;
        }
        System.out.println("Invalid input. Using 'all' (no filter).");
        return null;
    }

    /**
     * Displays the search results.
     *
     * @param results the list of stations found
     * @param latMin minimum latitude
     * @param latMax maximum latitude
     * @param lonMin minimum longitude
     * @param lonMax maximum longitude
     * @param elapsedTime search time in milliseconds
     */
    private void displayResults(List<TrainStation> results, double latMin, double latMax,
                                double lonMin, double lonMax, double elapsedTime) {
        System.out.println("\n" + "=".repeat(106));
        System.out.println("SEARCH RESULTS");
        System.out.println("=".repeat(106));
        System.out.printf("Geographical Range: [%.6f, %.6f] latitude, [%.6f, %.6f] longitude\n",
                latMin, latMax, lonMin, lonMax);
        System.out.printf("Total stations found: %d\n", results.size());
        System.out.printf("Search time: %.3f ms\n", elapsedTime);
        System.out.println("=".repeat(106));

        if (results.isEmpty()) {
            System.out.println("No stations found matching the criteria.");
            return;
        }

        // Display stations
        System.out.printf("%-32s | %-5s | %-12s | %-12s | %-10s | %-12s | %-12s\n",
                "Station", "Country", "Latitude", "Longitude", "isCity", "isMainStation", "isAirport");
        System.out.println("-".repeat(106));

        for (TrainStation station : results) {
            System.out.printf("%-32s | %-5s | %-12.6f | %-12.6f | %-10s | %-12s | %-12s\n",
                    station.getStation(),
                    station.getCountry(),
                    station.getCoordinates().getLatitude(),
                    station.getCoordinates().getLongitude(),
                    station.isCity(),
                    station.isMainStation(),
                    station.isAirport());
        }

        System.out.println("=".repeat(106) + "\n");
    }
}


