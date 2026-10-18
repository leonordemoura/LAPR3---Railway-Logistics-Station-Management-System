package pt.ipp.isep.dei.LAPR3.domain;

import pt.ipp.isep.dei.LAPR3.domain.constants.TrackType;

/**
 * Represents a segment of a railway line with specific attributes.
 */
public class LineSegment {
    private int segmentID;
    private Lines line;
    private TrackType trackType;
    private int orderLine;
    private double weight;
    private double length;
    private boolean maintenanceStatus;
    private int speedLimit;
    private boolean hasSiding;

    /**
     * Constructs a new LineSegment with the specified attributes.
     *
     * @param segmentID        the unique identifier of the line segment
     * @param line             the railway line this segment belongs to
     * @param orderLine        the order of the segment within the line
     * @param weight           the weight capacity of the segment
     * @param length           the length of the segment
     * @param maintenanceStatus indicates whether the segment is under maintenance
     * @param speedLimit       the speed limit of the segment
     */
    public LineSegment(int segmentID, Lines line, int orderLine, double weight, double length, boolean maintenanceStatus, int speedLimit) {
        this.segmentID = segmentID;
        this.line = line;
        this.orderLine = orderLine;
        this.weight = weight;
        this.length = length;
        this.maintenanceStatus = maintenanceStatus;
        this.speedLimit = speedLimit;
        this.trackType = TrackType.DOUBLE;
        this.hasSiding = false;
    }

    /**
     * Returns the unique identifier of the line segment.
     *
     * @return the segment ID
     */
    public int getSegmentID() {
        return segmentID;
    }

    /**
     * Returns the ID of the railway line this segment belongs to.
     *
     * @return the line ID
     */
    public int getLineID() {
        return line.getLineID();
    }

    /**
     * Returns the track type of this segment.
     *
     * @return the track type
     */
    public TrackType getTrackType() {
        return trackType;
    }

    public void setTrackType(TrackType trackType) {
        this.trackType = trackType;
    }

    /**
     * Returns the order of the segment within the line.
     *
     * @return the order line
     */
    public int getOrderLine() {
        return orderLine;
    }

    /**
     * Returns the weight capacity of the segment.
     *
     * @return the weight capacity
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Returns the length of the segment.
     *
     * @return the segment length
     */
    public double getLength() {
        return length;
    }

    /**
     * Checks if the segment is under maintenance.
     *
     * @return true if the segment is under maintenance, false otherwise
     */
    public boolean getMaintenanceStatus() {
        return maintenanceStatus;
    }

    /**
     * Returns the speed limit of the segment.
     *
     * @return the speed limit
     */
    public int getSpeedLimit() {
        return speedLimit;
    }

    /**
     * Checks if the segment has sidings.
     *
     * @return true if the segment has sidings, false otherwise
     */
    public boolean hasSiding() {
        return hasSiding;
    }

    /**
     * Sets whether the segment has sidings.
     *
     * @param hasSiding true if the segment has sidings, false otherwise
     */
    public void setHasSiding(boolean hasSiding) {
        this.hasSiding = hasSiding;
    }

    /**
     * Returns a string representation of the line segment.
     *
     * @return a formatted string containing the segment's details
     */
    @Override
    public String toString() {
        return String.format("Line Segment %d:\n Line ID: %d\n Order Line: %d\n Weight: %.3f\n Length: %.3f\n Maintenance Status: %b\n Speed Limit: %d\n Track Type: %s\n Has Sidings: %b",
                segmentID, getLineID(), orderLine, weight, length, maintenanceStatus, speedLimit, trackType, hasSiding);
    }
}