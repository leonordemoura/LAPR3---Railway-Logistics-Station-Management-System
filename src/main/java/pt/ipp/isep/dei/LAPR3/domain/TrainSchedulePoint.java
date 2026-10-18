package pt.ipp.isep.dei.LAPR3.domain;

import java.time.LocalDateTime;

public class TrainSchedulePoint {
    private final Facility facility;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
    private final boolean scheduledStop;

    public TrainSchedulePoint(Facility facility, LocalDateTime arrivalTime,
                              LocalDateTime departureTime, boolean scheduledStop) {
        this.facility = facility;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.scheduledStop = scheduledStop;
    }

    public Facility getFacility() {
        return facility;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public boolean isScheduledStop() {
        return scheduledStop;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }
}