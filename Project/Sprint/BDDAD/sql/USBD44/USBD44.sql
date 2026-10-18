CREATE OR REPLACE FUNCTION addLineSegment(
    p_segmentID         LineSegment.segmentID%TYPE,
    p_lineID            LineSegment.lineID%TYPE,
    p_orderLine         LineSegment.orderLine%TYPE,
    p_maxWeight         LineSegment.maxWeight%TYPE,
    p_length            LineSegment.length%TYPE,
    p_speedLimit        LineSegment.speedLimit%TYPE,
    p_isSimple          CHAR,                                     -- 'Y' or 'N'
    p_maintenanceStatus LineSegment.maintenanceStatus%TYPE,       -- 'Y' or 'N'
    p_sidingID          Siding.sidingID%TYPE DEFAULT NULL,
    p_sidingPos         Siding.position%TYPE DEFAULT NULL,
    p_sidingLen         Siding.length%TYPE DEFAULT NULL
)
RETURN VARCHAR2
IS
    v_count NUMBER;
BEGIN
    ------------------------------------------------------------
    -- Validar campos obrigatórios não nulos
    ------------------------------------------------------------
    IF p_segmentID IS NULL OR p_lineID IS NULL OR p_orderLine IS NULL OR
       p_maxWeight IS NULL OR p_length IS NULL OR p_speedLimit IS NULL OR
       p_isSimple IS NULL OR p_maintenanceStatus IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'Mandatory fields cannot be NULL');
    END IF;

    ------------------------------------------------------------
    -- Validar valores positivos
    ------------------------------------------------------------
    IF p_maxWeight <= 0 THEN
        RAISE_APPLICATION_ERROR(-20011, 'maxWeight must be greater than 0');
    END IF;

    IF p_length <= 0 THEN
        RAISE_APPLICATION_ERROR(-20012, 'length must be greater than 0');
    END IF;

    IF p_speedLimit <= 0 THEN
        RAISE_APPLICATION_ERROR(-20013, 'speedLimit must be greater than 0');
    END IF;

    IF p_orderLine <= 0 THEN
        RAISE_APPLICATION_ERROR(-20014, 'orderLine must be greater than 0');
    END IF;

    ------------------------------------------------------------
    -- Validar p_isSimple
    ------------------------------------------------------------
    IF p_isSimple NOT IN ('Y','N') THEN
        RAISE_APPLICATION_ERROR(-20000, 'p_isSimple must be Y or N');
    END IF;

    ------------------------------------------------------------
    -- Validar maintenanceStatus
    ------------------------------------------------------------
    IF p_maintenanceStatus NOT IN ('Y','N') THEN
        RAISE_APPLICATION_ERROR(-20005, 'maintenanceStatus must be Y or N');
    END IF;

    ------------------------------------------------------------
    -- Verificar se a linha existe
    ------------------------------------------------------------
    SELECT COUNT(*) INTO v_count
    FROM Line
    WHERE lineID = p_lineID;

    IF v_count = 0 THEN
            RAISE_APPLICATION_ERROR(-20001, 'Line does not exist.');
    END IF;

    ------------------------------------------------------------
    -- Verificar duplicação de segmentID
    ------------------------------------------------------------
    SELECT COUNT(*) INTO v_count
    FROM LineSegment
    WHERE segmentID = p_segmentID;

    IF v_count > 0 THEN
            RAISE_APPLICATION_ERROR(-20006, 'SegmentID already exists');
    END IF;

    ------------------------------------------------------------
    -- Inserir LineSegment
    ------------------------------------------------------------
    INSERT INTO LineSegment (
        segmentID, lineID, orderLine,
        maxWeight, length, maintenanceStatus, speedLimit
    )
    VALUES (
           p_segmentID, p_lineID, p_orderLine,
           p_maxWeight, p_length, p_maintenanceStatus, p_speedLimit
    );

    ------------------------------------------------------------
    -- SIMPLE TRACK
    ------------------------------------------------------------
    IF p_isSimple = 'Y' THEN
            INSERT INTO SimpleTrack (segmentID)
            VALUES (p_segmentID);

            -- Se houver siding
            IF p_sidingPos IS NOT NULL OR p_sidingLen IS NOT NULL THEN
                IF p_sidingPos IS NULL OR p_sidingLen IS NULL THEN
                    RAISE_APPLICATION_ERROR(
                        -20002,
                        'Both siding position and length must be provided'
                    );
                END IF;

            IF p_sidingPos < 0 OR p_sidingLen <= 0 THEN
                RAISE_APPLICATION_ERROR(
                    -20003,
                    'Siding position and length must be positive'
                );
            END IF;

        -- Inserir siding com sidingID
        INSERT INTO Siding (sidingID, segmentID, position, length)
        VALUES (p_sidingID, p_segmentID, p_sidingPos, p_sidingLen);
    END IF;

    ------------------------------------------------------------
    -- DOUBLE TRACK
    ------------------------------------------------------------
    ELSE
        INSERT INTO DoubleTrack (segmentID)
        VALUES (p_segmentID);

        -- double track não pode ter siding
        IF p_sidingPos IS NOT NULL OR p_sidingLen IS NOT NULL THEN
            RAISE_APPLICATION_ERROR(
                -20004,
                'Double track cannot have a siding'
            );
        END IF;
    END IF;

    RETURN 'Segment added successfully';
END;
