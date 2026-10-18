package pt.ipp.isep.dei.ESINF.domain;

public class Weight {
    private double distance;
    private double capacity;
    private double cost;

    public Weight(double distance, double capacity, double cost) {
        this.distance = distance;
        this.capacity = capacity;
        this.cost = cost;
    }

    public double getDistance() {
        return distance;
    }

    public double getCapacity() {
        return capacity;
    }

    public double getCost() {
        return cost;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return String.format("Weight\n Distance: %.2f\n Capacity: %.2f\n Cost: %.2f", distance, capacity, cost);
    }
}
