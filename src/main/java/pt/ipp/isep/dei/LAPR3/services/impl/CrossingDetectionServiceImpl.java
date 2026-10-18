package pt.ipp.isep.dei.LAPR3.services.impl;

import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.LAPR3.domain.*;
import pt.ipp.isep.dei.LAPR3.domain.constants.FacilityType;
import pt.ipp.isep.dei.LAPR3.domain.constants.TrackType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for detecting and resolving train crossing conflicts.
 */
public class CrossingDetectionServiceImpl {

    private final RailwayRepository repository;

    public CrossingDetectionServiceImpl() {
        this.repository = Repositories.getInstance().getRailwayRepository();
    }

    public List<CrossingOperation> detectAndResolveCrossings(List<TrainSchedule> schedules) {
        List<CrossingOperation> crossingOperations = new ArrayList<>();
        if (schedules == null || schedules.size() < 2) return crossingOperations;

        Set<Integer> trainsThatMustWait = new HashSet<>();
        Set<String> resolvedCrossings = new HashSet<>();

        // Iteratively resolve conflicts
        for (int iteration = 0; iteration < 10; iteration++) {
            List<ConflictInfo> allConflicts = new ArrayList<>();

            collectStationConflicts(schedules, allConflicts);
            collectSegmentConflicts(schedules, allConflicts, trainsThatMustWait);
            collectSameDirectionConflicts(schedules, allConflicts, resolvedCrossings);

            if (allConflicts.isEmpty()) break;

            allConflicts.sort(Comparator.comparing(c -> c.conflictTime));

            boolean hadChanges = false;
            for (ConflictInfo conflict : allConflicts) {
                String crossingKey = conflict.proceedingTrainId + "-" + conflict.waitingTrainId;
                if (resolvedCrossings.contains(crossingKey) ||
                        trainsThatMustWait.contains(conflict.proceedingTrainId)) {
                    continue;
                }

                if (conflict.isStationConflict) {
                    resolveStationConflict(conflict, crossingOperations, trainsThatMustWait, resolvedCrossings);
                } else {
                    resolveSegmentConflict(conflict, crossingOperations, trainsThatMustWait, resolvedCrossings);
                }
                hadChanges = true;
            }

            if (!hadChanges) break;
        }

        return crossingOperations;
    }

    private void collectStationConflicts(List<TrainSchedule> schedules, List<ConflictInfo> conflicts) {
        for (int i = 0; i < schedules.size(); i++) {
            for (int j = i + 1; j < schedules.size(); j++) {
                checkStationConflict(schedules.get(i), schedules.get(j), conflicts);
            }
        }
    }

    private void checkStationConflict(TrainSchedule s1, TrainSchedule s2, List<ConflictInfo> conflicts) {
        for (int p1 = 0; p1 < s1.getPoints().size() - 1; p1++) {
            TrainSchedulePoint point1 = s1.getPoints().get(p1);
            TrainSchedulePoint next1 = s1.getPoints().get(p1 + 1);

            if (point1.getFacility().getFacilityType() != FacilityType.STATION) continue;

            for (int p2 = 0; p2 < s2.getPoints().size() - 1; p2++) {
                TrainSchedulePoint point2 = s2.getPoints().get(p2);
                TrainSchedulePoint next2 = s2.getPoints().get(p2 + 1);

                if (!point1.getFacility().equals(point2.getFacility())) continue;

                LocalDateTime arr1 = point1.getArrivalTime();
                LocalDateTime dep1 = point1.getDepartureTime() != null ? point1.getDepartureTime() : arr1;
                LocalDateTime arr2 = point2.getArrivalTime();
                LocalDateTime dep2 = point2.getDepartureTime() != null ? point2.getDepartureTime() : arr2;

                if (!timesOverlap(arr1, dep1, arr2, dep2)) continue;

                LineSegment seg1 = findSegmentBetween(s1, point1.getFacility(), next1.getFacility());
                LineSegment seg2 = findSegmentBetween(s2, point2.getFacility(), next2.getFacility());

                if (seg1 == null || seg2 == null || seg1.getLineID() != seg2.getLineID() ||
                        seg1.getTrackType() != TrackType.SINGLE) continue;

                // Skip opposite direction (handled by segment conflicts)
                if (!next1.getFacility().equals(next2.getFacility())) continue;

                // Skip if siding available
                if (!repository.getSidingsBySegmentId(seg1.getSegmentID()).isEmpty()) continue;

                int proceedId = s1.getTrain().getTrainID() < s2.getTrain().getTrainID() ?
                        s1.getTrain().getTrainID() : s2.getTrain().getTrainID();
                int waitId = proceedId == s1.getTrain().getTrainID() ?
                        s2.getTrain().getTrainID() : s1.getTrain().getTrainID();
                TrainSchedulePoint proceedPt = proceedId == s1.getTrain().getTrainID() ? point1 : point2;
                TrainSchedulePoint waitPt = proceedId == s1.getTrain().getTrainID() ? point2 : point1;
                LineSegment waitSeg = proceedId == s1.getTrain().getTrainID() ? seg2 : seg1;

                TrainSchedule proceedSchedule = proceedId == s1.getTrain().getTrainID() ? s1 : s2;
                LineSegment proceedSeg = proceedId == s1.getTrain().getTrainID() ? seg1 : seg2;
                TrainSchedule.SegmentOccupancy proceedOcc =
                        findSegmentOccupancy(proceedSchedule, proceedSeg, proceedPt.getFacility());

                if (proceedOcc == null) continue;

                conflicts.add(new ConflictInfo(s1, s2, waitSeg, proceedOcc.getExitTime(),
                        waitPt.getFacility(), true, false, proceedId, waitId, proceedPt, waitPt));
            }
        }
    }

