package pt.ipp.isep.dei.LAPR3.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a travel time calculation, including details about the route, locomotive, and segments.
 */
public class TravelTimeResult {
    private Facility startFacility;
    private Facility endFacility;
    private Locomotive locomotive;
    private Lines line;
    private List<LineSegment> segments;
    private List<SegmentTravelInfo> segmentInfos;
    private double totalTimeMinutes;
    private double totalDistanceKm;

    /**
     * Constructs a new TravelTimeResult with the specified attributes.
     *
     * @param startFacility    the starting facility of the travel
     * @param endFacility      the ending facility of the travel
     * @param locomotive       the locomotive used for the travel
     * @param segments         the list of line segments in the route
     * @param line             the railway line associated with the travel
     * @param totalTimeMinutes the total travel time in minutes
     * @param totalDistanceKm  the total travel distance in kilometers
     */
    public TravelTimeResult(Facility startFacility, Facility endFacility, Locomotive locomotive, List<LineSegment> segments, Lines line, double totalTimeMinutes, double totalDistanceKm) {
        this.startFacility = startFacility;
        this.endFacility = endFacility;
        this.locomotive = locomotive;
        this.line = line;
        this.segments = segments;
        this.segmentInfos = new ArrayList<>();
        this.totalTimeMinutes = totalTimeMinutes;
        this.totalDistanceKm = totalDistanceKm;
    }

    public Facility getStartFacility() {
        return startFacility;
    }

    public Facility getEndFacility() {
        return endFacility;
    }

    public Locomotive getLocomotive() {
        return locomotive;
    }

    public Lines getLine() {
        return line;
    }

    public List<LineSegment> getSegments() {
        return segments;
    }

    public List<SegmentTravelInfo> getSegmentsInfos() {
        return segmentInfos;
    }

    public void setStartFacility(Facility startFacility) {
        this.startFacility = startFacility;
    }

    public void setEndFacility(Facility endFacility) {
        this.endFacility = endFacility;
    }

    public void setLocomotive(Locomotive locomotive) {
        this.locomotive = locomotive;
    }

    public void setLine(Lines line) {
        this.line = line;
    }

    public void setSegments(List<LineSegment> segments) {
        this.segments = segments;
    }

    /**
     * Sets the total travel time in minutes.
     *
     * @param totalTimeMinutes the total travel time in minutes
     */
    public void setTotalTimeMinutes(double totalTimeMinutes) {
        this.totalTimeMinutes = totalTimeMinutes;
    }

    /**
     * Sets the total travel distance in kilometers.
     *
     * @param totalDistanceKm the total travel distance in kilometers
     */
    public void setTotalDistanceKm(double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    /**
     * Returns the total travel time in hours.
     *
     * @return the total travel time in hours
     */
    public double getTotalTimeHours() {
        return totalTimeMinutes / 60.0;
    }

    /**
     * Returns the total distance travel.
     *
     * @return the total distance travel
     */
    public double getTotalDistanceKm() {return totalDistanceKm;}

    /**
     * Returns the total travel time in minutes.
     *
     * @return the total travel time in minutes
     */
    public double getTotalTimeMinutes(){
        return totalTimeMinutes;
    }

    /**
     * Returns the list of the segments' info.
     *
     * @return the list of segments' info
     */
    public List<SegmentTravelInfo> getSegmentInfos() {
        return segmentInfos;
    }

    /**
     * Returns a detailed string representation of the travel time result, including segment information.
     *
     * @return a formatted string with detailed travel information
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();

        sb.append("========================================================\n");
        sb.append("         TRAVEL TIME CALCULATION REPORT (USLP03)\n");
        sb.append("========================================================\n");
        sb.append(String.format("From:       %s (ID: %d)%n", startFacility.getName(), startFacility.getFacilityID()));
        sb.append(String.format("To:         %s (ID: %d)%n", endFacility.getName(), endFacility.getFacilityID()));
        sb.append(String.format("Line:       %d%n", line.getLineID()));
        sb.append("--------------------------------------------------------\n");
        sb.append(String.format("Locomotive: %s (ID: %d)%n", locomotive.getLocomotiveName(), locomotive.getLocomotiveId()));
        sb.append(String.format("Model:      %s%n", locomotive.getLocomotiveModelID().getVehicle().getVehicleName()));
        sb.append(String.format("Make:       %s%n", locomotive.getMake().getMakeName()));
        sb.append(String.format("Op. Speed:  %.3f km/h%n", locomotive.getOperationalSpeed()));
        sb.append("========================================================\n\n");

        sb.append("Segments covered (in order):\n");
        sb.append(String.format("%-10s | %-12s | %-10s | %-10s | %-12s%n",
                "Segment", "Length (km)", "Speed Limit", "Effective", "Time (min)"));
        sb.append("-------------------------------------------------------------------------\n");

        for (SegmentTravelInfo info : segmentInfos) {
            sb.append(String.format("%-10d | %-12.2f | %-10d | %-10.1f | %-12.2f%n",
                    info.segment.getSegmentID(),
                    info.segment.getLength() / 1000.0,
                    info.segment.getSpeedLimit(),
                    info.effectiveSpeed,
                    info.timeInMinutes));
        }

        sb.append("========================================================\n");
        sb.append(String.format("Total Distance:       %.2f km%n", totalDistanceKm));
        sb.append(String.format("Total Travel Time:    %.2f minutes %n",
                totalTimeMinutes));
        sb.append("========================================================\n");

        return sb.toString();
    }

    /**
     * Returns a concise string representation of the travel time result.
     *
     * @return a formatted string with total travel time and distance
     */
    @Override
    public String toString() {
        return String.format("Travel Time: %.2f minutes | Distance: %.2f km",
                totalTimeMinutes, getTotalTimeHours(), totalDistanceKm);
    }

    /**
     * Inner class to hold segment-specific travel information.
     */
    public static class SegmentTravelInfo {
        public LineSegment segment;
        public double effectiveSpeed;
        public double timeInMinutes;
    }
}