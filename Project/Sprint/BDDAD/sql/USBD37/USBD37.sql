CREATE OR REPLACE FUNCTION register_wagon(
    p_wagonID        IN Wagon.wagonID%TYPE,
    p_operatorID     IN Wagon.operatorID%TYPE,
    p_wagonModelID   IN Wagon.wagonModelID%TYPE,
    p_startFacility  IN Wagon.startFacility%TYPE,
    p_serviceYear    IN Wagon.serviceYear%TYPE,
    p_payloadTons    IN Wagon.payloadTons%TYPE,
    p_status         IN Wagon.status%TYPE
)
    RETURN VARCHAR2
IS
    v_count_gauges NUMBER;
    v_wagon_exists NUMBER;
    v_operator_exists NUMBER;
    v_model_exists NUMBER;
    v_facility_exists NUMBER;
BEGIN
    -- 1. Validar se wagon já existe
    SELECT COUNT(*) INTO v_wagon_exists
    FROM Wagon WHERE wagonID = p_wagonID;

    IF v_wagon_exists > 0 THEN
        RAISE_APPLICATION_ERROR(-20001,
            'ERROR: Wagon with ID ' || p_wagonID || ' already exists.');
    END IF;

    -- 2. Validar se operator existe
    SELECT COUNT(*) INTO v_operator_exists
    FROM Operator WHERE operatorID = p_operatorID;

    IF v_operator_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20002,
            'ERROR: Invalid operatorID ' || p_operatorID || '.');
    END IF;

    -- 3. Validar se wagon model existe
    SELECT COUNT(*) INTO v_model_exists
    FROM WagonModel WHERE wagonModelID = p_wagonModelID;

    IF v_model_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20003,
            'ERROR: Invalid wagonModelID ' || p_wagonModelID || '.');
    END IF;

    -- 4. Validar se start facility existe
    SELECT COUNT(*) INTO v_facility_exists
    FROM Facility WHERE facilityID = p_startFacility;

    IF v_facility_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20004,
            'ERROR: Invalid startFacility ' || p_startFacility || '.');
    END IF;

    -- 5. Verificar se o modelo tem gauges associados (requisito da US)
    SELECT COUNT(*)
    INTO v_count_gauges
    FROM WagonModel wm
         JOIN Gauge_Vehicle gv ON gv.vehicleID = wm.vehicleID
    WHERE wm.wagonModelID = p_wagonModelID;

    IF v_count_gauges = 0 THEN
        RAISE_APPLICATION_ERROR(-20005,
            'ERROR: The wagon model has no supported gauges registered.');
    END IF;

    -- 6. Inserir o wagon (as gauges são herdadas do Vehicle através do modelo)
    INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
    VALUES (p_wagonID, p_operatorID, p_wagonModelID, p_startFacility, p_serviceYear, p_payloadTons, p_status);

    COMMIT;

    RETURN 'Wagon ' || p_wagonID || ' registered successfully with ' ||
       v_count_gauges || ' supported gauge(s).';

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
END register_wagon;
/