package pt.ipp.isep.dei.LAPR3.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Freight {
    private int freightID;
    private Route route;
    private List<Wagon> wagons;
    private Train train;
    private Date departureDate;
    private Facility startEndPoint;
    private Facility endEndPoint;

    public Freight(int freightID,
                   Route route,
                   Train train,
                   Date departureDate,
                   Facility startEndPoint,
                   Facility endEndPoint) {
        this.freightID = freightID;
        this.route = route;
        this.train = train;
        this.departureDate = departureDate;
        this.startEndPoint = startEndPoint;
        this.endEndPoint = endEndPoint;
        this.wagons = new ArrayList<>();
    }

    public Freight(int freightID, Route route, Date departureDate, Facility startEndPoint, Facility endEndPoint) {
        this(freightID, route, null, departureDate, startEndPoint, endEndPoint);
    }

    public Freight(int freightID, Facility startEndPoint, Facility endEndPoint) {
        this(freightID, null, null, null, startEndPoint, endEndPoint);
    }

    public Freight(int freightID) {
        this(freightID, null, null, null, null, null);
    }

    public int getFreightID() {
        return freightID;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public boolean hasTrain() {
        return train != null;
    }

    public List<Wagon> getWagons() {
        return wagons;
    }

    public void addWagon(Wagon wagon) {
        wagons.add(wagon);
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public boolean hasRoute() {
        return route != null;
    }

    public boolean hasDepartureDate() {
        return departureDate != null;
    }

    public Facility getStartEndPoint() {
        return startEndPoint;
    }

    public void setStartEndPoint(Facility startEndPoint) {
        this.startEndPoint = startEndPoint;
    }

    public Facility getEndEndPoint() {
        return endEndPoint;
    }

    public void setEndEndPoint(Facility endEndPoint) {
        this.endEndPoint = endEndPoint;
    }

    public Facility getStartFacility() {
        return startEndPoint != null ? startEndPoint : null;
    }

    public Facility getEndFacility() {
        return endEndPoint != null ? endEndPoint : null;
    }

    public Train getAssignedTrain() {
        return train;
    }

    public boolean hasEndpoints() {
        return startEndPoint != null && endEndPoint != null;
    }

    @Override
    public String toString() {
        String routeInfo = route != null ? String.valueOf(route.getRouteId()) : "Unassigned";
        String trainInfo = train != null ? String.valueOf(train.getTrainID()) : "Unassigned";
        String departureInfo = departureDate != null ? departureDate.toString() : "Unscheduled";
        String endpointsInfo = hasEndpoints()
                ? String.format("%s ➝ %s",
                getStartFacility().getName(),
                getEndFacility().getName())
                : "Unassigned";

        return String.format("Freight %d%n Route %s%n Train %s%n Endpoints %s%n Number of Wagons: %d%n Departure Date %s",
                freightID,
                routeInfo,
                trainInfo,
                endpointsInfo,
                wagons.size(),
                departureInfo);
    }
}
