package pt.ipp.isep.dei.ESINF.domain;

import java.util.Objects;

/**
 * Class that represents the coordinate of a train station.
 */
public class GeoCoordinate {
    /** latitude of a station*/
    private final Double latitude;
    /** longitude of a station*/
    private final Double longitude;

    /**
     * Constructor of a Coordinate
     *
     * @param latitude the latitude of a Station
     * @param longitude the longitude of a Station
     */
    public GeoCoordinate(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sees if an object is the same as another
     *
     * @param o A different object to compare to
     * @return returns true if the latitude and longitude of each is the same, or false if otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoCoordinate that = (GeoCoordinate) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    /**
     * The standard format for this Class
     */
    @Override
    public String toString() {
        return String.format("(latitude=%f, longitude=%f)", latitude, longitude);
    }
}
