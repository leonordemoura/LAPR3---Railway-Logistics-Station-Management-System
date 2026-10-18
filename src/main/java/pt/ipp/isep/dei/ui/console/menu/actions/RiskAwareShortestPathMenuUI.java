package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.controller.RiskAwareShortestPathController;
import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.services.dto.RiskAwarePathResult;

import java.util.List;
import java.util.Scanner;
public class RiskAwareShortestPathMenuUI implements Runnable {
    private final RiskAwareShortestPathController controller;
    public RiskAwareShortestPathMenuUI() { this.controller = new RiskAwareShortestPathController(); }
    @Override
    public void run() {
        List<Station> stations = controller.getAllStations();
        if (stations.isEmpty()) { System.out.println("No stations loaded. Import stations and lines first.\n"); return; }
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nUSEI15 - Shortest path (risk-aware)\n");
        for (int i = 0; i < stations.size(); i++) {
            Station s = stations.get(i);
            System.out.printf("%3d) [%d] %s%n", i + 1, s.getStationID(), s.getName());
        }
        int sourceIndex = readIndex(scanner, "From Station index: ", stations.size());
        int targetIndex = readIndex(scanner, "To Station index: ", stations.size());
        Station source = stations.get(sourceIndex - 1);
        Station target = stations.get(targetIndex - 1);
        try {
            printResult(controller.computeRiskAwareShortestPath(source, target));
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
    private int readIndex(Scanner scanner, String prompt, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= 1 && value <= max) return value;
            } catch (NumberFormatException ignored) { }
            System.out.printf("Please enter a number between 1 and %d.%n", max);
        }
    }
    private void printResult(RiskAwarePathResult result) {
        System.out.println();
        if (result.path().isEmpty()) {
            if (result.hasNegativeCycle() && result.negativeCycleAffectsTarget())
                System.out.println("No finite shortest path (negative cycle).");
            else
                System.out.println("No path between these stations.");
        } else {
            System.out.print("Path: ");
            for (int i = 0; i < result.path().size(); i++) {
                Station s = result.path().get(i);
                if (i > 0) System.out.print(" -> ");
                System.out.print(s.getName());
            }
            System.out.printf("  [total cost=%.2f]%n", result.totalCost());
        }
        if (result.hasNegativeCycle())
            System.out.println(result.negativeCycleAffectsTarget()
                    ? "WARNING: negative cycle may affect this route."
                    : "Negative cycle detected but does not affect this route.");
    }
}
