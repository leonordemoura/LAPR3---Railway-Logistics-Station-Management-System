SELECT
    f_start.name AS from_station,
    f_end.name AS to_station,
    ls.segmentID,
    ls.orderLine AS segment_order,
    ls.length AS segment_length_m,
    LEAST(ls.speedLimit, loc.operationalSpeed) AS applied_speed_kmh,
    ROUND((ls.length / 1000) / LEAST(ls.speedLimit, loc.operationalSpeed) * 60, 2) AS estimated_segment_time_min
FROM Line l
         INNER JOIN Facility f_start ON l.startFacilityID = f_start.facilityID
         INNER JOIN Facility f_end ON l.endFacilityID = f_end.facilityID
         INNER JOIN LineSegment ls ON l.lineID = ls.lineID
         INNER JOIN Locomotive loc ON loc.locomotiveID = 5623
WHERE l.startFacilityID = 17
  AND l.endFacilityID = 21
ORDER BY ls.orderLine;

SELECT
    ROUND(SUM((ls.length / 1000) / LEAST(ls.speedLimit, loc.operationalSpeed) * 60), 2) AS total_trip_time_min
FROM Line l
         INNER JOIN LineSegment ls ON l.lineID = ls.lineID
         INNER JOIN Locomotive loc ON loc.locomotiveID = 5623
WHERE l.startFacilityID = 17
  AND l.endFacilityID = 21;
