package pt.ipp.isep.dei.LAPR3.controller;

import pt.ipp.isep.dei.LAPR3.domain.DispatchSchedule;
import pt.ipp.isep.dei.LAPR3.domain.Path;
import pt.ipp.isep.dei.LAPR3.domain.Train;
import pt.ipp.isep.dei.LAPR3.domain.TrainDispatchPlan;
import pt.ipp.isep.dei.LAPR3.services.DispatchSchedulerService;
import pt.ipp.isep.dei.LAPR3.services.impl.DispatchSchedulerServiceImpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class DispatchSchedulerController {

    private final DispatchSchedulerService service;

    public DispatchSchedulerController() {
        this.service = new DispatchSchedulerServiceImpl();
    }

    public DispatchSchedule schedule(List<TrainDispatchPlan> plans) {
        return service.scheduleTrains(plans);
    }

    public TrainDispatchPlan createPlan(Train train, Path path, LocalDateTime departureTime) {
        return new TrainDispatchPlan(train, path, departureTime);
    }

    public LocalDateTime getSuggestedDeparture(Train train) {
        Date startDate = train.getStartDate();
        if (startDate == null) {
            return LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        }
        return LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
    }


    public Path calculateAutomaticPath(Train train) {
        return service.calculateAutomaticPath(train);
    }

    public Path calculateAutomaticPathWithStops(Train train) {
        return service.calculateAutomaticPathWithStops(train);
    }




}

