package pt.ipp.isep.dei.LAPR3.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrainSchedule {
    private final Train train;
    private final Path path;
    private final List<TrainSchedulePoint> points;
    private final List<SegmentOccupancy> segmentOccupancies;
    private double totalDistanceKm;
    private double totalDurationMinutes;
    private double effectiveSpeedKmH;

    public TrainSchedule(Train train, Path path) {
        this.train = train;
        this.path = path;
        this.points = new ArrayList<>();
        this.segmentOccupancies = new ArrayList<>();
    }

    public Train getTrain() { return train; }
    public Path getPath() { return path; }

    public void addPoint(TrainSchedulePoint point) {
        points.add(point);
    }

    public void addSegmentOccupancy(SegmentOccupancy occupancy) {
        segmentOccupancies.add(occupancy);
    }

    public void addDistance(double distanceKm) {
        totalDistanceKm += distanceKm;
    }

    public void addDuration(double minutes) {
        totalDurationMinutes += minutes;
    }

    // Return mutable lists for schedule adjustments
    public List<TrainSchedulePoint> getPoints() {
        return points;
    }

    public List<SegmentOccupancy> getSegmentOccupancies() {
        return segmentOccupancies;
    }

    public double getTotalDistanceKm() { return totalDistanceKm; }
    public double getTotalDurationMinutes() { return totalDurationMinutes; }
    public double getEffectiveSpeedKmH() { return effectiveSpeedKmH; }

    public void setEffectiveSpeedKmH(double speed) {
        this.effectiveSpeedKmH = speed;
    }

    public void setTotalDurationMinutes(double minutes) {
        this.totalDurationMinutes = minutes;
    }

    /**
     * Applies a delay starting from a specific facility, propagating to all subsequent points and segments.
     * The arrival time at the stop facility remains unchanged - only departure and subsequent times shift.
     *
     * @param startFacilityId The facility ID where the delay starts
     * @param delayMinutes The delay in minutes to apply
     */
    public void applyDelayFromFacility(int startFacilityId, long delayMinutes) {
        boolean foundStopFacility = false;
        boolean applyToSubsequent = false;

        // Update schedule points
        for (TrainSchedulePoint point : points) {
            if (point.getFacility().getFacilityID() == startFacilityId) {
                foundStopFacility = true;
                // At the stop facility: arrival stays the same, only departure changes
                if (point.getDepartureTime() != null) {
                    point.setDepartureTime(point.getDepartureTime().plusMinutes(delayMinutes));
                }
                applyToSubsequent = true;
                continue;
            }

            if (applyToSubsequent) {
                // For all subsequent points, delay both arrival and departure
                if (point.getArrivalTime() != null) {
                    point.setArrivalTime(point.getArrivalTime().plusMinutes(delayMinutes));
                }
                if (point.getDepartureTime() != null) {
                    point.setDepartureTime(point.getDepartureTime().plusMinutes(delayMinutes));
                }
            }
        }

        // Update segment occupancies (only segments AFTER the stop facility)
        boolean applyToSegments = false;
        for (SegmentOccupancy occ : segmentOccupancies) {
            // Segments starting from the stop facility get delayed
            if (occ.getFromFacility().getFacilityID() == startFacilityId) {
                applyToSegments = true;
            }
            if (applyToSegments) {
                occ.shiftTimes(delayMinutes);
            }
        }

        // Update total duration
        totalDurationMinutes += delayMinutes;
    }

    /**
     * Applies a delay starting from a specific facility, propagating to all subsequent points and segments.
     * The arrival time at the stop facility remains unchanged - only departure and subsequent times shift.
     * Preserves the relationship where arrival == departure for subsequent points.
     *
     * @param startFacilityId The facility ID where the delay starts
     * @param delayMinutes The delay in minutes to apply
     */
    public void applyDelayFromFacilityPreservingEquality(int startFacilityId, long delayMinutes) {
        boolean foundStopFacility = false;
        boolean applyToSubsequent = false;

        // Update schedule points
        for (TrainSchedulePoint point : points) {
            if (point.getFacility().getFacilityID() == startFacilityId) {
                foundStopFacility = true;
                // At the stop facility: arrival stays the same, only departure changes
                if (point.getDepartureTime() != null) {
                    point.setDepartureTime(point.getDepartureTime().plusMinutes(delayMinutes));
                }
                applyToSubsequent = true;
                continue;
            }

            if (applyToSubsequent) {
                // For all subsequent points, delay both arrival and departure
                // But preserve the relationship where arrival == departure
                boolean arrivalEqualsDeparture = point.getArrivalTime() != null && 
                                                 point.getDepartureTime() != null &&
                                                 point.getArrivalTime().equals(point.getDepartureTime());
                
                if (point.getArrivalTime() != null) {
                    point.setArrivalTime(point.getArrivalTime().plusMinutes(delayMinutes));
                }
                if (point.getDepartureTime() != null) {
                    point.setDepartureTime(point.getDepartureTime().plusMinutes(delayMinutes));
                }
                
                // If they were equal before, keep them equal
                if (arrivalEqualsDeparture && point.getArrivalTime() != null && point.getDepartureTime() != null) {
                    point.setDepartureTime(point.getArrivalTime());
                }
            }
        }

        // Update segment occupancies (only segments AFTER the stop facility)
        boolean applyToSegments = false;
        for (SegmentOccupancy occ : segmentOccupancies) {
            // Segments starting from the stop facility get delayed
            if (occ.getFromFacility().getFacilityID() == startFacilityId) {
                applyToSegments = true;
            }
            if (applyToSegments) {
                occ.shiftTimes(delayMinutes);
            }
        }

        // Update total duration
        totalDurationMinutes += delayMinutes;
    }

    /**
     * Mutable segment occupancy class to allow schedule adjustments
     */
    public static class SegmentOccupancy {
        private final LineSegment segment;
        private LocalDateTime entryTime;
        private LocalDateTime exitTime;
        private final Facility fromFacility;
        private final Facility toFacility;

        public SegmentOccupancy(LineSegment segment,
                                LocalDateTime entryTime,
                                LocalDateTime exitTime,
                                Facility fromFacility,
                                Facility toFacility) {
            this.segment = segment;
            this.entryTime = entryTime;
            this.exitTime = exitTime;
            this.fromFacility = fromFacility;
            this.toFacility = toFacility;
        }

        public LineSegment getSegment() { return segment; }
        public LocalDateTime getEntryTime() { return entryTime; }
        public LocalDateTime getExitTime() { return exitTime; }
        public Facility getFromFacility() { return fromFacility; }
        public Facility getToFacility() { return toFacility; }

        public void setEntryTime(LocalDateTime entryTime) {
            this.entryTime = entryTime;
        }

        public void setExitTime(LocalDateTime exitTime) {
            this.exitTime = exitTime;
        }

        /**
         * Shifts both entry and exit times by the specified number of minutes
         */
        public void shiftTimes(long minutes) {
            this.entryTime = this.entryTime.plusMinutes(minutes);
            this.exitTime = this.exitTime.plusMinutes(minutes);
        }
    }
}