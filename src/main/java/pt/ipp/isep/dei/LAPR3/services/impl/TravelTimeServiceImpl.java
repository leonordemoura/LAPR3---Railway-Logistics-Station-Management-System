package pt.ipp.isep.dei.LAPR3.services.impl;

import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.LAPR3.domain.*;
import pt.ipp.isep.dei.LAPR3.services.TravelTimeService;

import java.util.List;

/**
 * USLP03 - Travel Time Calculation
 * <p>
 * Service implementation that calculates the travel time between two station, with a direct connection.
 * </p>
 */
public class TravelTimeServiceImpl implements TravelTimeService {
    private RailwayRepository repository;

    public TravelTimeServiceImpl() {
        repository = Repositories.getInstance().getRailwayRepository();
    }

    /**
     * Calculate travel time between two stations with direct connection
     * @param startFacilityId Start station ID
     * @param endFacilityId End station ID
     * @param locomotiveId Locomotive ID
     * @return TravelTimeResult with detailed information
     */
    @Override
    public TravelTimeResult calculateTravelTime(int startFacilityId, int endFacilityId, int locomotiveId) {
        Locomotive locomotive = repository.getLocomotiveById(locomotiveId);
        if (locomotive == null) {
            throw new IllegalArgumentException("Locomotive with ID " + locomotiveId + " not found");
        }

        Facility startFacility = repository.getFacilityById(startFacilityId);
        if (startFacility == null) {
            throw new IllegalArgumentException("Start facility with ID " + startFacilityId + " not found");
        }

        Facility endFacility = repository.getFacilityById(endFacilityId);
        if (endFacility == null) {
            throw new IllegalArgumentException("End facility with ID " + endFacilityId + " not found");
        }

        Lines line = findDirectLine(startFacilityId, endFacilityId);
        if (line == null) {
            throw new IllegalArgumentException("No direct line found between " +
                    startFacility.getName() + " and " + endFacility.getName());
        }

        List<LineSegment> segments = repository.getSegmentsByLineId(line.getLineID());

        if (segments.isEmpty()) {
            throw new IllegalArgumentException("No segments found for line " + line.getLineID());
        }

        TravelTimeResult result = new TravelTimeResult(startFacility, endFacility, locomotive, segments, line, 0, 0);
        result.setStartFacility(startFacility);
        result.setEndFacility(endFacility);
        result.setLocomotive(locomotive);
        result.setLine(line);
        result.setSegments(segments);

        double totalTime = 0;
        double totalDistance = 0;

        double locomotiveOperationalSpeed = locomotive.getOperationalSpeed();

        for (LineSegment segment : segments) {
            double segmentLengthKm = segment.getLength() / 1000.0;
            int segmentSpeedLimit = segment.getSpeedLimit();

            // Effective speed is the minimum between locomotive operational speed and segment speed limit
            double effectiveSpeed = Math.min(locomotiveOperationalSpeed, segmentSpeedLimit);

            // Time = Distance / Speed (result in hours)
            double timeInHours = segmentLengthKm / effectiveSpeed;

            // Convert to minutes
            double timeInMinutes = timeInHours * 60;

            totalTime += timeInMinutes;
            totalDistance += segmentLengthKm;

            // Store segment details
            TravelTimeResult.SegmentTravelInfo segmentInfo = new TravelTimeResult.SegmentTravelInfo();
            segmentInfo.segment = segment;
            segmentInfo.effectiveSpeed = effectiveSpeed;
            segmentInfo.timeInMinutes = timeInMinutes;
            result.getSegmentsInfos().add(segmentInfo);
        }

        result.setTotalTimeMinutes(totalTime);
        result.setTotalDistanceKm(totalDistance);

        return result;
    }

    /**
     * Find a direct line between two facilities
     */
    public Lines findDirectLine(int startId, int endId) {
        for (Lines line : repository.getAllLines()) {
            if ((line.getStartFacilityID() == startId && line.getEndFacilityID() == endId) ||
                    (line.getStartFacilityID() == endId && line.getEndFacilityID() == startId)) {
                return line;
            }
        }
        return null;
    }
}
