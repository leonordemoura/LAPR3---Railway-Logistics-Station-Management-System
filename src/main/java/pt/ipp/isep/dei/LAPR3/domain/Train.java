package pt.ipp.isep.dei.LAPR3.domain;

import pt.ipp.isep.dei.LAPR3.domain.constants.Status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Train {
    private int trainID;
    private Route route;
    private List<Locomotive> locomotives;
    private List<Freight> freights;
    private Date startDate;
    private Status status;

    public Train(int trainID, Route route, Date startDate, Status status) {
        this.trainID = trainID;
        this.route = route;
        this.startDate = startDate;
        locomotives = new ArrayList<>();
        freights = new ArrayList<>();
        this.status = status;
    }

    public int getTrainID() {
        return trainID;
    }

    public Route getRoute() {
        return route;
    }

    public List<Locomotive> getLocomotives() {
        return locomotives;
    }

    public List<Freight> getFreights() {
        return freights;
    }

    public void addLocomotive(Locomotive locomotive) {
        locomotives.add(locomotive);
    }

    public void addFreight(Freight freight) {
        freights.add(freight);
    }

    public Date getStartDate() {
        return startDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
