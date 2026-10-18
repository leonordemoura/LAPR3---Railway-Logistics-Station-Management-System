package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.controller.MaxFlowController;
import pt.ipp.isep.dei.ESINF.dataStruc.MaxFlow;
import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * UI para cálculo de fluxo máximo entre duas estações
 */
public class MaxFlowUI implements Runnable {

    private final MaxFlowController controller;
    private final Scanner scanner;

    public MaxFlowUI() {
        this.controller = new MaxFlowController();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        System.out.println("===== CALCULATE MAXIMUM FLOW BETWEEN TWO STATIONS =====");

        Station sourceStation = selectStation("\nChoose the source station: ");
        if (sourceStation == null) {
            System.out.println("Operation cancelled.");
            return;
        }

        Station destStation = selectStation("\nChoose the destination station: ");
        if (destStation == null) {
            System.out.println("Operation cancelled.");
            return;
        }

        if (sourceStation.equals(destStation)) {
            System.out.println("Source and destination stations must be different. Operation cancelled.");
            return;
        }

        try {
            MaxFlow.MaxFlowResult<Station> result = controller.calculateMaxFlow(sourceStation, destStation);
            displayMaxFlowResult(result);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unable to calculate maximum flow.");
            System.out.println("Error: " + e.getMessage());
        }
    }
    private Station selectStation(String label) {
        try {
            ArrayList<Station> stations = controller.getAllStations();

            if (stations == null || stations.isEmpty()) {
                System.out.println("No stations available. Please load data first.");
                return null;
            }

            System.out.println(label);
            for (int i = 0; i < stations.size(); i++) {
                System.out.printf("%d - %s%n", i + 1, stations.get(i).getName());
            }

            System.out.print("Enter the number of the station (or 0 to cancel): ");
            int option = scanner.nextInt();
            scanner.nextLine();

            if (option == 0) {
                return null;
            }

            if (option < 1 || option > stations.size()) {
                System.out.println("Invalid option.");
                return null;
            }

            return stations.get(option - 1);
        } catch (Exception e) {
            System.out.println("Error loading stations: " + e.getMessage());
            return null;
        }
    }

    /**
     * Exibe o resultado do cálculo de fluxo máximo
     *
     * @param result resultado do algoritmo Edmonds-Karp
     */
    private void displayMaxFlowResult(MaxFlow.MaxFlowResult<Station> result) {
        System.out.println("\n===== MAXIMUM FLOW RESULT =====");
        System.out.println("Source: " + result.source.getName());
        System.out.println("Destination: " + result.target.getName());
        System.out.println("Maximum Flow Value: " + result.maxFlowValue);
    }

}