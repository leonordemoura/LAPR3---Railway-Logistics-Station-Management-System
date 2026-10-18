package pt.ipp.isep.dei.LAPR3.domain;

public class Siding {
    private int sidingID;
    private LineSegment lineSegment;
    private int position;
    private int length;

    public Siding(int sidingID, LineSegment lineSegment, int position, int length) {
        this.sidingID = sidingID;
        this.lineSegment = lineSegment;
        this.position = position;
        this.length = length;
    }

    public int getSidingID() {
        return sidingID;
    }

    public LineSegment getLineSegment() {
        return lineSegment;
    }

    public int getPosition() {
        return position;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return String.format("Siding %d:\n Line Segment %d\n Position: %d\n Length: %d", sidingID, lineSegment.getSegmentID(), position, length);
    }
}
