package pt.ipp.isep.dei.LAPR3.domain;

import java.util.Collections;
import java.util.List;

public class DispatchSchedule {
    private final List<TrainSchedule> trainSchedules;
    private final List<CrossingOperation> crossingOperations;

    public DispatchSchedule(List<TrainSchedule> trainSchedules,
                            List<CrossingOperation> crossingOperations) {
        this.trainSchedules = Collections.unmodifiableList(trainSchedules);
        this.crossingOperations = Collections.unmodifiableList(crossingOperations);
    }

    public List<TrainSchedule> getTrainSchedules() {
        return trainSchedules;
    }

    public List<CrossingOperation> getCrossingOperations() {
        return crossingOperations;
    }
}

