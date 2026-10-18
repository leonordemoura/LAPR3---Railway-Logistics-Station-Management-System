DECLARE
    v_result VARCHAR2(200);
    v_count NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('==============================================');
    DBMS_OUTPUT.PUT_LINE(' USBD37 - TESTES: Register a new Wagon');
    DBMS_OUTPUT.PUT_LINE('==============================================');
    ------------------------------------------------------------------
-- TESTE POSITIVO
------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Positive Test: Model with gauges ---');

    BEGIN
        v_result := register_wagon(
                p_wagonID      => 900001,
                p_operatorID   => 1,
                p_wagonModelID => 1278,
                p_startFacility => 1,   -- facility válida
                p_serviceYear  => 2025,
                p_payloadTons  => 26,
                p_status       => 'AVAILABLE'
                    );

        DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

        -- Verificar se foi realmente inserido
        SELECT COUNT(*) INTO v_count FROM Wagon WHERE wagonID = 900001;
        DBMS_OUTPUT.PUT_LINE('Wagon inserted in the BD: ' ||
                             CASE WHEN v_count = 1 THEN 'YES' ELSE 'NO' END);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('UNEXPECTED ERROR: ' || SQLERRM);
    END;

    ------------------------------------------------------------------
    -- TESTE NEGATIVO 1: Modelo sem gauges
    ------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Negative Test 1: Model without gauges  ---');

    BEGIN
        v_result := register_wagon(
                p_wagonID      => 900002,
                p_operatorID   => 1,
                p_wagonModelID => 90091,
                p_startFacility => 1,   -- facility válida
                p_serviceYear  => 2025,
                p_payloadTons  => 20,
                p_status       => 'AVAILABLE'
                    );

        DBMS_OUTPUT.PUT_LINE('ERROR: Should have failed (model without gauges)!');
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20005 THEN
                DBMS_OUTPUT.PUT_LINE('Expected error (without gauges): ' || SQLERRM);
            ELSIF SQLCODE = -20003 THEN
                DBMS_OUTPUT.PUT_LINE('Warning: Model does not exist in the database. Adjust wagonModelID.');
            ELSE
                DBMS_OUTPUT.PUT_LINE('Unexpected error: ' || SQLERRM);
            END IF;
    END;

    ------------------------------------------------------------------
    -- TESTE NEGATIVO 2: Wagon duplicado
    ------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Negative Test 2: Duplicate Wagon ---');

    BEGIN
        v_result := register_wagon(
                p_wagonID      => 900001,
                p_operatorID   => 1,
                p_wagonModelID => 1278,
                p_startFacility => 1,
                p_serviceYear  => 2025,
                p_payloadTons  => 26,
                p_status => 'AVAILABLE'
                    );

        DBMS_OUTPUT.PUT_LINE('ERROR: Should have failed (duplicated wagon)!');
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20001 THEN
                DBMS_OUTPUT.PUT_LINE('Expected error (duplicated wagon): ' || SQLERRM);
            ELSE
                DBMS_OUTPUT.PUT_LINE('Unexpected error: ' || SQLERRM);
            END IF;
    END;

    ------------------------------------------------------------------
    -- TESTE NEGATIVO 3: Operator invalido
    ------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Negative Test 3: Invalid Operator ---');

    BEGIN
        v_result := register_wagon(
                p_wagonID      => 900003,
                p_operatorID   => 99999,
                p_wagonModelID => 1278,
                p_startFacility => 1,
                p_serviceYear  => 2025,
                p_payloadTons  => 26,
                p_status => 'AVAILABLE'
                    );

        DBMS_OUTPUT.PUT_LINE('ERROR: Should have failed (invalid operator)!');
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20002 THEN
                DBMS_OUTPUT.PUT_LINE('Expected error (invalid operator): ' || SQLERRM);
            ELSE
                DBMS_OUTPUT.PUT_LINE('Unexpected error: ' || SQLERRM);
            END IF;
    END;

    ------------------------------------------------------------------
    -- TESTE NEGATIVO 4: StartFacility inválida
    ------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Negative Test 4: Invalid Facility ---');

    BEGIN
        v_result := register_wagon(
                p_wagonID      => 900004,
                p_operatorID   => 1,
                p_wagonModelID => 1278,
                p_startFacility => 99999,
                p_serviceYear  => 2025,
                p_payloadTons  => 26,
                p_status => 'AVAILABLE'
                    );

        DBMS_OUTPUT.PUT_LINE('ERROR: It should have failed!');
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20004 THEN
                DBMS_OUTPUT.PUT_LINE('Expected error (invalid facility): ' || SQLERRM);
            ELSE
                DBMS_OUTPUT.PUT_LINE('Unexpected error: ' || SQLERRM);
            END IF;
    END;


    ------------------------------------------------------------------
    -- LIMPEZA
    ------------------------------------------------------------------
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Clean test data ---');
    DELETE FROM Wagon WHERE wagonID IN (900001, 900002, 900003, 900004);
    COMMIT;

    DBMS_OUTPUT.PUT_LINE('Test data removed.');
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '=== END OF TESTS ===');
END;
/