package pt.ipp.isep.dei.LAPR3.domain;

import pt.ipp.isep.dei.LAPR3.domain.constants.BuildingType;

public class Building {
    private int buildingID;
    private BuildingType buildingType;
    private Facility facility;

    public Building(int buildingID, BuildingType buildingType, Facility facility) {
        this.buildingID = buildingID;
        this.buildingType = buildingType;
        this.facility = facility;
    }

    public int getBuildingID() {
        return buildingID;
    }

    public BuildingType getBuildingType() {
        return buildingType;
    }

    public Facility getFacility() {
        return facility;
    }
}
