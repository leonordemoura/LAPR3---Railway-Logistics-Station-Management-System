package pt.ipp.isep.dei.LAPR3.services.dto;

import pt.ipp.isep.dei.LAPR3.domain.Facility;

/**
 * DTO representing locomotive availability information for train assembly.
 */
public class LocomotiveAvailabilityDTO {
    private int locomotiveID;
    private String name;
    private double operationalSpeed;
    private boolean inTransit;
    private Facility finalDestination; // For locomotives in transit
    private Facility currentStation; // For parked locomotives
    private double distanceFromStart; // For parked locomotives

    public LocomotiveAvailabilityDTO(int locomotiveID, String name, double operationalSpeed) {
        this.locomotiveID = locomotiveID;
        this.name = name;
        this.operationalSpeed = operationalSpeed;
        this.inTransit = false;
    }

    public int getLocomotiveID() {
        return locomotiveID;
    }

    public String getName() {
        return name;
    }

    public double getOperationalSpeed() {
        return operationalSpeed;
    }

    public boolean isInTransit() {
        return inTransit;
    }

    public void setInTransit(boolean inTransit) {
        this.inTransit = inTransit;
    }

    public Facility getFinalDestination() {
        return finalDestination;
    }

    public void setFinalDestination(Facility finalDestination) {
        this.finalDestination = finalDestination;
    }

    public Facility getCurrentStation() {
        return currentStation;
    }

    public void setCurrentStation(Facility currentStation) {
        this.currentStation = currentStation;
    }

    public double getDistanceFromStart() {
        return distanceFromStart;
    }

    public void setDistanceFromStart(double distanceFromStart) {
        this.distanceFromStart = distanceFromStart;
    }

    @Override
    public String toString() {
        if (inTransit) {
            return String.format("Locomotive %d (%s) - In Transit → %s",
                    locomotiveID, name != null ? name : "Nameless",
                    finalDestination != null ? finalDestination.getName() : "Unknown");
        } else {
            return String.format("Locomotive %d (%s) - Parked at %s (Distance: %.2f km)",
                    locomotiveID, name != null ? name : "Nameless",
                    currentStation != null ? currentStation.getName() : "Unknown",
                    distanceFromStart / 1000.0); // Convert meters to km
        }
    }
}


