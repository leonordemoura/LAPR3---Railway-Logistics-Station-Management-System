CREATE OR REPLACE FUNCTION registerFacility(
    p_facilityID     IN Facility.facilityID%TYPE,
    p_name           IN Facility.name%TYPE,
    p_facilityTypeID IN FacilityType.facilityTypeID%TYPE,
    p_isIntermodal   IN FacilityType.isIntermodal%TYPE
)
    RETURN NUMBER
    IS
    v_name_normalized Facility.name%TYPE;
    v_typeName        FacilityType.name%TYPE;
    v_count           NUMBER;
BEGIN
    ------------------------------------------------------------------
    -- Validações básicas
    ------------------------------------------------------------------
    IF p_facilityID IS NULL THEN
        RAISE_APPLICATION_ERROR(-20018, 'Facility ID is required');
    END IF;

    IF p_name IS NULL THEN
        RAISE_APPLICATION_ERROR(-20017, 'Facility name is required');
    END IF;

    IF p_facilityTypeID IS NULL THEN
        RAISE_APPLICATION_ERROR(-20019, 'Facility type is required');
    END IF;

    IF p_isIntermodal IS NULL THEN
        RAISE_APPLICATION_ERROR(-20020, 'Is intermodal is required');
    END IF;

    IF p_isIntermodal NOT IN ('Y','N') THEN
        RAISE_APPLICATION_ERROR(-20021, 'isIntermodal must be Y or N');
    END IF;

    ------------------------------------------------------------------
    -- Obter tipo da facility
    ------------------------------------------------------------------
    SELECT name
    INTO v_typeName
    FROM FacilityType
    WHERE facilityTypeID = p_facilityTypeID;

    ------------------------------------------------------------------
    -- Validar coerência tipo - intermodal
    ------------------------------------------------------------------
    IF v_typeName IN ('STATION', 'FREIGHT_YARD') AND p_isIntermodal != 'N' THEN
            RAISE_APPLICATION_ERROR(
                    -20023,
                    'Only TERMINAL facilities can be intermodal'
            );
    END IF;

    ------------------------------------------------------------------
    -- Normalizar nome
    ------------------------------------------------------------------
    v_name_normalized := REGEXP_REPLACE(UPPER(TRIM(p_name)), '\s+', ' ');

    ------------------------------------------------------------------
    -- Verificar duplicados
    ------------------------------------------------------------------
    SELECT COUNT(*) INTO v_count
    FROM Facility
    WHERE facilityID = p_facilityID;

    IF v_count > 0 THEN
            RAISE_APPLICATION_ERROR(-20024, 'Facility ID already exists');
    END IF;

    SELECT COUNT(*) INTO v_count
    FROM Facility
    WHERE REGEXP_REPLACE(UPPER(TRIM(name)), '\s+', ' ') = v_name_normalized;

    IF v_count > 0 THEN
            RAISE_APPLICATION_ERROR(-20025, 'Facility name already exists');
    END IF;

    ------------------------------------------------------------------
    -- Inserção final
    ------------------------------------------------------------------
    INSERT INTO Facility (facilityID, name, facilityTypeID)
    VALUES (p_facilityID, v_name_normalized, p_facilityTypeID);

    RETURN p_facilityID;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20026, 'Invalid facility type ID');

    WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
END registerFacility;
/
