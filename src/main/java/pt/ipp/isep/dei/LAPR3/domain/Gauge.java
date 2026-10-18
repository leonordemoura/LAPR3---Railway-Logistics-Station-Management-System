package pt.ipp.isep.dei.LAPR3.domain;

/**
 * Represents a gauge with a unique identifier and dimension.
 */
public class Gauge {
    private int dimension;
    private String name;

    /**
     * Constructs a new Gauge with the specified ID and dimension.
     *
     * @param dimension the dimension of the gauge
     */
    public Gauge(int dimension, String name) {
        this.name = name;
        this.dimension = dimension;
    }

    /**
     * Returns the dimension of the gauge.
     *
     * @return the gauge dimension
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * Returns a string representation of the gauge.
     *
     * @return a formatted string containing the gauge ID and dimension
     */
    @Override
    public String toString() {
        return String.format("Size: %d", dimension);
    }
}
