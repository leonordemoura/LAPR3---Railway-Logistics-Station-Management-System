package pt.ipp.isep.dei.LAPR3.domain;

import java.time.LocalDateTime;

public class CrossingOperation {
    private final Train firstTrain;
    private final Train secondTrain;
    private final LineSegment segment;
    private final LocalDateTime scheduledTime;
    private final String locationDescription;

    public CrossingOperation(Train firstTrain,
                             Train secondTrain,
                             LineSegment segment,
                             LocalDateTime scheduledTime,
                             String locationDescription) {
        this.firstTrain = firstTrain;
        this.secondTrain = secondTrain;
        this.segment = segment;
        this.scheduledTime = scheduledTime;
        this.locationDescription = locationDescription;
    }

    public Train getFirstTrain() {
        return firstTrain;
    }

    public Train getSecondTrain() {
        return secondTrain;
    }

    public LineSegment getSegment() {
        return segment;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public String getLocationDescription() {
        return locationDescription;
    }
}

