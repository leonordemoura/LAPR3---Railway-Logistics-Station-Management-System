package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.controller.ProximitySearchController;
import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.dataStruc.ProximitySearch;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ProximitySearchUI implements Runnable {

    private final ProximitySearchController controller;
    private final Scanner scanner;

    public ProximitySearchUI() {
        this.controller = new ProximitySearchController();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        System.out.println(Utils.createHeader("USEI09: Proximity Search (Nearest-N with Filters)"));

        KDTree<TrainStation> kdTree = controller.getKDTree();
        if (kdTree == null || kdTree.isEmpty()) {
            System.out.println("ERROR: KD-tree not built. Run USEI07 first.\n");
            return;
        }

        System.out.println("KD-Tree size: " + kdTree.size() + " stations\n");

        // Input
        double targetLat = readDouble("Target latitude: ");
        double targetLon = readDouble("Target longitude: ");
        int n = readInt("Number of neighbors: ");
        String timeZoneFilter = readFilter();

        if (!controller.validateCoordinates(targetLat, targetLon)) {
            System.out.println("Invalid coordinates.\n");
            return;
        }

        // Search
        System.out.println("\nSearching...");
        long start = System.nanoTime();
        List<ProximitySearch.StationDistance> results = controller.findNearestN(
                targetLat, targetLon, n, timeZoneFilter);
        long time = (System.nanoTime() - start) / 1_000_000;

        // Results
        System.out.println("\n" + "=".repeat(100));
        System.out.println("RESULTS - Target: (" + targetLat + ", " + targetLon + ") | Found: " + results.size() + " | Time: " + time + "ms");
        System.out.println("=".repeat(100));

        if (results.isEmpty()) {
            System.out.println("No stations found.\n");
            return;
        }

        System.out.println("Stations found:");
        System.out.println("-".repeat(50));

        for (int i = 0; i < results.size(); i++) {
            TrainStation s = results.get(i).station;
            System.out.printf("%d - %s\n", i + 1, s.getStation());
        }

        System.out.println("=".repeat(100) + "\n");
    }

        private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double v = Double.parseDouble(scanner.nextLine().trim());
                if (v >= -90 && v <= 90 || v >= -180 && v <= 180) return v;
                System.out.println("Invalid range.\n");
            } catch (Exception e) {
                System.out.println("Invalid input.\n");
            }
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int v = Integer.parseInt(scanner.nextLine().trim());
                if (v > 0) return v;
                System.out.println("Must be > 0.\n");
            } catch (Exception e) {
                System.out.println("Invalid input.\n");
            }
        }
    }

    private String readFilter() {
        System.out.println("\nAvailable timezone filters:");
        System.out.println("  1 - WET/GMT");
        System.out.println("  2 - CET");
        System.out.println("  3 - EET");
        System.out.println("  0 - No filter");

        System.out.print("Select timezone filter: ");
        String input = scanner.nextLine().trim();

        switch(input) {
            case "1":
                return "WET/GMT";
            case "2":
                return "CET";
            case "3":
                return "EET";
            case "0":
                return null;
            default:
                System.out.println("Invalid option. Using no filter.\n");
                return null;
        }
    }
}