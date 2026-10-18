package pt.ipp.isep.dei.LAPR3.domain;

import java.util.List;

/**
 * Represents a locomotive model with specific attributes and associated bogies.
 */
public class LocomotiveModel {
    private int locomotiveModelId;
    private Vehicle vehicle;
    private double power;
    private ElectricLocomotive electricLocomotive;
    private DieselLocomotive dieselLocomotive;

    /**
     * Constructs a new LocomotiveModel with the specified attributes.
     *
     * @param locomotiveModelId the unique identifier of the locomotive model
     * @param vehicle             the model details of the locomotive
     * @param power             the power output of the locomotive model
     */
    public LocomotiveModel(int locomotiveModelId, Vehicle vehicle, double power) {
        this.locomotiveModelId = locomotiveModelId;
        this.vehicle = vehicle;
        this.power = power;
    }

    /**
     * Returns the unique identifier of the locomotive model.
     *
     * @return the locomotive model ID
     */
    public int getLocomotiveModelId() {
        return locomotiveModelId;
    }

    /**
     * Returns the model details of the locomotive.
     *
     * @return the model
     */
    public Vehicle getVehicle() {
        return vehicle;
    }



    /**
     * Returns the power output of the locomotive model.
     *
     * @return the power output
     */
    public double getPower() {
        return power;
    }

    public void setElectricLocomotive(ElectricLocomotive electricLocomotive) {
        this.electricLocomotive = electricLocomotive;
    }

    public void setDieselLocomotive(DieselLocomotive dieselLocomotive) {
        this.dieselLocomotive = dieselLocomotive;
    }

    /**
     * Returns a string representation of the locomotive model.
     *
     * @return a formatted string containing the locomotive model details
     */
    @Override
    public String toString() {
        return String.format("Locomotive Model %d:\n Model: %s\n Power: %.3f", locomotiveModelId, vehicle, power);
    }
}