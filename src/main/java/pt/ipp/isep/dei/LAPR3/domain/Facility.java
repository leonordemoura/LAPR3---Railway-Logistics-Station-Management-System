package pt.ipp.isep.dei.LAPR3.domain;

import pt.ipp.isep.dei.LAPR3.domain.constants.FacilityType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a facility in the system with specific attributes and capabilities.
 */
public class Facility {
    private int facilityID;
    private FacilityType facilityType;
    private String name;
    private List<Building> buildings;

    /**
     * Constructs a new Facility with the specified attributes.
     *
     * @param facilityID         the unique identifier of the facility
     * @param name               the name of the facility
     */
    public Facility(int facilityID, String name, FacilityType facilityType) {
        this.facilityID = facilityID;
        this.name = name;
        this.facilityType = facilityType;
        this.buildings = new ArrayList<>();
    }

    /**
     * Returns the unique identifier of the facility.
     *
     * @return the facility ID
     */
    public int getFacilityID() {
        return facilityID;
    }

    /**
     * Returns the type of the facility.
     *
     * @return the facility type
     */
    public FacilityType getFacilityType() {
        return facilityType;
    }

    /**
     * Returns the name of the facility.
     *
     * @return the facility name
     */
    public String getName() {
        return name;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public void setFacilityType(FacilityType facilityType) {
        this.facilityType = facilityType;
    }

    public void addBuilding(Building building) {
        this.buildings.add(building);
    }

    /**
     * Returns a string representation of the facility.
     *
     * @return a formatted string containing the facility ID, type, and name
     */
    @Override
    public String toString() {
        return String.format("%s", name);
    }
}
