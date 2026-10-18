package pt.ipp.isep.dei.LAPR3.domain;

import java.time.LocalDateTime;

public class ConflictInfo {
    public final TrainSchedule schedule1;
    public final TrainSchedule schedule2;
    public final LineSegment segment;
    public final LineSegment originalSegment; // Original segment where conflict occurs (for siding conflicts)
    public final LocalDateTime conflictTime;
    public final Facility location;
    public final boolean isStationConflict;
    public final boolean hasSiding;
    public final int proceedingTrainId;
    public final int waitingTrainId;
    public final TrainSchedulePoint point1;
    public final TrainSchedulePoint point2;
    public final TrainSchedule.SegmentOccupancy occ1;
    public final TrainSchedule.SegmentOccupancy occ2;

    // For station conflicts
    public ConflictInfo(TrainSchedule s1, TrainSchedule s2, LineSegment seg,
                 LocalDateTime time, Facility loc, boolean station, boolean siding,
                 int proceedingId, int waitingId, TrainSchedulePoint p1, TrainSchedulePoint p2) {
        this.schedule1 = s1;
        this.schedule2 = s2;
        this.segment = seg;
        this.originalSegment = seg;
        this.conflictTime = time;
        this.location = loc;
        this.isStationConflict = station;
        this.hasSiding = siding;
        this.proceedingTrainId = proceedingId;
        this.waitingTrainId = waitingId;
        this.point1 = p1;
        this.point2 = p2;
        this.occ1 = null;
        this.occ2 = null;
    }

    // For segment conflicts
    public ConflictInfo(TrainSchedule s1, TrainSchedule s2, LineSegment seg, LineSegment originalSeg,
                 LocalDateTime time, Facility loc, boolean station, boolean siding,
                 int proceedingId, int waitingId, TrainSchedule.SegmentOccupancy o1, TrainSchedule.SegmentOccupancy o2) {
        this.schedule1 = s1;
        this.schedule2 = s2;
        this.segment = seg;
        this.originalSegment = originalSeg;
        this.conflictTime = time;
        this.location = loc;
        this.isStationConflict = station;
        this.hasSiding = siding;
        this.proceedingTrainId = proceedingId;
        this.waitingTrainId = waitingId;
        this.point1 = null;
        this.point2 = null;
        this.occ1 = o1;
        this.occ2 = o2;
    }
}
