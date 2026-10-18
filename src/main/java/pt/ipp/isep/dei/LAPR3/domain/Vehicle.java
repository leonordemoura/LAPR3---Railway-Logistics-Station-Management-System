package pt.ipp.isep.dei.LAPR3.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a model with specific attributes such as ID, name, and physical dimensions.
 */
public class Vehicle {
    private int vehicleID;
    private String vehicleName;
    private List<Gauge> gauges;
    private double weight;
    private double width;
    private double height;
    private double length;
    private int numberOfWheels;
    private int maxSpeed;

    /**
     * Constructs a new Model with the specified attributes.
     *
     * @param vehicleID   the unique identifier of the model
     * @param vehicleName the name of the model
     * @param weight    the weight of the model
     * @param width     the width of the model
     * @param height    the height of the model
     * @param length    the length of the model
     */
    public Vehicle(int vehicleID, String vehicleName, double weight, double width, double height, double length, int numberOfWheels, int maxSpeed) {
        this.vehicleID = vehicleID;
        this.vehicleName = vehicleName;
        this.weight = weight;
        this.width = width;
        this.height = height;
        this.length = length;
        this.numberOfWheels = numberOfWheels;
        this.maxSpeed = maxSpeed;
        this.gauges = new ArrayList<>();
    }

    /**
     * Returns the unique identifier of the model.
     *
     * @return the model ID
     */
    public int getVehicleID() {
        return vehicleID;
    }

    /**
     * Returns the name of the model.
     *
     * @return the model name
     */
    public String getVehicleName() {
        return vehicleName;
    }

    /**
     * Returns the weight of the model.
     *
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Returns the width of the model.
     *
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the height of the model.
     *
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Returns the length of the model.
     *
     * @return the length
     */
    public double getLength() {
        return length;
    }

    public int getNumberOfWheels() {
        return numberOfWheels;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public List<Gauge> getGauges() {
        return gauges;
    }

    public void addGauge(Gauge gauge) {
        gauges.add(gauge);
    }

    /**
     * Returns a string representation of the model.
     *
     * @return a formatted string containing the model ID, name, weight, width, and height
     */
    @Override
    public String toString() {
        return String.format("Model %d\n Name: %s\n Weight: %.3f\n Width: %.3f\n Height: %.3f", vehicleID, vehicleName, weight, width, height);
    }
}