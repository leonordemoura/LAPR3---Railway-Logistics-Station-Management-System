package pt.ipp.isep.dei.LAPR3.domain.constants;

/**
 * Enum representing different types of facilities in the system.
 */
public enum FacilityType {
    STATION(1, "Station", false),
    TERMINAL(2, "Terminal", true),
    FREIGHT_YARD(3, "Freight Yard", false);

    private final String name;
    private int id;
    private final boolean isIntermodal;

    /**
     * Constructs a FacilityType with the specified name and intermodal status.
     *
     * @param name        the name of the facility type
     * @param isIntermodal indicates whether the facility supports intermodal transport
     */
    FacilityType(int id, String name, boolean isIntermodal) {
        this.id = id;
        this.name = name;
        this.isIntermodal = isIntermodal;
    }

    /**
     * Gets the id of the facility type
     *
     * @return id of the facility Type
     */
    public int getId() {
        return id;
    }

    public static FacilityType getFacilityTypeById(int id) {
        for(FacilityType facilityType : FacilityType.values()) {
            if (id > 2) {
                id -= 1;
                if (facilityType.getId() == id) {

                    return facilityType;
                }
            }
        }
        return null;
    }

    /**
     * Returns a string representation of the facility type.
     *
     * @return a formatted string containing the facility type name
     */
    @Override
    public String toString() {
        return String.format("FacilityType: %s", name);
    }
}
