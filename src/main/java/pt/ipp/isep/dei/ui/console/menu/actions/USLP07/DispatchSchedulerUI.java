package pt.ipp.isep.dei.ui.console.menu.actions.USLP07;

import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.LAPR3.controller.DispatchSchedulerController;
import pt.ipp.isep.dei.LAPR3.database.ConnectionFactory;
import pt.ipp.isep.dei.LAPR3.database.DatabaseConnection;
import pt.ipp.isep.dei.LAPR3.database.PathStore;
import pt.ipp.isep.dei.LAPR3.database.TrainStore;
import pt.ipp.isep.dei.LAPR3.domain.*;
import pt.ipp.isep.dei.LAPR3.domain.constants.RouteType;
import pt.ipp.isep.dei.LAPR3.domain.constants.Status;
import pt.ipp.isep.dei.LAPR3.services.impl.CrossingDetectionServiceImpl;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class DispatchSchedulerUI implements Runnable {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final RailwayRepository repository;
    private final DispatchSchedulerController controller;
    private final PathStore pathStore;
    private final TrainStore trainStore;
    private final ConnectionFactory connectionFactory;
    private final Scanner scanner;

    public DispatchSchedulerUI() throws IOException {
        this.repository = Repositories.getInstance().getRailwayRepository();
        this.controller = new DispatchSchedulerController();
        this.pathStore = new PathStore();
        this.trainStore = new TrainStore();
        this.connectionFactory = ConnectionFactory.getInstance();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        System.out.println(Utils.createHeader("USLP07 - Train Dispatch Scheduler"));

        // Only show trains that are PLANNED (not yet dispatched) AND have locomotives assigned
        List<Train> allTrains = repository.getAllTrains().stream()
                .filter(t -> (t.getStatus() == null || t.getStatus() == Status.PLANNED))
                .filter(t -> t.getLocomotives() != null && !t.getLocomotives().isEmpty())
                .collect(Collectors.toList());

        if (allTrains.isEmpty()) {
            System.out.println("No trains available to dispatch.");
            return;
        }

        List<Train> selectedTrains = selectTrains(allTrains);
        if (selectedTrains.isEmpty()) {
            System.out.println("No trains selected. Operation cancelled.");
            return;
        }

        List<TrainDispatchPlan> plans = new ArrayList<>();
        for (Train train : selectedTrains) {
            Path path = null;
            // Perguntar se o path será automático ou manual
            System.out.printf("%nTrain %d: choose path option:%n", train.getTrainID());
            System.out.println("1 - Automatic path");
            System.out.println("2 - Manual path");
            int choice = Utils.readIntFromConsole("Enter option (1 or 2): ", 1, 2);

            if (choice == 1) {
                try {
                    path = controller.calculateAutomaticPathWithStops(train);
                } catch (IllegalStateException e) {
                    System.out.println("Error calculating path: " + e.getMessage());
                    continue;
                }

                System.out.printf("Automatic path generated for Train %d (%d facilities).%n",
                        train.getTrainID(), path.getFacilities().size());

                // Cabeçalho da tabela
                System.out.printf("%-6s %-6s %-30s %-5s%n", "Order", "ID", "Facility", "Stop");
                System.out.println("-".repeat(55));

                for (int i = 0; i < path.getFacilities().size(); i++) {
                    Path.PathFacilityStep step = path.getFacilities().get(i);

                    // Agora, se Darque tiver carga associada, step.isScheduledStop() será TRUE
                    String stopMark = step.isScheduledStop() ? "Yes" : "No";

                    System.out.printf("%-6d %-6d %-30s %-5s%n",
                            i + 1,
                            step.getFacility().getFacilityID(),
                            step.getFacility().getName(),
                            stopMark);
                }
            }

            else {
                // Path manual
                path = choosePathForTrain(train);
                if (path == null) {
                    System.out.printf("Skipping train %d due to missing path.%n", train.getTrainID());
                    continue;
                }
            }

            LocalDateTime departure = promptDepartureTime(train);
            plans.add(controller.createPlan(train, path, departure));
        }

        if (plans.isEmpty()) {
            System.out.println("No valid dispatch plans were created.");
            return;
        }

        try {
            DispatchSchedule schedule = controller.schedule(plans);
            displaySchedule(schedule);

            // Update train status to AVAILABLE after successful dispatch
            // This ensures locomotives and wagons are updated in the "in transit" lists
            updateDispatchedTrainsStatus(plans);
        } catch (IllegalArgumentException e) {
            System.out.println("Unable to create schedule: " + e.getMessage());
        }
    }

    private List<Train> selectTrains(List<Train> trains) {
        System.out.println("Available trains:");
        System.out.printf("%-8s %-35s %-25s %-20s%n", "ID", "Stations", "Route", "Departure");
        for (Train train : trains) {
            Route route = train.getRoute();
            String routeLabel = route == null ? "N/A" :
                    route.getStartFacility().getName() + " → " + route.getEndFacility().getName();

            if (route != null && train.getFreights().size() > 1){
                route.setRouteType(RouteType.COMPLEX);
            }

            String departureLabel = controller.getSuggestedDeparture(train).format(DATE_TIME_FORMATTER);
            System.out.printf("%-8d %-35s %-20s %-20s %-20s%n",
                    train.getTrainID(),
                    routeLabel,
                    "Route " + (route != null ? route.getRouteId() : "-"),
                    route.getRouteType(),
                    departureLabel);
        }

        String input = Utils.readLineFromConsole("Enter train IDs separated by comma (or press ENTER to cancel): ");
        if (input == null || input.isBlank()) {
            return Collections.emptyList();
        }

        if (input.trim().equalsIgnoreCase("ALL")) {
            return new ArrayList<>(trains);
        }

        String[] tokens = input.split(",");
        Set<Integer> ids = new LinkedHashSet<>();
        for (String token : tokens) {
            try {
                ids.add(Integer.parseInt(token.trim()));
            } catch (NumberFormatException e) {
                System.out.printf("Invalid train ID '%s' ignored.%n", token.trim());
            }
        }

        List<Train> selected = new ArrayList<>();
        for (Integer id : ids) {
            Train train = repository.getTrainById(id);
            if (train != null) {
                selected.add(train);
            } else {
                System.out.printf("Train with ID %d not found.%n", id);
            }
        }
        return selected;
    }

    private Path choosePathForTrain(Train train) {
        Route route = train.getRoute();
        if (route == null) {
            System.out.printf("Train %d has no route assigned.%n", train.getTrainID());
            return null;
        }

        List<Path> existingPaths = repository.getPathsByRouteId(route.getRouteId());
        List<Object> options = new ArrayList<>(existingPaths);
        options.add("Define manual path");

        if (existingPaths.isEmpty()) {
            System.out.printf("No paths defined for Route %s → %s.%n",
                    route.getStartFacility().getName(), route.getEndFacility().getName());
        }

        System.out.printf("%nSelect path for Train %d (Route %s → %s)%n",
                train.getTrainID(), route.getStartFacility().getName(), route.getEndFacility().getName());

        int option = Utils.showAndSelectIndex(options, "Available Paths:");
        if (option == -1) {
            return null;
        }

        if (option < existingPaths.size()) {
            Path selected = existingPaths.get(option);
            if (!validatePathForRouteAndTrain(selected, route, train)) {
                System.out.println("The selected path does not cover the necessary facilities.");
                return null;
            }
            return selected;
        } else {
            return defineManualPath(train, route);
        }
    }


    private Path defineManualPath(Train train, Route route) {
        System.out.printf("%n--- Manual Path Definition for Train %d ----%n", train.getTrainID());

        displayAvailableFacilities(route);

        List<PathDefinitionHelper.PathFacilitySelection> selections =
                PathDefinitionHelper.promptPathDefinition(repository, route, scanner);

        if (selections.isEmpty()) {
            return null;
        }

        try {
            DatabaseConnection dbConnection = connectionFactory.getDatabaseConnection();
            int pathID = pathStore.getNextPathID(dbConnection);

            Path path = new Path(pathID);
            path.setRoute(route);

            int order = 1;
            for (PathDefinitionHelper.PathFacilitySelection selection : selections) {
                path.addFacility(selection.getFacility(), order++, selection.isScheduledStop());
            }

            // Save path to database
            boolean saved = pathStore.save(dbConnection, path);
            if (!saved) {
                return null;
            }

            // Add path to repository
            repository.addPath(path);

            return path;
        } catch (Exception e) {
            System.err.println("Error creating train: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void displayAvailableFacilities(Route route) {
        List<Facility> allFacilities = repository.getAllFacilities();

        System.out.println("\nAvailable Facilities:");
        System.out.printf("%-10s %-30s %-20s%n", "Facility ID", "Name", "Type");
        System.out.println("-".repeat(60));

        for (Facility facility : allFacilities) {
            String facilityType = facility.getClass().getSimpleName();
            System.out.printf("%-10s %-30s %-20s%n", facility.getFacilityID(), facility.getName(), facilityType);
        }

        System.out.printf("\nRoute: %s → %s%n",
                route.getStartFacility().getName(),
                route.getEndFacility().getName());
    }

    private LocalDateTime promptDepartureTime(Train train) {
        LocalDateTime defaultTime = controller.getSuggestedDeparture(train);
        while (true) {
            System.out.printf("Planned departure for train %d is %s. Press ENTER to keep or enter new (dd-MM-yyyy HH:mm): ",
                    train.getTrainID(), defaultTime.format(DATE_TIME_FORMATTER));
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return defaultTime;
            }
            try {
                return LocalDateTime.parse(input, DATE_TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date/time format. Please try again.");
            }
        }
    }

    private void displaySchedule(DispatchSchedule schedule) {
        // Display train schedules
        for (TrainSchedule trainSchedule : schedule.getTrainSchedules()) {
            Train train = trainSchedule.getTrain();
            Route route = train.getRoute();
            System.out.printf("%n=== Train %d | Route %s ➝ %s ===%n",
                    train.getTrainID(),
                    route != null ? route.getStartFacility().getName() : "?",
                    route != null ? route.getEndFacility().getName() : "?");

            System.out.printf("%-30s %-20s %-20s %-6s%n", "Facility", "Arrival", "Departure", "Stop");
            for (TrainSchedulePoint point : trainSchedule.getPoints()) {
                if(route != null && point.getFacility() == route.getStartFacility()){
                    System.out.printf("%-30s %-20s %-20s %-6s%n",
                            point.getFacility() != null ? point.getFacility().getName() : "?",
                            "---",
                            formatTime(point.getDepartureTime()),
                            point.isScheduledStop() ? "Yes" : "No");
                }else if(route != null && point.getFacility() == route.getEndFacility()){
                    System.out.printf("%-30s %-20s %-20s %-6s%n",
                            point.getFacility() != null ? point.getFacility().getName() : "?",
                            formatTime(point.getArrivalTime()),
                            "---",
                            point.isScheduledStop() ? "Yes" : "No");
                }else {
                    System.out.printf("%-30s %-20s %-20s %-6s%n",
                            point.getFacility() != null ? point.getFacility().getName() : "?",
                            formatTime(point.getArrivalTime()),
                            formatTime(point.getDepartureTime()),
                            point.isScheduledStop() ? "Yes" : "No");
                }
            }

            List<TrainSchedulePoint> points = trainSchedule.getPoints();
            // Velocidade entre cada parada
            double sumSpeeds = 0;
            int count = 0;
            System.out.println("\n--- Speeds between stops ---");
            for (int i = 1; i < points.size(); i++) {
                TrainSchedulePoint prev = points.get(i - 1);
                TrainSchedulePoint curr = points.get(i);

                // Calcula distância entre as duas paradas
                double distanceKm = 0;
                for (TrainSchedule.SegmentOccupancy segment : trainSchedule.getSegmentOccupancies()) {
                    if (segment.getFromFacility().equals(prev.getFacility()) &&
                            segment.getToFacility().equals(curr.getFacility())) {
                        distanceKm += segment.getSegment().getLength() / 1000.0;
                    }
                }

                // Tempo em movimento (ignora dwell time)
                double minutes = java.time.Duration.between(prev.getDepartureTime(), curr.getArrivalTime()).toMinutes();
                double hours = minutes / 60.0;
                double speedKmh = hours > 0 ? distanceKm / hours : 0;
                sumSpeeds += speedKmh;
                count++;
                System.out.printf("From %s to %s: %.2f km/h%n",
                        prev.getFacility().getName(),
                        curr.getFacility().getName(),
                        speedKmh);
            }
            double averageSpeed = count > 0 ? sumSpeeds / count : 0;
            System.out.printf("\nAverage speed: %.2f km/h%n",
                    averageSpeed);
            System.out.printf("\nTotal Distance: %.2f km | Total Duration: %.1f minutes%n",
                    trainSchedule.getTotalDistanceKm(), trainSchedule.getTotalDurationMinutes());
        }

        System.out.println("\n--- Crossing Operations ---");
        List<CrossingOperation> operations = schedule.getCrossingOperations();

        if (operations.isEmpty()) {
            System.out.println("\n✓ No train crossings detected - All trains can proceed without conflicts.");
        } else {
            System.out.printf("\n⚠ %d crossing operation(s) detected:%n", operations.size());

            for (int i = 0; i < operations.size(); i++) {
                CrossingDetectionServiceImpl.displayCrossing(operations.get(i), i + 1);
                System.out.println();
            }

            CrossingDetectionServiceImpl.displayCrossingSummary(operations);
        }
    }

    private String formatTime(LocalDateTime time) {
        return time == null ? "-" : time.format(DATE_TIME_FORMATTER);
    }

    private boolean validatePathForRouteAndTrain(Path path, Route route, Train train) {
        // Verifica início e fim da route
        boolean hasStart = path.getFacilities().stream()
                .anyMatch(f -> f.getFacility().equals(route.getStartFacility()));
        boolean hasEnd = path.getFacilities().stream()
                .anyMatch(f -> f.getFacility().equals(route.getEndFacility()));

        if (!hasStart || !hasEnd) return false;

        // Verifica cada freight do train
        for (Freight freight : train.getFreights()) {
            Facility freightStart = freight.getStartFacility();
            Facility freightEnd = freight.getEndFacility();

            boolean hasFreightStart = path.getFacilities().stream()
                    .anyMatch(f -> f.getFacility().equals(freightStart));
            boolean hasFreightEnd = path.getFacilities().stream()
                    .anyMatch(f -> f.getFacility().equals(freightEnd));

            if (!hasFreightStart || !hasFreightEnd) {
                System.out.printf("Path does not include start/end facilities for freight %d.%n",
                        freight.getFreightID());
                return false;
            }
        }

        return true;
    }

    /**
     * Disassociates dispatched trains when route completes.
     * Moves locomotives to endRoute facility and makes them available.
     * Moves wagons to their freight's end facility and makes them available.
     * Removes freights completely (they have reached their destination).
     * Removes train from database and repository so it no longer appears in dispatch lists.
     *
     * @param plans List of train dispatch plans that were successfully scheduled
     */
    private void updateDispatchedTrainsStatus(List<TrainDispatchPlan> plans) {
        DatabaseConnection dbConnection = connectionFactory.getDatabaseConnection();

        for (TrainDispatchPlan plan : plans) {
            Train train = plan.getTrain();

            // Disassociate train: move locomotives to endRoute, wagons to endFreight, remove associations
            boolean disassociated = trainStore.disassociateTrainOnRouteCompletion(dbConnection, train.getTrainID());
            if (!disassociated) {
                System.err.printf("Warning: Failed to disassociate train %d after dispatch.%n",
                        train.getTrainID());
                SQLException lastError = dbConnection.getLastError();
                if (lastError != null) {
                    System.err.println("Database error: " + lastError.getMessage());
                } else {
                    System.err.println("Database error: Unknown error occurred during disassociation.");
                }
            } else {
                // Remove train from repository so it no longer appears in dispatch lists
                repository.removeTrain(train.getTrainID());
            }
        }

        System.out.println("\n✓ Trains and freights removed after dispatch. Locomotives and wagons moved to their destinations and made available.");
    }
}