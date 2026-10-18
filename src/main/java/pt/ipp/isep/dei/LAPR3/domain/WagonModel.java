package pt.ipp.isep.dei.LAPR3.domain;

import pt.ipp.isep.dei.LAPR3.domain.constants.WagonType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a wagon model with specific attributes, including type, maker, and physical characteristics.
 */
public class WagonModel {
    private int wagonModelID;
    private Vehicle vehicle;
    private WagonType wagonType;
    private WagonMaker wagonMaker;
    private double tareTons;
    private int maxPayload;
    private double volume;


    /**
     * Constructs a new WagonModel with the specified attributes.
     *
     * @param wagonModelID the unique identifier of the wagon model
     * @param vehicle        the model details of the wagon
     * @param wagonType    the type of the wagon
     * @param wagonMaker   the manufacturer of the wagon
     * @param tareTons     the tare weight of the wagon model in tons
     * @param payload      the payload capacity of the wagon model
     * @param volume       the volume capacity of the wagon model
     */
    public WagonModel(int wagonModelID, Vehicle vehicle, WagonType wagonType, WagonMaker wagonMaker, double tareTons, int payload, double volume) {
        this.wagonModelID = wagonModelID;
        this.vehicle = vehicle;
        this.wagonType = wagonType;
        this.wagonMaker = wagonMaker;
        this.tareTons = tareTons;
        this.maxPayload = payload;
        this.volume = volume;
    }

    /**
     * Returns the unique identifier of the wagon model.
     *
     * @return the wagon model ID
     */
    public int getWagonModelID() {
        return wagonModelID;
    }

    /**
     * Returns the model details of the wagon.
     *
     * @return the model
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Returns the type of the wagon.
     *
     * @return the wagon type
     */
    public WagonType getWagonType() {
        return wagonType;
    }

    /**
     * Returns the manufacturer of the wagon.
     *
     * @return the wagon maker
     */
    public WagonMaker getWagonMaker() {
        return wagonMaker;
    }

    /**
     * Returns the tare weight of the wagon model in tons.
     *
     * @return the tare weight in tons
     */
    public double getTareTons() {
        return tareTons;
    }

    /**
     * Returns the payload capacity of the wagon model.
     *
     * @return the payload capacity
     */
    public int getPayload() {
        return maxPayload;
    }

    /**
     * Returns the volume capacity of the wagon model.
     *
     * @return the volume capacity
     */
    public double getVolume() {
        return volume;
    }

    /**
     * Returns a string representation of the wagon model.
     *
     * @return a formatted string containing the wagon model's details
     */
    @Override
    public String toString() {
        return String.format("Wagon Model %d:\n Model: %s\n Type: %s\n Maker: %s\n Tare Tons: %.3f\n PayLoad: %d\n Volume: %d", wagonModelID, vehicle, wagonType, wagonMaker, tareTons, maxPayload, volume);
    }
}