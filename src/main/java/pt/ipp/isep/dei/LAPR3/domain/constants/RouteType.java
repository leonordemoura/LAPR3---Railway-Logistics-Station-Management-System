package pt.ipp.isep.dei.LAPR3.domain.constants;

public enum RouteType {
    SIMPLE("Simple"),
    COMPLEX("Complex");

    private String type;

    RouteType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}
