package pt.ipp.isep.dei.ui.console.menu.actions.USLP07;

import pt.ipp.isep.dei.LAPR3.domain.Facility;
import pt.ipp.isep.dei.LAPR3.domain.RailwayRepository;
import pt.ipp.isep.dei.LAPR3.domain.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

final class PathDefinitionHelper {

    private PathDefinitionHelper() {
        // utility class
    }

    static List<PathFacilitySelection> promptPathDefinition(RailwayRepository repository,
                                                            Route route,
                                                            Scanner scanner) {
        if (route == null) {
            return Collections.emptyList();
        }

        Facility start = route.getStartFacility();
        Facility end = route.getEndFacility();

        if (start == null || end == null) {
            return Collections.emptyList();
        }

        List<PathFacilitySelection> selections = new ArrayList<>();
        selections.add(new PathFacilitySelection(start, true));

        System.out.printf("%nDefining path for Route %d (%s ➝ %s)%n",
                route.getRouteId(), start.getName(), end.getName());
        System.out.println("Enter facility IDs in traversal order.");
        System.out.println("Press ENTER to finish by adding the destination, or type 'C' to cancel.");

        int stepOrder = 2;
        boolean exitRequested = false;
        while (!exitRequested) {
            System.out.printf("Next facility for step %d (current end: %s): ",
                    stepOrder, end.getName());
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("C")) {
                return Collections.emptyList();
            }

            if (input.isEmpty()) {
                break;
            }

            int facilityId;
            try {
                facilityId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
                continue;
            }

            Facility facility = repository.getFacilityById(facilityId);
            if (facility == null) {
                System.out.printf("Facility with ID %d not found.%n", facilityId);
                continue;
            }

            boolean isStop = promptYesNo(scanner, "Is this a scheduled stop (Y/N)? ");
            selections.add(new PathFacilitySelection(facility, isStop));
            stepOrder++;

            if (facility.getFacilityID() == end.getFacilityID()) {
                exitRequested = true;
            }
        }

        if (selections.get(selections.size() - 1).getFacility().getFacilityID() != end.getFacilityID()) {
            selections.add(new PathFacilitySelection(end, true));
        }

        return selections;
    }

    private static boolean promptYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = scanner.nextLine().trim();
            if (answer.equalsIgnoreCase("Y") || answer.equalsIgnoreCase("YES") || answer.isEmpty()) {
                return true;
            }
            if (answer.equalsIgnoreCase("N") || answer.equalsIgnoreCase("NO")) {
                return false;
            }
            System.out.println("Please respond with Y or N.");
        }
    }

    static final class PathFacilitySelection {
        private final Facility facility;
        private final boolean scheduledStop;

        PathFacilitySelection(Facility facility, boolean scheduledStop) {
            this.facility = facility;
            this.scheduledStop = scheduledStop;
        }

        public Facility getFacility() {
            return facility;
        }

        public boolean isScheduledStop() {
            return scheduledStop;
        }
    }
}

