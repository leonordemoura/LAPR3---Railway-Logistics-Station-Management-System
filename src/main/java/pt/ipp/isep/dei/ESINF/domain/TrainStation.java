package pt.ipp.isep.dei.ESINF.domain;

public class TrainStation implements Comparable<TrainStation> {
    private String country;
    private String timeZone;
    private String timeZoneGroup;
    private String station;
    private GeoCoordinate coordinates;
    private boolean isCity;
    private boolean isMainStation;
    private boolean isAirport;

    public TrainStation(String country, String timeZone, String timeZoneGroup, String station,
                        GeoCoordinate coordinates, boolean isCity, boolean isMainStation, boolean isAirport) {
        this.country = country;
        this.timeZone = timeZone;
        this.timeZoneGroup = timeZoneGroup;
        this.station = station;
        this.coordinates = coordinates;
        this.isCity = isCity;
        this.isMainStation = isMainStation;
        this.isAirport = isAirport;
    }

    public String getCountry() {
        return country;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getTimeZoneGroup() {
        return timeZoneGroup;
    }

    public String getTimeZoneGroupAndCountry() {
        return timeZoneGroup + "_" + country;
    }

    public String getStation() {
        return station;
    }

    public GeoCoordinate getCoordinates() {
        return coordinates;
    }

    public boolean isCity() {
        return isCity;
    }

    public boolean isMainStation() {
        return isMainStation;
    }

    public boolean isAirport() {
        return isAirport;
    }

    @Override
    public String toString() {
        return "TrainStation{" +
                "country='" + country + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", timeZoneGroup='" + timeZoneGroup + '\'' +
                ", station='" + station + '\'' +
                ", latitude=" + coordinates.getLatitude() +
                ", longitude=" + coordinates.getLongitude() +
                ", isCity=" + isCity +
                ", isMainStation=" + isMainStation +
                ", isAirport=" + isAirport +
                '}';
    }

    @Override
    public int compareTo(TrainStation o) {
        return this.station.compareTo(o.station);
    }
}
