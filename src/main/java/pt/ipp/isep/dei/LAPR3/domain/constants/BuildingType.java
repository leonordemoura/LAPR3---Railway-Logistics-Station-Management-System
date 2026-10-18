package pt.ipp.isep.dei.LAPR3.domain.constants;

public enum BuildingType {
    WAREHOUSE(1, "Warehouse"),
    GRAIN_SILO(2, "Grain Silo"),
    REFRIGERATED_AREA(3, "Refrigerated Area");

    private int buildingTypeId;
    private String buildingTypeName;

     BuildingType(int buildingTypeId, String buildingTypeName) {
        this.buildingTypeId = buildingTypeId;
        this.buildingTypeName = buildingTypeName;
    }

    public int getBuildingTypeId() {
        return buildingTypeId;
    }
}
