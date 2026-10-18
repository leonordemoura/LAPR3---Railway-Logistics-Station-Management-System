package pt.ipp.isep.dei.LAPR3.services;

import pt.ipp.isep.dei.LAPR3.domain.DispatchSchedule;
import pt.ipp.isep.dei.LAPR3.domain.Path;
import pt.ipp.isep.dei.LAPR3.domain.Train;
import pt.ipp.isep.dei.LAPR3.domain.TrainDispatchPlan;

import java.util.List;

public interface DispatchSchedulerService {
    DispatchSchedule scheduleTrains(List<TrainDispatchPlan> plans);
    Path calculateAutomaticPath(Train train);
    Path calculateAutomaticPathWithStops(Train train);

}

