package pt.ipp.isep.dei.LAPR3.domain;

import pt.ipp.isep.dei.LAPR3.domain.constants.RouteType;

import java.util.Date;

public class Route {
    private int routeId;
    private RouteType routeType;
    private Facility startFacility;
    private Facility endFacility;

    public Route(int routeId, Facility startFacility, Facility endFacility) {
        this.startFacility = startFacility;
        this.endFacility = endFacility;
        this.routeId = routeId;
        this.routeType = RouteType.SIMPLE;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public Facility getStartFacility() {
        return startFacility;
    }
    public Facility getEndFacility() {
        return endFacility;
    }
    public int getStartFacilityId() {
        return startFacility.getFacilityID();
    }
    public int getEndFacilityId() {
        return endFacility.getFacilityID();
    }
    public RouteType getRouteType() {
        return routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    @Override
    public String toString() {
        return String.format("Route %d (%s ➝ %s)",
                routeId,
                startFacility.getName(),
                endFacility.getName());
    }
}
