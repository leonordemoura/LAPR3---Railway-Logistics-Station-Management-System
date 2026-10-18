package pt.ipp.isep.dei.ui.console.menu.actions.USLP07;

import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.LAPR3.database.ConnectionFactory;
import pt.ipp.isep.dei.LAPR3.database.DatabaseConnection;
import pt.ipp.isep.dei.LAPR3.database.RouteStore;
import pt.ipp.isep.dei.LAPR3.domain.*;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class CreateRouteUI implements Runnable{
    private RailwayRepository repository;
    private RouteStore routeStore;
    private final ConnectionFactory connectionFactory;
    private final Scanner scanner;


    public CreateRouteUI() throws IOException {
        this.repository = Repositories.getInstance().getRailwayRepository();
        this.routeStore = new RouteStore();
        this.connectionFactory = ConnectionFactory.getInstance();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        System.out.println(Utils.createHeader("Create a new route"));

        Facility startFacility = selectFacility("Departure Facility");
        if (startFacility == null) return;

        Facility endFacility = selectFacility("Arrival Facility: ");
        if (endFacility == null) return;

        Facility startEndPoint = repository.getFacilityById(startFacility.getFacilityID());
        Facility endEndPoint = repository.getFacilityById(endFacility.getFacilityID());

        if (startEndPoint == null || endEndPoint == null) {
            System.out.println("Unable to find endpoints for selected facilities. Please ensure database data is loaded.");
            return;
        }

        try {
            DatabaseConnection dbConnection = connectionFactory.getDatabaseConnection();
            int routeID = routeStore.getNextRouteID(dbConnection);

            Route route = new Route(routeID, startEndPoint, endEndPoint);


            boolean routeSaved = routeStore.save(dbConnection, route);
            if (!routeSaved) {
                System.err.println("Warning: Failed to create route " + routeID);
            }

            repository.addRoute(route);

        } catch (Exception e) {
            System.err.println("Error creating route: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\nRoute created successfully: ("
                + startFacility.getName() + " ➝ " + endFacility.getName() + ")");

    }

    private Facility selectFacility(String type) {
        List<Facility> facilities = repository.getAllFacilities();
        List<Freight> freights = repository.getAllFreights();

        List<Freight> load = new ArrayList<>();
        List<Freight> unload = new ArrayList<>();

        List<Train> trains = repository.getAllTrains();

        System.out.println("\nAvailable Facilities (" + type + "):");
        System.out.println("------------------------------------");

        for (Facility f : facilities) {
            System.out.printf("%2d - %s%n", f.getFacilityID(), f.getName());
            for (Freight ft : freights) {
                if(ft.getStartFacility() == f && !trains.contains(ft.getAssignedTrain())) {
                    load.add(ft);
                }
                if (ft.getEndFacility() == f && !trains.contains(ft.getAssignedTrain())) {
                    unload.add(ft);
                }
            }
            if(!load.isEmpty()) {
                System.out.println("   \uD83D\uDCE6 Freights to Load:");
            }
            for(Freight ft : load){
                System.out.printf("     -> Freight ID %d%n", ft.getFreightID());
            }

            if(!unload.isEmpty()) {
                System.out.println("   \uD83D\uDC77\u200D♂\uFE0F Freights to Unload:");
            }
            for(Freight ft : unload){
                System.out.printf("     -> Freight ID %d%n", ft.getFreightID());
            }
            load.clear();
            unload.clear();
        }

        System.out.println("0  - Cancel:");

        int id = readInteger("\nEnter " + type + " ID: ");

        Facility facility = repository.getFacilityById(id);
        if(id == 0){
            System.out.println("Operation cancelled.");
            return null;
        }

        if (facility == null) {
            System.out.println("ERROR: Facility with ID " + id + " not found.");
        }

        return facility;
    }

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