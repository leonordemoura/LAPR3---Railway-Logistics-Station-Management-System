CREATE OR REPLACE FUNCTION countEndPoints(searchTrainID train.trainID%TYPE)
    RETURN  SYS_REFCURSOR AS
    endpointsList SYS_REFCURSOR;

BEGIN
    OPEN endpointsList FOR
        SELECT f.name AS FacilityName,
               f.facilityID
        FROM Path_Facility pf
                 JOIN Facility f ON f.facilityID = pf.facilityID
        WHERE pf.stopOrder = 'N'
          AND pf.pathID IN (
            SELECT pr.pathID
            FROM Train t
                     JOIN Path_Route pr ON t.routeID = pr.routeID
            WHERE t.trainID = searchTrainID
        )
        ORDER BY pf."order";
    RETURN endpointsList;
END;


CREATE OR REPLACE FUNCTION train_exists(searchTrainID train.trainID%TYPE) return boolean is
    v_count int;
BEGIN
    SELECT COUNT(*) INTO v_count
        FROM TRAIN
            WHERE TRAINID = searchTrainID;
    return v_count > 0;
end;




