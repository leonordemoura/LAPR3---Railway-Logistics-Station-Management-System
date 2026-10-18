package pt.ipp.isep.dei.LAPR3.domain.constants;

/**
 * Enum representing different types of wagons in the system.
 */
public enum WagonType {
    BOXCAR("Boxcar"),
    TANK_CAR("Tank Car"),
    FLATCAR("Flatcar"),
    HOPPER_CAR("Hopper Car"),
    REFRIGERATED_CAR("Refrigerated Yard");

    private final String name;

    /**
     * Constructs a WagonType with the specified name.
     *
     * @param name the name of the wagon type
     */
    WagonType(String name) {
        this.name = name;
    }

    /**
     * Returns a string representation of the wagon type.
     *
     * @return a formatted string containing the wagon type name
     */
    @Override
    public String toString() {
        return String.format("Wagon Type: %s", name);
    }
}
