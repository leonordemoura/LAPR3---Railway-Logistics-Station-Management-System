package pt.ipp.isep.dei.LAPR3.domain;

import pt.ipp.isep.dei.LAPR3.domain.constants.Status;

/**
 * Represents a wagon with its associated attributes and operator.
 */
public class Wagon {
    private int wagonID;
    private Operator operator;
    private WagonModel wagonModel;
    private int serviceYear;
    private double payloadTons;
    private Facility startFacility;
    private Status status;

    /**
     * Constructs a new Wagon with the specified attributes.
     *
     * @param wagonID      the unique identifier of the wagon
     * @param operator     the operator of the wagon
     * @param serviceYear  the year the wagon entered service
     * @param payloadTons  the payload capacity of the wagon in tons
     */
    public Wagon(int wagonID, Operator operator, WagonModel model, int serviceYear, double payloadTons, Facility startFacility, Status status) {
        this.wagonID = wagonID;
        this.operator = operator;
        this.wagonModel = model;
        this.serviceYear = serviceYear;
        this.payloadTons = payloadTons;
        this.startFacility = startFacility;
        this.status = status;
    }

    /**
     * Returns the unique identifier of the wagon.
     *
     * @return the wagon ID
     */
    public int getWagonID() {
        return wagonID;
    }

    /**
     * Returns the operator of the wagon.
     *
     * @return the operator
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * Returns the wagon model.
     *
     * @return the wagon model
     */
    public WagonModel wagonModel() {
        return wagonModel;
    }

    /**
     * Returns the year the wagon entered service.
     *
     * @return the service year
     */
    public int getServiceYear() {
        return serviceYear;
    }

    /**
     * Returns the payload capacity of the wagon in tons.
     *
     * @return the payload capacity in tons
     */
    public double getPayloadTons() {
        return payloadTons;
    }

    public Facility getStartFacility() {
        return startFacility;
    }

    public Status getStatus() {
        return status;
    }

    /**
     * Returns a string representation of the wagon.
     *
     * @return a formatted string containing the wagon's details
     */
    @Override
    public String toString() {
        return String.format("Wagon %d:\n Operator: %s\n Wagon Model: %s\n Service Year: %d\n Payload Tons: %.3f", wagonID, operator, wagonModel, serviceYear, payloadTons);
    }
}