    private void collectSegmentConflicts(List<TrainSchedule> schedules, List<ConflictInfo> conflicts,
                                         Set<Integer> trainsThatMustWait) {
        Set<String> handledWithSiding = new HashSet<>();

        // First pass: segments WITH sidings
        for (int i = 0; i < schedules.size(); i++) {
            for (int j = i + 1; j < schedules.size(); j++) {
                TrainSchedule s1 = schedules.get(i);
                TrainSchedule s2 = schedules.get(j);
                if (!areOnSameLineOppositeDirection(s1, s2)) continue;

                checkSegmentConflicts(s1, s2, conflicts, trainsThatMustWait, handledWithSiding, true);
            }
        }

        // Second pass: segments WITHOUT sidings (if not handled by siding)
        for (int i = 0; i < schedules.size(); i++) {
            for (int j = i + 1; j < schedules.size(); j++) {
                TrainSchedule s1 = schedules.get(i);
                TrainSchedule s2 = schedules.get(j);

                String pairKey = Math.min(s1.getTrain().getTrainID(), s2.getTrain().getTrainID()) + "-" +
                        Math.max(s1.getTrain().getTrainID(), s2.getTrain().getTrainID());
                if (handledWithSiding.contains(pairKey)) continue;
                if (!areOnSameLineOppositeDirection(s1, s2)) continue;

                checkSegmentConflicts(s1, s2, conflicts, trainsThatMustWait, handledWithSiding, false);
            }
        }
    }

    private void checkSegmentConflicts(TrainSchedule s1, TrainSchedule s2, List<ConflictInfo> conflicts,
                                       Set<Integer> trainsThatMustWait, Set<String> handledWithSiding,
                                       boolean sidingPass) {
        for (TrainSchedule.SegmentOccupancy occ1 : s1.getSegmentOccupancies()) {
            for (TrainSchedule.SegmentOccupancy occ2 : s2.getSegmentOccupancies()) {
                if (occ1.getSegment().getSegmentID() != occ2.getSegment().getSegmentID()) continue;
                if (occ1.getSegment().getTrackType() != TrackType.SINGLE) continue;
                if (!timesOverlap(occ1.getEntryTime(), occ1.getExitTime(),
                        occ2.getEntryTime(), occ2.getExitTime())) continue;

                boolean hasSiding = !repository.getSidingsBySegmentId(occ1.getSegment().getSegmentID()).isEmpty();

                if (sidingPass && !hasSiding) continue;
                if (!sidingPass && hasSiding) continue;

                int proceedId = s1.getTrain().getTrainID() < s2.getTrain().getTrainID() ?
                        s1.getTrain().getTrainID() : s2.getTrain().getTrainID();
                int waitId = proceedId == s1.getTrain().getTrainID() ?
                        s2.getTrain().getTrainID() : s1.getTrain().getTrainID();

                if (trainsThatMustWait.contains(proceedId)) continue;

                TrainSchedule.SegmentOccupancy proceedOcc = proceedId == s1.getTrain().getTrainID() ? occ1 : occ2;
                TrainSchedule.SegmentOccupancy waitOcc = proceedId == s1.getTrain().getTrainID() ? occ2 : occ1;

                if (hasSiding) {
                    String pairKey = Math.min(s1.getTrain().getTrainID(), s2.getTrain().getTrainID()) + "-" +
                            Math.max(s1.getTrain().getTrainID(), s2.getTrain().getTrainID());
                    handledWithSiding.add(pairKey);
                }

                conflicts.add(new ConflictInfo(s1, s2, occ1.getSegment(), occ1.getSegment(),
                        proceedOcc.getExitTime(), occ1.getFromFacility(), false, hasSiding,
                        proceedId, waitId, proceedOcc, waitOcc));
            }
        }
    }

