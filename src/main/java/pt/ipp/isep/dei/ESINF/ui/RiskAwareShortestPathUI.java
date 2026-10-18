package pt.ipp.isep.dei.ESINF.ui;

import pt.ipp.isep.dei.ESINF.controller.RiskAwareShortestPathController;
import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.services.dto.RiskAwarePathResult;

import java.util.List;
import java.util.Scanner;

public class RiskAwareShortestPathUI {

    private final RiskAwareShortestPathController controller;

    public RiskAwareShortestPathUI() {
        this.controller = new RiskAwareShortestPathController();
    }

    public static void main(String[] args) {
        new RiskAwareShortestPathUI().run();
    }

    public void run() {
        List<Station> stations = controller.getAllStations();
        if (stations.isEmpty()) {
            System.out.println("No stations loaded. Import stations and lines first.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== USEI15 – Risk-Aware Shortest Path ===");
        System.out.println("Available stations:");
        for (int i = 0; i < stations.size(); i++) {
            Station s = stations.get(i);
            System.out.printf("%3d) [%d] %s%n", i + 1, s.getStationID(), s.getName());
        }

        int sourceIndex = readIndex(scanner, "Enter source station index: ", stations.size());
        int targetIndex = readIndex(scanner, "Enter target station index: ", stations.size());

        Station source = stations.get(sourceIndex - 1);
        Station target = stations.get(targetIndex - 1);

        try {
            RiskAwarePathResult result = controller.computeRiskAwareShortestPath(source, target);
            printResult(result);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private int readIndex(Scanner scanner, String prompt, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                int value = Integer.parseInt(line.trim());
                if (value >= 1 && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.printf("Please enter a number between 1 and %d.%n", max);
        }
    }

    private void printResult(RiskAwarePathResult result) {
        System.out.println();
        System.out.printf("Path from %s to %s:%n",
                result.source().getName(), result.target().getName());

        if (result.path().isEmpty()) {
            if (result.hasNegativeCycle() && result.negativeCycleAffectsTarget()) {
                System.out.println("No finite shortest path exists: a reachable negative cycle affects this route.");
            } else {
                System.out.println("No path found between the selected stations.");
            }
        } else {
            System.out.print("Stations: ");
            for (int i = 0; i < result.path().size(); i++) {
                Station s = result.path().get(i);
                if (i > 0) System.out.print(" -> ");
                System.out.print(s.getName());
            }
            System.out.println();

            System.out.println("Segments:");
            for (RiskAwarePathResult.PathSegment seg : result.segments()) {
                System.out.printf("  %s -> %s  cost=%.2f  cumulative=%.2f%n",
                        seg.from().getName(),
                        seg.to().getName(),
                        seg.segmentCost(),
                        seg.cumulativeCost());
            }

            System.out.printf("Total cost: %.2f%n", result.totalCost());
        }

        if (result.hasNegativeCycle()) {
            System.out.println();
            System.out.println("Detected negative cycle(s) reachable from the source.");
            if (result.negativeCycleAffectsTarget()) {
                System.out.println("Warning: some negative cycle can affect the source→target route.");
            } else {
                System.out.println("These cycles do NOT affect the chosen source→target route.");
            }
            if (!result.negativeCycleStations().isEmpty()) {
                System.out.print("Cycle stations: ");
                for (int i = 0; i < result.negativeCycleStations().size(); i++) {
                    Station s = result.negativeCycleStations().get(i);
                    if (i > 0) System.out.print(" -> ");
                    System.out.print(s.getName());
                }
                System.out.println();
            }
        }
    }
}
