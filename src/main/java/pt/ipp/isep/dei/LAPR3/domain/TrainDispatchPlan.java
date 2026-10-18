package pt.ipp.isep.dei.LAPR3.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class TrainDispatchPlan {
    private final Train train;
    private final Path path;
    private final LocalDateTime departureTime;

    public TrainDispatchPlan(Train train, Path path, LocalDateTime departureTime) {
        this.train = Objects.requireNonNull(train, "train is required");
        this.path = Objects.requireNonNull(path, "path is required");
        this.departureTime = Objects.requireNonNull(departureTime, "departure time is required");
    }

    public Train getTrain() {
        return train;
    }

    public Path getPath() {
        return path;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }
}

