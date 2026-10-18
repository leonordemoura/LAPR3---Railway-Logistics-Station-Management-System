-- ============================================================================
-- USBD45: Add New Line with at least one segment
-- ============================================================================
CREATE OR REPLACE FUNCTION addLineWithSegment(
    p_lineID          IN Line.lineID%TYPE,
    p_startFacilityID IN Line.startFacilityID%TYPE,
    p_endFacilityID   IN Line.endFacilityID%TYPE,
    p_lineOwnerID     IN Line.lineOwnerID%TYPE,
    p_gaugeID         IN Line.gaugeID%TYPE,
    p_isElectrified   IN Line.isElectrified%TYPE,
    p_segmentID       IN LineSegment.segmentID%TYPE,
    p_orderLine       IN LineSegment.orderLine%TYPE,
    p_maxWeight       IN LineSegment.maxWeight%TYPE,
    p_length          IN LineSegment.length%TYPE,
    p_speedLimit      IN LineSegment.speedLimit%TYPE,
    p_trackType       IN VARCHAR2  -- 'SIMPLE' ou 'DOUBLE'
)
    RETURN VARCHAR2
    IS
    v_count NUMBER;
BEGIN
    ----------------------------------------------------------------
    -- Validações de entrada
    ----------------------------------------------------------------

    -- 1. IDs não podem ser NULL
    IF p_lineID IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001, 'Line ID cannot be null');
    END IF;

    IF p_segmentID IS NULL THEN
        RAISE_APPLICATION_ERROR(-20002, 'Segment ID cannot be null');
    END IF;

    -- 2. Facilities diferentes
    IF p_startFacilityID = p_endFacilityID THEN
        RAISE_APPLICATION_ERROR(-20003, 'Start and end facilities must be different');
    END IF;

    -- 3. Track type válido
    IF p_trackType NOT IN ('SIMPLE', 'DOUBLE') THEN
        RAISE_APPLICATION_ERROR(-20004, 'Invalid track type. Must be SIMPLE or DOUBLE');
    END IF;

    -- 4. isElectrified válido
    IF p_isElectrified NOT IN ('Y', 'N') THEN
        RAISE_APPLICATION_ERROR(-20005, 'isElectrified must be Y or N');
    END IF;

    -- 5. Valores positivos
    IF p_maxWeight IS NULL OR p_maxWeight <= 0 THEN
        RAISE_APPLICATION_ERROR(-20006, 'maxWeight must be greater than 0');
    END IF;

    IF p_length IS NULL OR p_length <= 0 THEN
        RAISE_APPLICATION_ERROR(-20007, 'length must be greater than 0');
    END IF;

    IF p_speedLimit IS NULL OR p_speedLimit <= 0 THEN
        RAISE_APPLICATION_ERROR(-20008, 'speedLimit must be greater than 0');
    END IF;

    IF p_orderLine IS NULL OR p_orderLine <= 0 THEN
        RAISE_APPLICATION_ERROR(-20009, 'orderLine must be greater than 0');
    END IF;

    ----------------------------------------------------------------
    -- Verificar se entidades referenciadas existem
    ----------------------------------------------------------------

    -- Verificar startFacility
    SELECT COUNT(*) INTO v_count
    FROM Facility
    WHERE facilityID = p_startFacilityID;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20010,'Start facility ID ' || p_startFacilityID || ' does not exist');
    END IF;

    -- Verificar endFacility
    SELECT COUNT(*) INTO v_count
    FROM Facility
    WHERE facilityID = p_endFacilityID;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20011, 'End facility ID ' || p_endFacilityID || ' does not exist');
    END IF;

    -- Verificar lineOwner
    SELECT COUNT(*) INTO v_count
    FROM LineOwner
    WHERE lineOwnerID = p_lineOwnerID;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20012, 'Line owner ID ' || p_lineOwnerID || ' does not exist');
    END IF;

    -- Verificar gauge
    SELECT COUNT(*) INTO v_count
    FROM Gauge
    WHERE dimension = p_gaugeID;

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20013, 'Gauge dimension ' || p_gaugeID || ' does not exist');
    END IF;

    ----------------------------------------------------------------
    -- Verificar se a line já existe
    ----------------------------------------------------------------
    SELECT COUNT(*) INTO v_count
    FROM Line
    WHERE lineID = p_lineID;

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20014, 'Line ID ' || p_lineID || ' already exists');
    END IF;

    -- Verificar se o segment já existe
    SELECT COUNT(*) INTO v_count
    FROM LineSegment
    WHERE segmentID = p_segmentID;

    IF v_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20015, 'Segment ID ' || p_segmentID || ' already exists');
    END IF;

    ----------------------------------------------------------------
    -- Inserir LINE
    ----------------------------------------------------------------
    INSERT INTO Line (
        lineID, startFacilityID, endFacilityID,
        lineOwnerID, gaugeID, isElectrified
    )
    VALUES (
               p_lineID, p_startFacilityID, p_endFacilityID,
               p_lineOwnerID, p_gaugeID, p_isElectrified
           );

    ----------------------------------------------------------------
    -- Inserir PRIMEIRO SEGMENTO (obrigatório)
    ----------------------------------------------------------------
    INSERT INTO LineSegment (
        segmentID, lineID, orderLine, maxWeight,
        length, maintenanceStatus, speedLimit
    )
    VALUES (
               p_segmentID, p_lineID, p_orderLine, p_maxWeight,
               p_length, 'N', p_speedLimit
           );

    ----------------------------------------------------------------
    -- Tipo de linha (SIMPLE ou DOUBLE)
    ----------------------------------------------------------------
    IF p_trackType = 'SIMPLE' THEN
        INSERT INTO SimpleTrack (segmentID)
        VALUES (p_segmentID);
    ELSIF p_trackType = 'DOUBLE' THEN
        INSERT INTO DoubleTrack (segmentID)
        VALUES (p_segmentID);
    END IF;

    COMMIT;

    RETURN 'SUCCESS: Line ' || p_lineID || ' created with segment ' || p_segmentID;

EXCEPTION
    WHEN OTHERS THEN
        RAISE;  -- Re-lança a exceção para o chamador saber o que aconteceu
END addLineWithSegment;
/