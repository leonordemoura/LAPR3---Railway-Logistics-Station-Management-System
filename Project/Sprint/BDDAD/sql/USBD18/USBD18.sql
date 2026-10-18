SELECT
    l.lineID,
    ls.numberOfTracks AS track_type,
    COUNT(ls.segmentID) AS number_of_segments
FROM Line l
         INNER JOIN LineSegment ls ON l.lineID = ls.lineID
WHERE l.lineID = 1
GROUP BY l.lineID, ls.numberOfTracks
ORDER BY l.lineID, ls.numberOfTracks;