    private void collectSameDirectionConflicts(List<TrainSchedule> schedules, List<ConflictInfo> conflicts,
                                               Set<String> resolvedCrossings) {
        for (int i = 0; i < schedules.size(); i++) {
            for (int j = i + 1; j < schedules.size(); j++) {
                TrainSchedule s1 = schedules.get(i);
                TrainSchedule s2 = schedules.get(j);

                if (!areGoingSameDirection(s1, s2)) continue;

                checkSameDirectionConflict(s1, s2, conflicts, resolvedCrossings);
            }
        }
    }

    private void checkSameDirectionConflict(TrainSchedule s1, TrainSchedule s2, List<ConflictInfo> conflicts,
                                            Set<String> resolvedCrossings) {
        for (TrainSchedule.SegmentOccupancy occ1 : s1.getSegmentOccupancies()) {
            for (TrainSchedule.SegmentOccupancy occ2 : s2.getSegmentOccupancies()) {
                if (occ1.getSegment().getSegmentID() != occ2.getSegment().getSegmentID()) continue;
                if (occ1.getSegment().getTrackType() != TrackType.SINGLE) continue;

                int aheadId, behindId;
                TrainSchedule.SegmentOccupancy aheadOcc, behindOcc;
                TrainSchedule aheadSchedule, behindSchedule;

                if (occ1.getEntryTime().isBefore(occ2.getEntryTime()) ||
                        (occ1.getEntryTime().equals(occ2.getEntryTime()) &&
                                s1.getTrain().getTrainID() < s2.getTrain().getTrainID())) {
                    aheadId = s1.getTrain().getTrainID();
                    behindId = s2.getTrain().getTrainID();
                    aheadOcc = occ1;
                    behindOcc = occ2;
                    aheadSchedule = s1;
                    behindSchedule = s2;
                } else {
                    aheadId = s2.getTrain().getTrainID();
                    behindId = s1.getTrain().getTrainID();
                    aheadOcc = occ2;
                    behindOcc = occ1;
                    aheadSchedule = s2;
                    behindSchedule = s1;
                }

                if (!behindOcc.getEntryTime().isBefore(aheadOcc.getExitTime()) &&
                        !behindOcc.getEntryTime().equals(aheadOcc.getExitTime())) continue;

                String crossingKey = aheadId + "-" + behindId;
                if (resolvedCrossings.contains(crossingKey)) continue;

                // Find waiting point
                Facility waitStation = behindOcc.getFromFacility();
                TrainSchedulePoint waitPt = null, aheadPt = null;
                LineSegment segToEnter = null;

                for (int k = 0; k < behindSchedule.getPoints().size() - 1; k++) {
                    TrainSchedulePoint pt = behindSchedule.getPoints().get(k);
                    if (pt.getFacility().equals(waitStation)) {
                        waitPt = pt;
                        segToEnter = findSegmentBetween(behindSchedule, pt.getFacility(),
                                behindSchedule.getPoints().get(k + 1).getFacility());
                        break;
                    }
                }

                for (TrainSchedulePoint pt : aheadSchedule.getPoints()) {
                    if (pt.getFacility().equals(waitStation)) {
                        aheadPt = pt;
                        break;
                    }
                }

                if (waitPt == null || segToEnter == null) continue;

                conflicts.add(new ConflictInfo(s1, s2, segToEnter, aheadOcc.getExitTime(),
                        waitStation, true, false, aheadId, behindId, aheadPt, waitPt));
                break;
            }
        }
    }

