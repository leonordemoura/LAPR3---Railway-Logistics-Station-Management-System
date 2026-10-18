package pt.ipp.isep.dei.LAPR3.domain.constants;

public enum Status {
    ASSIGNED("Assigned"),
    AVAILABLE("Available"),
    PLANNED("Planned"),
    RUNNING("Running");


    private String name;

    Status(String name) {
        this.name = name;
    }
}
