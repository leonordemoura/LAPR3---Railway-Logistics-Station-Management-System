CREATE OR REPLACE FUNCTION getGrainWagonsUsedInAllTrains(p_startDate  DATE,p_endDate    DATE)
    RETURN SYS_REFCURSOR AS
    c SYS_REFCURSOR;
BEGIN
    OPEN c FOR
        SELECT DISTINCT fw.wagonID
        FROM Freight_Wagon fw
        WHERE fw.freightID IN (
            SELECT f.freightID
            FROM Freight f
            WHERE f.trainID IN (
                SELECT t.trainID
                FROM Train t
                WHERE t.startDate BETWEEN p_startDate AND p_endDate
            )
        )
          AND fw.wagonID IN (
            SELECT w.wagonID
            FROM Wagon w
            WHERE w.wagonModelID IN (
                SELECT wm.wagonModelID
                FROM WagonModel wm
                WHERE wm.wagonTypeID IN (
                    SELECT wt.wagonTypeID
                    FROM WagonType wt
                    WHERE wt.name LIKE '%grain%'
                )
            )
        );

    RETURN c;
END;
