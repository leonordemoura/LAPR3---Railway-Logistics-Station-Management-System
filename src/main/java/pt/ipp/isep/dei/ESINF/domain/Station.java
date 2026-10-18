package pt.ipp.isep.dei.ESINF.domain;

import java.util.Objects;

public class Station {
    public int stationID;
    public String name;
    public GeoCoordinate location;
    public Coordinate coordinate;

    public Station(int stationID, String name, GeoCoordinate location, Coordinate coordinate) {
        this.stationID = stationID;
        this.name = name;
        this.location = location;
        this.coordinate = coordinate;
    }

    public int getStationID() {
        return stationID;
    }

    public String getName() {
        return name;
    }

    public GeoCoordinate getLocation() {
        return location;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setStationID(int stationID) {
        this.stationID = stationID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(GeoCoordinate location) {
        this.location = location;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;

        // Apenas o ID é usado para definir se duas estações são a mesma entidade.
        return stationID == station.stationID;
    }

    /**
     * CORREÇÃO: Usa apenas o stationID para gerar o hash.
     * Isto é vital para o correto funcionamento de HashMap e HashSet.
     */
    @Override
    public int hashCode() {
        // Gera o hash APENAS a partir do stationID.
        return Objects.hash(stationID);
    }
    @Override
    public String toString() {
        return String.format("Station: %d\n Name: %s\n Location: %s\n Coordinate : %s", stationID, name, location, coordinate);
    }
}
