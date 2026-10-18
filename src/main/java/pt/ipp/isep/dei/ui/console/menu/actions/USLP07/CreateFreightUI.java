package pt.ipp.isep.dei.ui.console.menu.actions.USLP07;

import pt.ipp.isep.dei.LAPR3.controller.CreateFreightController;
import pt.ipp.isep.dei.LAPR3.domain.Facility;
import pt.ipp.isep.dei.LAPR3.domain.Freight;
import pt.ipp.isep.dei.LAPR3.domain.Wagon;
import pt.ipp.isep.dei.LAPR3.services.dto.WagonAvailabilityDTO;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CreateFreightUI implements Runnable {

    private final CreateFreightController controller;

    public CreateFreightUI() {
        this.controller = new CreateFreightController();
    }

    @Override
    public void run() {
        System.out.println(Utils.createHeader("USLP07 - Create Freight"));

        Facility startEndPoint = selectEndPoint("\nChoose the start facility: ");
        if (startEndPoint == null) {
            return;
        }

        Facility endEndPoint = selectEndPoint("\nChoose the end facility: ");
        if (endEndPoint == null) {
            return;
        }

        if (startEndPoint.getFacilityID() == endEndPoint.getFacilityID()) {
            System.out.println("Start and end facilities must be different. Operation cancelled.");
            return;
        }

        List<Wagon> wagons = selectWagons(startEndPoint);
        if (wagons == null || wagons.isEmpty()) {
            System.out.println("At least one wagon is required. Operation cancelled.");
            return;
        }

        System.out.println("\n--- Freight Summary ---");
        System.out.println("Freight ID will be assigned automatically.");
        System.out.println("Route and departure will be defined later when assigning the freight to a train.");
        System.out.println("Endpoints: " + startEndPoint.getName() + " ➝ " + endEndPoint.getName());
        System.out.println("Wagons (" + wagons.size() + "):");
        for (Wagon wagon : wagons) {
            System.out.println("  - Wagon ID: " + wagon.getWagonID()
                    + " | Model: " + (wagon.wagonModel() != null ? wagon.wagonModel().getVehicle().getVehicleName() : "N/A")
                    + " | Operator: " + (wagon.getOperator() != null ? wagon.getOperator().getOperatorName() : "N/A"));
        }

        String confirm = Utils.readLineFromConsole("\nConfirm creation? (Y/N): ");
        if (!"Y".equalsIgnoreCase(confirm)) {
            System.out.println("Operation cancelled.");
            return;
        }

        Freight freight = controller.createFreight(startEndPoint, endEndPoint, wagons);
        if (freight != null) {
            System.out.println("\n✓ Freight created successfully! ID: " + freight.getFreightID());
            System.out.println("Reminder: assign this freight to a train to set its route and departure date.");
        } else {
            System.out.println("\n✗ Failed to create freight. Please check logs for details.");
        }
    }

    private Facility selectEndPoint(String label) {
        Facility facility = selectFacility(label);
        if (facility == null) {
            return null;
        }

        Facility endPoint = controller.getEndPointForFacility(facility.getFacilityID());
        if (endPoint == null) {
            System.out.println("No endpoint found for selected facility. Please ensure database data is loaded.");
        }
        return endPoint;
    }

    private Facility selectFacility(String label) {
        List<Facility> facilities = controller.getFacilities();
        if (facilities.isEmpty()) {
            System.out.println("No facilities available. Please import data first.");
            return null;
        }

        int option = Utils.showAndSelectIndex(facilities, label);
        if (option == -1) {
            System.out.println("Operation cancelled.");
            return null;
        }
        return facilities.get(option);
    }

    private List<Wagon> selectWagons(Facility startFacility) {
        List<Wagon> availableWagons = controller.getAvailableWagons(startFacility);
        
        // Show wagons already in use, grouped by freight (USLP09 requirement)
        List<Freight> freightsWithWagons = controller.getFreightsWithWagons();
        if (!freightsWithWagons.isEmpty()) {
            System.out.println("\n--- Wagons Already in Use (assigned to other freights) ---");
            System.out.println("Wagons are grouped by freight and sorted by freight ID.\n");
            
            for (Freight freight : freightsWithWagons) {
                System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════════════════");
                System.out.println("Freight ID: " + freight.getFreightID() + 
                        (freight.hasEndpoints() ? 
                                " | Route: " + freight.getStartFacility().getName() + " ➝ " + freight.getEndFacility().getName() : 
                                " | Route: Not assigned"));
                System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════════════════");
                System.out.printf("%-10s | %-15s | %-15s | %-10s | %-30s%n", 
                        "Wagon ID", "Model", "Operator", "Payload", "Destination Station");
                System.out.println("--------------------------------------------------------------------------------------------------------");
                
                Facility endFacility = freight.getEndFacility();
                String destination = endFacility != null ? endFacility.getName() : "Unknown";
                
                for (Wagon wagon : freight.getWagons()) {
                    System.out.printf("%-10d | %-15s | %-15s | %-10.2f | %-30s%n",
                            wagon.getWagonID(),
                            wagon.wagonModel() != null ? wagon.wagonModel().getVehicle().getVehicleName() : "N/A",
                            wagon.getOperator() != null ? wagon.getOperator().getOperatorName() : "N/A",
                            wagon.getPayloadTons(),
                            destination);
                }
                System.out.println(); // Empty line between freights
            }
        }
        
        if (availableWagons.isEmpty()) {
            System.out.println("\nNo available wagons. Please ensure wagons are not already assigned to freights.");
            return new ArrayList<>();
        }

        System.out.println("\n--- Select Wagons (available wagons) ---");
        System.out.println("Wagons are ordered by distance from freight start facility (descending).");
        System.out.printf("%-10s | %-15s | %-15s | %-10s | %-30s | %-15s%n", 
                "ID", "Model", "Operator", "Payload", "Current Station", "Distance (km)");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");

        // Get wagons with distance information
        List<WagonAvailabilityDTO> wagonsWithDistance = controller.getAvailableWagonsWithDistance(startFacility);
        for (WagonAvailabilityDTO dto : wagonsWithDistance) {
            Wagon wagon = availableWagons.stream()
                    .filter(w -> w.getWagonID() == dto.getWagonID())
                    .findFirst()
                    .orElse(null);
            
            if (wagon != null) {
                Facility currentStation = wagon.getStartFacility();
                String stationName = currentStation != null ? currentStation.getName() : "Unknown";
                double distanceKm = dto.getDistanceFromStart() / 1000.0; // Convert meters to km
                System.out.printf("%-10d | %-15s | %-15s | %-10.2f | %-30s | %-15.2f%n",
                        wagon.getWagonID(),
                        wagon.wagonModel() != null ? wagon.wagonModel().getVehicle().getVehicleName() : "N/A",
                        wagon.getOperator() != null ? wagon.getOperator().getOperatorName() : "N/A",
                        wagon.getPayloadTons(),
                        stationName,
                        distanceKm);
            }
        }

        String input = Utils.readLineFromConsole("\nEnter wagon IDs separated by comma (at least one): ");
        if (input == null || input.isBlank()) {
            return new ArrayList<>();
        }

        String[] tokens = input.split(",");
        List<Wagon> selected = new ArrayList<>();

        for (String token : tokens) {
            try {
                int id = Integer.parseInt(token.trim());
                Wagon wagon = availableWagons.stream()
                        .filter(w -> w.getWagonID() == id)
                        .findFirst()
                        .orElse(null);

                if (wagon != null && selected.stream().noneMatch(w -> w.getWagonID() == id)) {
                    selected.add(wagon);
                } else if (wagon == null) {
                    System.out.printf("Wagon with ID %d not available.%n", id);
                } else {
                    System.out.printf("Wagon with ID %d already selected.%n", id);
                }
            } catch (NumberFormatException e) {
                System.out.printf("Invalid wagon ID '%s' ignored.%n", token.trim());
            }
        }

        return selected;
    }
}

