package pt.ipp.isep.dei.ESINF.domain;

public class Line {
    private Station departureStation;
    private Station arrivalStation;
    private Weight weight;

    public Line(Station departureStation, Station arrivalStation, Weight weight) {
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.weight = weight;
    }

    public Station getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(Station departureStation) {
        this.departureStation = departureStation;
    }

    public Station getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(Station arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public Weight getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return String.format("Line\n Departure Station: %s\n Arrival Station: %s\n Distance: %.10f\n Capacity: %.10f\n Cost: %.10f\n", departureStation, arrivalStation, weight.getDistance(), weight.getCapacity(), weight.getCost());
    }
}
