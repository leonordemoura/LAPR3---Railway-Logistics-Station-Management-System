package pt.ipp.isep.dei.LAPR3.services.dto;

import pt.ipp.isep.dei.LAPR3.domain.Facility;

/**
 * DTO representing wagon availability information for train assembly.
 */
public class WagonAvailabilityDTO {
    private int wagonID;
    private double payloadTons;
    private boolean inTransit;
    private Facility finalDestination; // For wagons in transit
    private Facility currentStation; // For parked wagons
    private double distanceFromStart; // For parked wagons

    public WagonAvailabilityDTO(int wagonID, double payloadTons) {
        this.wagonID = wagonID;
        this.payloadTons = payloadTons;
        this.inTransit = false;
    }

    public int getWagonID() {
        return wagonID;
    }

    public double getPayloadTons() {
        return payloadTons;
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
            return String.format("Wagon %d (%.2f tons) - In Transit → %s",
                    wagonID, payloadTons,
                    finalDestination != null ? finalDestination.getName() : "Unknown");
        } else {
            return String.format("Wagon %d (%.2f tons) - Parked at %s (Distance: %.2f km)",
                    wagonID, payloadTons,
                    currentStation != null ? currentStation.getName() : "Unknown",
                    distanceFromStart / 1000.0); // Convert meters to km
        }
    }
}


