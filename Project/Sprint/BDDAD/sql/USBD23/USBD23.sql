CREATE OR REPLACE FUNCTION getPlannedRouteLineSegments(p_trainID NUMBER)
    RETURN SYS_REFCURSOR AS
    c SYS_REFCURSOR;
BEGIN
OPEN c FOR
SELECT ls.segmentID,
       ls.lineID,
       ls.orderLine
FROM LineSegment ls
WHERE ls.lineID IN (
    SELECT l.lineID
    FROM Line l
    WHERE l.startFacilityID IN (
        SELECT pf.facilityID
        FROM Path_Facility pf
        WHERE pf.pathID IN (
            SELECT pr.pathID
            FROM Path_Route pr
            WHERE pr.routeID IN (
                SELECT t.routeID
                FROM Train t
                WHERE t.trainID = p_trainID
                )
            )
        )
    );

    RETURN c;
END;