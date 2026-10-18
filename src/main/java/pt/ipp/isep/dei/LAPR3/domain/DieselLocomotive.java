package pt.ipp.isep.dei.LAPR3.domain;

public class DieselLocomotive {
    private LocomotiveModel locomotiveModel;
    private int fuelCapacity;

    public DieselLocomotive(LocomotiveModel locomotiveModel, int fuelCapacity) {
        this.locomotiveModel = locomotiveModel;
        this.fuelCapacity = fuelCapacity;
    }

    public LocomotiveModel getLocomotiveModel() {
        return locomotiveModel;
    }

    public double getFuelCapacity() {
        return fuelCapacity;
    }


}
