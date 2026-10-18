package pt.ipp.isep.dei.LAPR3.domain;

import pt.ipp.isep.dei.LAPR3.domain.constants.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a locomotive with its associated attributes and capabilities.
 */
public class Locomotive {
    private int locomotiveId;
    private Operator operator;
    private LocomotiveModel locomotiveModel;
    private LocomotiveMake make;
    private List<Gauge> gauges;
    private String locomotiveName;
    private int serviceYear;
    private double operationalSpeed;
    private Facility startFacility;
    private Status status;

    /**
     * Constructs a new Locomotive with the specified attributes.
     *
     * @param locomotiveId     the unique identifier of the locomotive
     * @param operator         the operator of the locomotive
     * @param locomotiveModel  the model of the locomotive
     * @param make             the make of the locomotive
     * @param locomotiveName   the name of the locomotive
     * @param serviceYear      the year the locomotive entered service
     * @param operationalSpeed the operational speed of the locomotive
     */
    public Locomotive(int locomotiveId, Operator operator, LocomotiveModel locomotiveModel, LocomotiveMake make, String locomotiveName, int serviceYear, double operationalSpeed, Facility startFacility, Status status) {
        this.locomotiveId = locomotiveId;
        this.operator = operator;
        this.locomotiveModel = locomotiveModel;
        this.make = make;
        this.gauges = new ArrayList<>();
        this.locomotiveName = locomotiveName;
        this.serviceYear = serviceYear;
        this.operationalSpeed = operationalSpeed;
        this.startFacility = startFacility;
        this.status = status;
    }

    /**
     * Returns the unique identifier of the locomotive.
     *
     * @return the locomotive ID
     */
    public int getLocomotiveId() {
        return locomotiveId;
    }

    /**
     * Returns the operator of the locomotive.
     *
     * @return the operator
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * Returns the model of the locomotive.
     *
     * @return the locomotive model
     */
    public LocomotiveModel getLocomotiveModelID() {
        return locomotiveModel;
    }

    /**
     * Returns the make of the locomotive.
     *
     * @return the locomotive make
     */
    public LocomotiveMake getMake() {
        return make;
    }

    /**
     * Returns the list of gauges compatible with the locomotive.
     *
     * @return the list of gauges
     */
    public List<Gauge> getGauges() {
        return gauges;
    }

    /**
     * Returns the name of the locomotive.
     *
     * @return the locomotive name
     */
    public String getLocomotiveName() {
        return locomotiveName;
    }

    /**
     * Returns the year the locomotive entered service.
     *
     * @return the service year
     */
    public int getServiceYear() {
        return serviceYear;
    }

    /**
     * Returns the operational speed of the locomotive.
     *
     * @return the operational speed
     */
    public double getOperationalSpeed() {
        return operationalSpeed;
    }

    /**
     * Returns the start facility where the locomotive is located.
     *
     * @return the start facility
     */
    public Facility getStartFacility() {
        return startFacility;
    }

    /**
     * Returns the status of the locomotive.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Returns a string representation of the locomotive.
     *
     * @return a formatted string containing the locomotive's details
     */
    @Override
    public String toString() {
        return String.format("Locomotive %d:\n Operator: %s\n Locomotive Model: %d\n Locomotive Make: %s\n Locomotive Name: %s\n Service Year: %d\n Operational Speed: %.3f", locomotiveId, operator.getOperatorName(), locomotiveModel.getLocomotiveModelId(), make.getMakeName(), locomotiveName, serviceYear, operationalSpeed);
    }
}