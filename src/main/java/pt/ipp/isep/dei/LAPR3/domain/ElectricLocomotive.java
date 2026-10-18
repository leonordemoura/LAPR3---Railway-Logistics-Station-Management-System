package pt.ipp.isep.dei.LAPR3.domain;

public class ElectricLocomotive {
    private LocomotiveModel locomotiveModel;
    private int frequency;
    private int voltage;

    public ElectricLocomotive(LocomotiveModel locomotiveModel, int frequency, int voltage) {
        this.locomotiveModel = locomotiveModel;
        this.frequency = frequency;
        this.voltage = voltage;
    }

    public LocomotiveModel getLocomotiveModel() {
        return locomotiveModel;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getVoltage() {
        return voltage;
    }
}
