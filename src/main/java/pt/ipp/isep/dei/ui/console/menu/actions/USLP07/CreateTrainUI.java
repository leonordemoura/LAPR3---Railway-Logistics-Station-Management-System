package pt.ipp.isep.dei.ui.console.menu.actions.USLP07;

import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.LAPR3.controller.CreateTrainController;
import pt.ipp.isep.dei.LAPR3.domain.Freight;
import pt.ipp.isep.dei.LAPR3.domain.Locomotive;
import pt.ipp.isep.dei.LAPR3.domain.RailwayRepository;
import pt.ipp.isep.dei.LAPR3.domain.Route;
import pt.ipp.isep.dei.LAPR3.domain.Train;
import pt.ipp.isep.dei.LAPR3.services.dto.LocomotiveAvailabilityDTO;
import pt.ipp.isep.dei.LAPR3.services.dto.WagonAvailabilityDTO;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CreateTrainUI implements Runnable {

    private final CreateTrainController controller;
    private final RailwayRepository repository;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    public CreateTrainUI() {
        this.controller = new CreateTrainController();
        this.repository = Repositories.getInstance().getRailwayRepository();
        DATE_FORMAT.setLenient(false);
    }

    @Override
    public void run() {
        System.out.println(Utils.createHeader("USLP07/USLP09 - Create Train & Assemble Train to Route"));

        // Step 1: Select Route
        Route route = selectRoute();
        if (route == null) {
            System.out.println("Operation cancelled.");
            return;
        }

        // Step 2: Select Locomotives (with USLP09 enhanced display)
        List<Locomotive> locomotives = selectLocomotives(route);
        if (locomotives == null || locomotives.isEmpty()) {
            System.out.println("At least one locomotive is required. Operation cancelled.");
            return;
        }

        // Step 3: Display Wagons/Carriages availability (USLP09 requirement)
        displayWagonsAvailability(route);
        
        // Step 4: Select Freights (wagons are selected via freights)
        List<Freight> freights = selectFreights(route);
        if (freights == null || freights.isEmpty()) {
            System.out.println("At least one freight is required. Operation cancelled.");
            return;
        }

        // Step 5: Enter Start Date
        Date startDate = enterStartDate();
        if (startDate == null) {
            System.out.println("Operation cancelled.");
            return;
        }

        // Step 6: Confirm and Create
        System.out.println("\n--- Train Summary ---");
        System.out.println("Route: " + route.getStartFacility().getName() + " ➝ " + route.getEndFacility().getName());
        System.out.println("Locomotives: " + locomotives.size());
        for (Locomotive loc : locomotives) {
            String name = loc.getLocomotiveName();
            System.out.println("  - " + (name != null && !name.isEmpty() ? name : "Nameless")
                    + " (ID: " + loc.getLocomotiveId() + ")");
        }
        int totalWagons = freights.stream().mapToInt(f -> f.getWagons().size()).sum();
        System.out.println("Freights: " + freights.size() + " (Total Wagons: " + totalWagons + ")");
        for (Freight freight : freights) {
            String departureInfo = freight.getDepartureDate() != null
                    ? DATE_FORMAT.format(freight.getDepartureDate())
                    : "Unscheduled";
            System.out.println("  - Freight " + freight.getFreightID() + " | Departure: " + departureInfo
                    + " | Wagons: " + freight.getWagons().size());
        }
        System.out.println("Start Date: " + DATE_FORMAT.format(startDate));

        // Validate before asking for confirmation
        List<String> validationErrors = controller.validateTrainCreation(route, locomotives, freights, startDate);
        if (!validationErrors.isEmpty()) {
            System.out.println("\n✗ Validation errors found. Cannot create train:");
            for (String error : validationErrors) {
                System.out.println("  - " + error);
            }
            System.out.println("\nPlease correct the issues and try again.");
            return;
        }

        String confirm = Utils.readLineFromConsole("\nConfirm creation? (Y/N): ");
        if (!"Y".equalsIgnoreCase(confirm)) {
            System.out.println("Operation cancelled.");
            return;
        }

        // Create the train
        try {
            int requestedLocomotives = locomotives.size();
            int requestedFreights = freights.size();
            int requestedWagons = freights.stream().mapToInt(f -> f.getWagons().size()).sum();

            Train train = controller.createTrain(route, locomotives, freights, startDate);
            if (train != null) {
                System.out.println("\n✓ Train created successfully!");
                System.out.println("Train ID: " + train.getTrainID());
                
                int actualLocomotives = train.getLocomotives().size();
                System.out.println("Locomotives: " + actualLocomotives + " (requested: " + requestedLocomotives + ")");
                if (actualLocomotives < requestedLocomotives) {
                    System.out.println("  ⚠ Note: Some locomotives were duplicates and were not added (same locomotive ID cannot be added twice).");
                }
                
                int actualFreights = train.getFreights().size();
                System.out.println("Freights: " + actualFreights + " (requested: " + requestedFreights + ")");
                int actualWagonCount = train.getFreights().stream()
                        .mapToInt(f -> f.getWagons().size())
                        .sum();
                System.out.println("Wagons transported: " + actualWagonCount + " (requested total: " + requestedWagons + ")");
                if (actualFreights < requestedFreights) {
                    System.out.println("  ⚠ Note: Some freights could not be assigned. Ensure they are not already linked to another train.");
                }
            } else {
                System.out.println("\n✗ Failed to create train. Please check the logs for details.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\n✗ Error: " + e.getMessage());
        }
    }

    private Route selectRoute() {
        List<Route> routes = controller.getAllRoutes();
        if (routes.isEmpty()) {
            System.out.println("No routes available. Please create a route first.");
            return null;
        }

        System.out.println("\n--- Select Route ---");
        int option = Utils.showAndSelectIndex(routes, "Available Routes:");
        if (option == -1) {
            return null;
        }
        return routes.get(option);
    }

    private List<Locomotive> selectLocomotives(Route route) {
        // Get locomotives using PL/SQL functions (USLP09 requirement)
        List<LocomotiveAvailabilityDTO> inTransit = route != null ? controller.getLocomotivesInTransit(route) : new ArrayList<>();
        List<LocomotiveAvailabilityDTO> parked = route != null ? controller.getLocomotivesParked(route) : new ArrayList<>();
        
        // Also get from repository for backward compatibility
        List<Locomotive> availableLocomotives = controller.getAvailableLocomotives();
        
        if (availableLocomotives.isEmpty() && inTransit.isEmpty() && parked.isEmpty()) {
            System.out.println("\nNo available locomotives. All locomotives are already assigned to trains.");
            return null;
        }

        System.out.println("\n--- Select Locomotives (USLP09 - showing transit/parked status) ---");
        
        // Always display locomotives in transit if they exist (USLP09 requirement)
        if (!inTransit.isEmpty()) {
            System.out.println("\n--- Locomotives in Service (In Transit) ---");
            System.out.println("These locomotives are currently assigned to running trains.");
            System.out.printf("%-8s | %-20s | %-15s | %-30s%n", 
                    "ID", "Name", "Op Speed", "Final Destination (where it will be left)");
            System.out.println("--------------------------------------------------------------------------------");
            for (LocomotiveAvailabilityDTO loc : inTransit) {
                System.out.printf("%-8d | %-20s | %-15.2f | %-30s%n",
                        loc.getLocomotiveID(),
                        loc.getName() != null && !loc.getName().isEmpty() ? loc.getName() : "Nameless",
                        loc.getOperationalSpeed(),
                        loc.getFinalDestination() != null ? loc.getFinalDestination().getName() : "Unknown");
            }
        } else {
            System.out.println("\n--- Locomotives in Service (In Transit) ---");
            System.out.println("No locomotives currently in transit.");
        }
        
        // Always display locomotives parked if they exist (USLP09 requirement)
        if (!parked.isEmpty()) {
            System.out.println("\n--- Locomotives Available (Parked) ---");
            System.out.println("These locomotives are available and parked at stations (ordered by distance from route start - descending).");
            System.out.printf("%-8s | %-20s | %-15s | %-30s | %-15s%n", 
                    "ID", "Name", "Op Speed", "Current Station", "Distance (km)");
            System.out.println("------------------------------------------------------------------------------------------------");
            for (LocomotiveAvailabilityDTO loc : parked) {
                System.out.printf("%-8d | %-20s | %-15.2f | %-30s | %-15.2f%n",
                        loc.getLocomotiveID(),
                        loc.getName() != null && !loc.getName().isEmpty() ? loc.getName() : "Nameless",
                        loc.getOperationalSpeed(),
                        loc.getCurrentStation() != null ? loc.getCurrentStation().getName() : "Unknown",
                        loc.getDistanceFromStart() / 1000.0); // Convert meters to km
            }
        } else {
            System.out.println("\n--- Locomotives Available (Parked) ---");
            System.out.println("No locomotives available (all are in transit).");
        }
        
        // Fallback: show traditional list if both lists are empty but repository has locomotives
        if (inTransit.isEmpty() && parked.isEmpty() && !availableLocomotives.isEmpty()) {
            System.out.println("\n--- All Locomotives (fallback display) ---");
            System.out.printf("%-6s | %-20s | %-15s | %-10s | %-10s%n", 
                    "ID", "Name", "Model", "Power (kW)", "Op Speed");
            System.out.println("--------------------------------------------------------------------------------");
            
            for (Locomotive loc : availableLocomotives) {
                double power = loc.getLocomotiveModelID() != null ? 
                        loc.getLocomotiveModelID().getPower() : 0.0;
                System.out.printf("%-6d | %-20s | %-15s | %-10.2f | %-10.2f%n",
                        loc.getLocomotiveId(),
                        loc.getLocomotiveName() != null && !loc.getLocomotiveName().isEmpty() ? loc.getLocomotiveName() : "Nameless",
                        loc.getLocomotiveModelID() != null ? 
                                loc.getLocomotiveModelID().getVehicle().getVehicleName() : "N/A",
                        power,
                        loc.getOperationalSpeed());
            }
        }

        String input = Utils.readLineFromConsole("\nEnter locomotive IDs separated by comma (at least one required): ");
        if (input == null || input.isBlank()) {
            return null;
        }

        String[] tokens = input.split(",");
        List<Integer> ids = new ArrayList<>();
        for (String token : tokens) {
            try {
                ids.add(Integer.parseInt(token.trim()));
            } catch (NumberFormatException e) {
                System.out.printf("Invalid locomotive ID '%s' ignored.%n", token.trim());
            }
        }

        // Build a map of all locomotives from repository for lookup
        List<Locomotive> allLocomotivesFromRepo = repository.getAllLocomotives();
        
        List<Locomotive> selected = new ArrayList<>();
        for (Integer id : ids) {
            // Check if ID exists in inTransit or parked lists
            boolean foundInTransit = inTransit.stream().anyMatch(l -> l.getLocomotiveID() == id);
            boolean foundParked = parked.stream().anyMatch(l -> l.getLocomotiveID() == id);
            
            if (foundInTransit || foundParked) {
                // Find the locomotive in repository
                Locomotive loc = allLocomotivesFromRepo.stream()
                        .filter(l -> l.getLocomotiveId() == id)
                        .findFirst()
                        .orElse(null);
                if (loc != null) {
                    selected.add(loc);
                } else {
                    System.out.printf("Locomotive with ID %d found in availability list but not in repository.%n", id);
                }
            } else {
                // Also check in availableLocomotives for backward compatibility
                Locomotive loc = availableLocomotives.stream()
                        .filter(l -> l.getLocomotiveId() == id)
                        .findFirst()
                        .orElse(null);
                if (loc != null) {
                    selected.add(loc);
                } else {
                    System.out.printf("Locomotive with ID %d not found in available lists.%n", id);
                }
            }
        }

        return selected;
    }

    /**
     * Displays wagons/carriages availability with distinction between in transit and parked (USLP09 requirement).
     * This allows the Traffic Manager to see all available carriages before selecting freights.
     *
     * @param route The route to check wagons for
     */
    private void displayWagonsAvailability(Route route) {
        // Get wagons using PL/SQL functions (USLP09 requirement)
        List<WagonAvailabilityDTO> wagonsInTransit = route != null ? controller.getWagonsInTransit(route) : new ArrayList<>();
        List<WagonAvailabilityDTO> wagonsParked = route != null ? controller.getWagonsParked(route) : new ArrayList<>();

        System.out.println("\n--- Carriages/Wagons Availability (USLP09 - showing transit/parked status) ---");

        // Display wagons in transit if they exist (USLP09 requirement)
        if (!wagonsInTransit.isEmpty()) {
            System.out.println("\n--- Carriages/Wagons in Service (In Transit) ---");
            System.out.println("These carriages are currently assigned to trains via freights.");
            System.out.printf("%-8s | %-15s | %-30s%n",
                    "ID", "Payload (tons)", "Final Destination (where it will be left)");
            System.out.println("--------------------------------------------------------------------------------");
            for (WagonAvailabilityDTO wagon : wagonsInTransit) {
                System.out.printf("%-8d | %-15.2f | %-30s%n",
                        wagon.getWagonID(),
                        wagon.getPayloadTons(),
                        wagon.getFinalDestination() != null ? wagon.getFinalDestination().getName() : "Unknown");
            }
        } else {
            System.out.println("\n--- Carriages/Wagons in Service (In Transit) ---");
            System.out.println("No carriages currently in transit.");
        }

        // Display wagons parked if they exist (USLP09 requirement)
        if (!wagonsParked.isEmpty()) {
            System.out.println("\n--- Carriages/Wagons Available (Parked) ---");
            System.out.println("These carriages are available and parked at stations (ordered by distance from route start - descending).");
            System.out.printf("%-8s | %-15s | %-30s | %-15s%n",
                    "ID", "Payload (tons)", "Current Station", "Distance (km)");
            System.out.println("------------------------------------------------------------------------------------------------");
            for (WagonAvailabilityDTO wagon : wagonsParked) {
                System.out.printf("%-8d | %-15.2f | %-30s | %-15.2f%n",
                        wagon.getWagonID(),
                        wagon.getPayloadTons(),
                        wagon.getCurrentStation() != null ? wagon.getCurrentStation().getName() : "Unknown",
                        wagon.getDistanceFromStart() / 1000.0); // Convert meters to km
            }
        } else {
            System.out.println("\n--- Carriages/Wagons Available (Parked) ---");
            System.out.println("No carriages available (all are in transit).");
        }

        System.out.println("\nNote: Carriages are assigned to trains via freights. The above list shows all available carriages.");
    }

    private List<Freight> selectFreights(Route route) {
        List<Freight> availableFreights = controller.getUnassignedFreights();
        if (availableFreights.isEmpty()) {
            System.out.println("\nNo unassigned freights available. Please create new freights or unassign existing ones.");
            return null;
        }

        System.out.println("\n--- Select Freights (only freights not yet assigned to a train) ---");
        
        System.out.println("\n--- Available Freights ---");
        System.out.printf("%-8s | %-20s | %-20s | %-10s | %-10s%n", "ID", "Route", "Departure", "Wagons", "Train");
        System.out.println("---------------------------------------------------------------------------------------------");

        for (Freight freight : availableFreights) {
            String routeLabel;
            if (freight.getRoute() != null) {
                routeLabel = freight.getRoute().getStartFacility().getName() + " ➝ " + freight.getRoute().getEndFacility().getName();
            } else if (freight.getStartFacility() != null && freight.getEndFacility() != null) {
                routeLabel = freight.getStartFacility().getName() + " ➝ " + freight.getEndFacility().getName();
            } else {
                routeLabel = "Unassigned";
            }
            String departureLabel = freight.getDepartureDate() != null
                    ? DATE_FORMAT.format(freight.getDepartureDate())
                    : "Unscheduled";
            String trainLabel = freight.getTrain() != null
                    ? String.valueOf(freight.getTrain().getTrainID())
                    : "Unassigned";
            System.out.printf("%-8d | %-20s | %-20s | %-10d | %-10s%n",
                    freight.getFreightID(),
                    routeLabel,
                    departureLabel,
                    freight.getWagons().size(),
                    trainLabel);
        }

        String input = Utils.readLineFromConsole("\nEnter freight IDs separated by comma (at least one required): ");
        if (input == null || input.isBlank()) {
            return null;
        }

        String[] tokens = input.split(",");
        List<Freight> selected = new ArrayList<>();

        for (String token : tokens) {
            try {
                int id = Integer.parseInt(token.trim());
                Freight freight = availableFreights.stream()
                        .filter(f -> f.getFreightID() == id)
                        .findFirst()
                        .orElse(null);
                if (freight != null && selected.stream().noneMatch(f -> f.getFreightID() == id)) {
                    selected.add(freight);
                } else if (freight == null) {
                    System.out.printf("Freight with ID %d not available.%n", id);
                } else {
                    System.out.printf("Freight with ID %d already selected.%n", id);
                }
            } catch (NumberFormatException e) {
                System.out.printf("Invalid freight ID '%s' ignored.%n", token.trim());
            }
        }

        return selected;
    }

    private Date enterStartDate() {
        while (true) {
            try {
                String input = Utils.readLineFromConsole("\nEnter start date and time (dd-MM-yyyy HH:mm): ");
                if (input == null || input.isBlank()) {
                    return null;
                }
                return DATE_FORMAT.parse(input.trim());
            } catch (ParseException e) {
                System.out.println("Invalid format! Please use the format dd-MM-yyyy HH:mm (e.g., 25-12-2025 14:30).");
            }
        }
    }
}

