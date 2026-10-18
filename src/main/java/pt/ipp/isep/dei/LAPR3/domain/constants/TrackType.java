package pt.ipp.isep.dei.LAPR3.domain.constants;

public enum TrackType {
    SINGLE("Simple Track"), DOUBLE("Double Track");

    private final String name;

    TrackType(String name){
        this.name = name;
    }
}
