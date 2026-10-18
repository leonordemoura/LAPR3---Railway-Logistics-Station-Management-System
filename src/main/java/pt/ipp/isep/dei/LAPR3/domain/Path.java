package pt.ipp.isep.dei.LAPR3.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Path {
    private int pathID;
    private Route route;
    private final List<PathFacilityStep> facilities;

    public Path(int pathID) {
        this.pathID = pathID;
        this.facilities = new ArrayList<>();
    }

    public int getPathID() {
        return pathID;
    }

    public void setPathID(int pathID) {
        this.pathID = pathID;
    }

    public Route getRoute() {
        return route;
    }

    public List<PathFacilityStep> getFacilities() {
        return Collections.unmodifiableList(facilities);
    }

    public void clearFacilities() {
        facilities.clear();
    }

    public void addFacility(Facility facility, boolean scheduledStop) {
        int nextOrder = facilities.isEmpty() ? 1 : facilities.get(facilities.size() - 1).getOrder() + 1;
        addFacility(facility, nextOrder, scheduledStop);
    }

    public void addFacility(Facility facility, int order, boolean scheduledStop) {
        facilities.add(new PathFacilityStep(order, facility, scheduledStop));
        facilities.sort(Comparator.comparingInt(PathFacilityStep::getOrder));
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    private Facility getFirstFacility() {
        return facilities.isEmpty() ? null : facilities.get(0).getFacility();
    }

    private Facility getLastFacility() {
        return facilities.isEmpty() ? null : facilities.get(facilities.size() - 1).getFacility();
    }

    @Override
    public String toString() {
        Facility start = getFirstFacility();
        Facility end = getLastFacility();
        return String.format("Path %d [%s ➝ %s] (%d facilities)",
                pathID,
                start != null ? start.getName() : "?",
                end != null ? end.getName() : "?",
                facilities.size());
    }

    public static class PathFacilityStep {
        private final int order;
        private final Facility facility;
        private final boolean scheduledStop;

        public PathFacilityStep(int order, Facility facility, boolean scheduledStop) {
            this.order = order;
            this.facility = facility;
            this.scheduledStop = scheduledStop;
        }

        public int getOrder() {
            return order;
        }

        public Facility getFacility() {
            return facility;
        }

        public boolean isScheduledStop() {
            return scheduledStop;
        }
    }
}
