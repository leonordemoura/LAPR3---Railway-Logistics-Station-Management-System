CREATE OR REPLACE FUNCTION registerTrainMovement(
    p_pathID        Path.pathID%TYPE,
    p_facilityID    Facility.facilityID%TYPE,
    p_arrivalTime   Path_Facility.arrivalTime%TYPE,
    p_departureTime Path_Facility.departureTime%TYPE DEFAULT NULL
)
RETURN VARCHAR2
IS
    v_count          NUMBER;
    v_finalDeparture Path_Facility.departureTime%TYPE;
    v_nextOrder      Path_Facility."order"%TYPE;
BEGIN
    ----------------------------------------------------------------
    -- USBD42:
    -- If the train stops at the facility, departure time is registered.
    -- Otherwise, arrival time is reused as departure time.
    ----------------------------------------------------------------
    IF p_departureTime IS NULL THEN
        v_finalDeparture := p_arrivalTime;
ELSE
        v_finalDeparture := p_departureTime;
END IF;

    ----------------------------------------------------------------
    -- Validate timestamps
    ----------------------------------------------------------------
    IF v_finalDeparture < p_arrivalTime THEN
        RAISE_APPLICATION_ERROR(
            -20032,
            'Departure time cannot be earlier than arrival time.'
        );
END IF;

    ----------------------------------------------------------------
    -- Verify if Path exists
    ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM Path
WHERE pathID = p_pathID;

IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20030, 'Path does not exist.');
END IF;

    ----------------------------------------------------------------
    -- Verify if Facility exists
    ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM Facility
WHERE facilityID = p_facilityID;

IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20031, 'Facility does not exist.');
END IF;

    ----------------------------------------------------------------
    -- Check if movement already exists for this Path and Facility
    ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM Path_Facility
WHERE pathID = p_pathID
  AND facilityID = p_facilityID;

IF v_count > 0 THEN
        ------------------------------------------------------------
        -- Update existing movement
        ------------------------------------------------------------
UPDATE Path_Facility
SET arrivalTime   = p_arrivalTime,
    departureTime = v_finalDeparture
WHERE pathID = p_pathID
  AND facilityID = p_facilityID;

RETURN 'Movement successfully updated';
ELSE
        ------------------------------------------------------------
        -- Calculate next order in the path
        ------------------------------------------------------------
SELECT NVL(MAX("order"), 0) + 1
INTO v_nextOrder
FROM Path_Facility
WHERE pathID = p_pathID;

------------------------------------------------------------
-- Register new movement
------------------------------------------------------------
INSERT INTO Path_Facility (
    pathID,
    facilityID,
    "order",
    arrivalTime,
    departureTime
)
VALUES (
           p_pathID,
           p_facilityID,
           v_nextOrder,
           p_arrivalTime,
           v_finalDeparture
       );

RETURN 'Movement successfully registered';
END IF;

END;
/