    private void resolveStationConflict(ConflictInfo conflict, List<CrossingOperation> operations,
                                        Set<Integer> trainsThatMustWait, Set<String> resolvedCrossings) {
        if (conflict.point1 == null || conflict.point2 == null) return;

        TrainSchedule proceedSchedule = conflict.proceedingTrainId == conflict.schedule1.getTrain().getTrainID() ?
                conflict.schedule1 : conflict.schedule2;
        TrainSchedule waitSchedule = conflict.waitingTrainId == conflict.schedule1.getTrain().getTrainID() ?
                conflict.schedule1 : conflict.schedule2;
        TrainSchedulePoint waitPt = conflict.waitingTrainId == conflict.schedule1.getTrain().getTrainID() ?
                conflict.point1 : conflict.point2;
        TrainSchedulePoint proceedPt = conflict.proceedingTrainId == conflict.schedule1.getTrain().getTrainID() ?
                conflict.point1 : conflict.point2;

        TrainSchedule.SegmentOccupancy proceedOcc = findSegmentOccupancy(proceedSchedule,
                conflict.segment, proceedPt.getFacility());
        if (proceedOcc == null) return;

        LocalDateTime proceedExit = proceedOcc.getExitTime();
        LocalDateTime waitDepart = waitPt.getDepartureTime() != null ?
                waitPt.getDepartureTime() : waitPt.getArrivalTime();
        long delay = Duration.between(waitDepart, proceedExit).toMinutes();

        if (delay <= 0) return;

        waitSchedule.applyDelayFromFacilityPreservingEquality(waitPt.getFacility().getFacilityID(), delay);
        trainsThatMustWait.add(conflict.waitingTrainId);
        resolvedCrossings.add(conflict.proceedingTrainId + "-" + conflict.waitingTrainId);

        operations.add(new CrossingOperation(proceedSchedule.getTrain(), waitSchedule.getTrain(),
                conflict.segment, proceedExit,
                String.format("Station %s (Segment %d)", waitPt.getFacility().getName(), conflict.segment.getSegmentID())));
    }

    private void resolveSegmentConflict(ConflictInfo conflict, List<CrossingOperation> operations,
                                        Set<Integer> trainsThatMustWait, Set<String> resolvedCrossings) {
        TrainSchedule waitSchedule = conflict.waitingTrainId == conflict.schedule1.getTrain().getTrainID() ?
                conflict.schedule1 : conflict.schedule2;
        TrainSchedule proceedSchedule = conflict.proceedingTrainId == conflict.schedule1.getTrain().getTrainID() ?
                conflict.schedule1 : conflict.schedule2;
        TrainSchedule.SegmentOccupancy waitOcc = conflict.waitingTrainId == conflict.schedule1.getTrain().getTrainID() ?
                conflict.occ1 : conflict.occ2;
        TrainSchedule.SegmentOccupancy proceedOcc = conflict.proceedingTrainId == conflict.schedule1.getTrain().getTrainID() ?
                conflict.occ1 : conflict.occ2;

        long delay = Duration.between(waitOcc.getEntryTime(), proceedOcc.getExitTime()).toMinutes();
        if (delay <= 0) return;

        String locationDesc;
        if (conflict.hasSiding) {
            // Apply delay to next station and subsequent points
            Facility toFacility = waitOcc.getToFacility();
            for (TrainSchedulePoint pt : waitSchedule.getPoints()) {
                if (pt.getFacility().getFacilityID() == toFacility.getFacilityID()) {
                    if (pt.getArrivalTime() != null) pt.setArrivalTime(pt.getArrivalTime().plusMinutes(delay));
                    if (pt.getDepartureTime() != null) pt.setDepartureTime(pt.getDepartureTime().plusMinutes(delay));
                    break;
                }
            }

            boolean found = false;
            for (TrainSchedulePoint pt : waitSchedule.getPoints()) {
                if (pt.getFacility().equals(toFacility)) { found = true; continue; }
                if (found) {
                    if (pt.getArrivalTime() != null) pt.setArrivalTime(pt.getArrivalTime().plusMinutes(delay));
                    if (pt.getDepartureTime() != null) pt.setDepartureTime(pt.getDepartureTime().plusMinutes(delay));
                }
            }

            found = false;
            for (TrainSchedule.SegmentOccupancy occ : waitSchedule.getSegmentOccupancies()) {
                if (occ.getSegment().getSegmentID() == conflict.segment.getSegmentID()) { found = true; continue; }
                if (found) occ.shiftTimes(delay);
            }

            waitSchedule.setTotalDurationMinutes(waitSchedule.getTotalDurationMinutes() + delay);

            List<Siding> sidings = repository.getSidingsBySegmentId(conflict.segment.getSegmentID());
            locationDesc = String.format("Siding %d (Segment %d)",
                    sidings.get(0).getSidingID(), conflict.originalSegment.getSegmentID());
        } else {
            Facility prevStation = findPreviousStationBeforeSegment(waitSchedule, conflict.segment);
            if (prevStation == null) prevStation = waitOcc.getFromFacility();

            waitSchedule.applyDelayFromFacilityPreservingEquality(prevStation.getFacilityID(), delay);
            locationDesc = String.format("Station %s (Segment %d)",
                    prevStation.getName(), conflict.segment.getSegmentID());
        }

        trainsThatMustWait.add(conflict.waitingTrainId);
        resolvedCrossings.add(conflict.proceedingTrainId + "-" + conflict.waitingTrainId);

        operations.add(new CrossingOperation(proceedSchedule.getTrain(), waitSchedule.getTrain(),
                conflict.segment, proceedOcc.getExitTime(), locationDesc));
    }

