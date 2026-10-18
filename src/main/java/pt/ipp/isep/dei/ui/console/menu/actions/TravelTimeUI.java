package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.LAPR3.controller.TravelTimeController;
import pt.ipp.isep.dei.LAPR3.domain.*;
import pt.ipp.isep.dei.LAPR3.services.impl.DatabaseImportServiceImpl;

import java.util.List;
import java.util.Scanner;

public class TravelTimeUI implements Runnable {
    private final TravelTimeController controller;
    private final RailwayRepository repository;
    private final Scanner scanner;

    public TravelTimeUI() {
        this.repository = getRepository();
        this.controller = new TravelTimeController();
        this.scanner = new Scanner(System.in);
    }

    public RailwayRepository getRepository() {
        return Repositories.getInstance().getRailwayRepository();
    }

    @Override
    public void run() {
        System.out.println("\n========== USLP03: Calculate Travel Time ==========\n");

        // Check if data is loaded
        if (repository.getAllFacilities().isEmpty()) {
            System.out.println("ERROR: No data found. Please check your data source.");
            return;
        }

        try {
            // Select start facility
            Facility startFacility = selectFacility("START");
            if (startFacility == null) return;

            // Select end facility
            Facility endFacility = selectFacility("END");
            if (endFacility == null) return;

            // Select locomotive
            Locomotive locomotive = selectLocomotive();
            if (locomotive == null) return;

            // Calculate travel time
            System.out.println("\nCalculating travel time...\n");

            TravelTimeResult result = controller.calculateTravelTime(
                    startFacility.getFacilityID(),
                    endFacility.getFacilityID(),
                    locomotive.getLocomotiveId()
            );

            // Display result
            System.out.println(result.toDetailedString());

        } catch (IllegalArgumentException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n[ERROR] An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Select a facility from the list
     */
    private Facility selectFacility(String type) {
        List<Facility> facilities = repository.getAllFacilities();

        System.out.println("\nAvailable Facilities (" + type + "):");
        System.out.println("------------------------------------");

        for (Facility f : facilities) {
            System.out.printf("%2d - %s%n", f.getFacilityID(), f.getName());
        }

        int id = readInteger("\nEnter " + type + " Facility ID: ");

        Facility facility = repository.getFacilityById(id);
        if (facility == null) {
            System.out.println("ERROR: Facility with ID " + id + " not found.");
        }

        return facility;
    }

    /**
     * Select a locomotive from the list
     */
    private Locomotive selectLocomotive() {
        List<Locomotive> locomotives = repository.getAllLocomotives();

        if (locomotives.isEmpty()) {
            System.out.println("ERROR: No locomotives found.");
            return null;
        }

        System.out.println("\nAvailable Locomotives:");
        System.out.println("------------------------------------");
        System.out.printf("%-6s | %-15s | %-20s | %-10s%n", "ID", "Name", "Model", "Op Speed");
        System.out.println("----------------------------------------------------------");

        for (Locomotive l : locomotives) {
            System.out.printf("%-6d | %-15s | %-20s | %-10.3f%n",
                    l.getLocomotiveId(),
                    l.getLocomotiveName(),
                    l.getLocomotiveModelID().getVehicle().getVehicleName(),
                    l.getOperationalSpeed());
        }

        int id = readInteger("\nEnter Locomotive ID: ");

        Locomotive locomotive = repository.getLocomotiveById(id);
        if (locomotive == null) {
            System.out.println("ERROR: Locomotive with ID " + id + " not found.");
        }

        return locomotive;
    }

    /**
     * Read an integer from user input
     */
    private int readInteger(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        }
    }
}