package pt.ipp.isep.dei.LAPR3.domain;

/**
 * Represents a railway line connecting two facilities, owned by a line owner and associated with a gauge.
 */
public class Lines {
    public int lineID;
    public Facility startFacility;
    public Facility endFacility;
    public LineOwner lineOwner;
    public Gauge gauge;
    public boolean isElectrified;

    /**
     * Constructs a new Lines instance with the specified attributes.
     *
     * @param lineID        the unique identifier of the line
     * @param startFacility the starting facility of the line
     * @param endFacility   the ending facility of the line
     * @param lineOwner     the owner of the line
     * @param gauge         the gauge associated with the line
     */
    public Lines(int lineID, Facility startFacility, Facility endFacility, LineOwner lineOwner, Gauge gauge, boolean isElectrified) {
        this.lineID = lineID;
        this.startFacility = startFacility;
        this.endFacility = endFacility;
        this.lineOwner = lineOwner;
        this.gauge = gauge;
        this.isElectrified = isElectrified;
    }

    /**
     * Returns the unique identifier of the line.
     *
     * @return the line ID
     */
    public int getLineID() {
        return lineID;
    }

    /**
     * Returns the ID of the starting facility.
     *
     * @return the starting facility ID
     */
    public int getStartFacilityID() {
        return startFacility.getFacilityID();
    }

    /**
     * Returns the ID of the ending facility.
     *
     %.```java
     * @return the ending facility ID
     */
    public int getEndFacilityID() {
        return endFacility.getFacilityID();
    }

    /**
     * Returns the ID of the line owner.
     *
     * @return the line owner ID
     */
    public int getLineOwnerID() {
        return lineOwner.getLineOwnerID();
    }

    /**
     * Returns the ID of the gauge associated with the line.
     *
     * @return the gauge ID
     */
    public int getGaugeID() {
        return gauge.getDimension();
    }

    public boolean isElectrified() {
        return isElectrified;
    }

    /**
     * Returns a string representation of the line.
     *
     * @return a formatted string containing the line ID, start facility name, end facility name, line owner name, and gauge dimension
     */
    @Override
    public String toString() {
        return String.format("Line %d: \nStart Facility: %s\n End Facility: %s\n LineOwner: %s\n Gauge: %d", lineID, startFacility.getName(), endFacility.getName(), lineOwner.getName(), gauge.getDimension());
    }
}