    private boolean areGoingSameDirection(TrainSchedule s1, TrainSchedule s2) {
        for (int i = 0; i < s1.getPoints().size() - 1; i++) {
            Facility f1a = s1.getPoints().get(i).getFacility();
            Facility f1b = s1.getPoints().get(i + 1).getFacility();
            for (int j = 0; j < s2.getPoints().size() - 1; j++) {
                if (f1a.equals(s2.getPoints().get(j).getFacility()) &&
                        f1b.equals(s2.getPoints().get(j + 1).getFacility())) return true;
            }
        }
        return false;
    }

    private boolean areOnSameLineOppositeDirection(TrainSchedule s1, TrainSchedule s2) {
        Set<Integer> lines = new HashSet<>();
        s1.getSegmentOccupancies().forEach(o -> lines.add(o.getSegment().getLineID()));
        return s2.getSegmentOccupancies().stream().anyMatch(o -> lines.contains(o.getSegment().getLineID()));
    }

    private boolean timesOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    private LineSegment findSegmentBetween(TrainSchedule schedule, Facility from, Facility to) {
        return schedule.getSegmentOccupancies().stream()
                .filter(o -> o.getFromFacility().equals(from) && o.getToFacility().equals(to))
                .map(TrainSchedule.SegmentOccupancy::getSegment)
                .findFirst().orElse(null);
    }

    private TrainSchedule.SegmentOccupancy findSegmentOccupancy(TrainSchedule schedule, LineSegment segment, Facility from) {
        return schedule.getSegmentOccupancies().stream()
                .filter(o -> o.getSegment().getSegmentID() == segment.getSegmentID() && o.getFromFacility().equals(from))
                .findFirst().orElse(null);
    }

    private Facility findPreviousStationBeforeSegment(TrainSchedule schedule, LineSegment conflictSegment) {
        TrainSchedule.SegmentOccupancy conflictOcc = schedule.getSegmentOccupancies().stream()
                .filter(o -> o.getSegment().getSegmentID() == conflictSegment.getSegmentID())
                .findFirst().orElse(null);

        if (conflictOcc == null) return null;

        Facility from = conflictOcc.getFromFacility();
        if (from.getFacilityType() == FacilityType.STATION) return from;

        Facility prev = null;
        for (TrainSchedulePoint pt : schedule.getPoints()) {
            if (pt.getFacility().equals(from)) break;
            if (pt.getFacility().getFacilityType() == FacilityType.STATION) prev = pt.getFacility();
        }
        return prev;
    }

    public static void displayCrossing(CrossingOperation op, int index) {
        System.out.printf("Crossing Operation %d:%n  Trains: %d (proceeds) ↔ %d (waits)%n  Location: %s%n  Scheduled Time: %s%n  Segment: %d (Line %d)%n",
                index, op.getFirstTrain().getTrainID(), op.getSecondTrain().getTrainID(), op.getLocationDescription(),
                op.getScheduledTime().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")),
                op.getSegment().getSegmentID(), op.getSegment().getLineID());
    }

    public static void displayCrossingSummary(List<CrossingOperation> operations) {
        if (operations.isEmpty()) return;
        System.out.println("\n--- Crossing Summary ---");
        System.out.printf("Total crossing operations: %d%n", operations.size());
        Map<Integer, Integer> counts = new HashMap<>();
        operations.forEach(op -> counts.merge(op.getSecondTrain().getTrainID(), 1, Integer::sum));
        if (!counts.isEmpty()) {
            System.out.println("\nTrains that need to wait:");
            counts.entrySet().stream().sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                    .forEach(e -> System.out.printf("  Train %d: %d crossing(s)%n", e.getKey(), e.getValue()));
        }
    }
}