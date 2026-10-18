SET SERVEROUTPUT ON;

BEGIN
    DBMS_OUTPUT.PUT_LINE('==============================================');
    DBMS_OUTPUT.PUT_LINE(' USBD45 - TESTES: Add Line With Segment');
    DBMS_OUTPUT.PUT_LINE('==============================================');
END;
/

DECLARE
v_result VARCHAR2(200);
    v_count NUMBER;
BEGIN
    -- TESTE 1: DOUBLE track line
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 1: Add DOUBLE track line ---');
BEGIN
        v_result := addLineWithSegment(
                p_lineID          => 8000,
                p_startFacilityID => 1,
                p_endFacilityID   => 2,
                p_lineOwnerID     => 1,
                p_gaugeID         => 1668,
                p_isElectrified   => 'Y',
                p_segmentID       => 88001,
                p_orderLine       => 1,
                p_maxWeight       => 9000,
                p_length          => 1200,
                p_speedLimit      => 140,
                p_trackType       => 'DOUBLE'
                    );
        DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

SELECT COUNT(*) INTO v_count FROM Line WHERE lineID = 8000;
DBMS_OUTPUT.PUT_LINE('  Line exists: ' || CASE WHEN v_count = 1 THEN 'YES' ELSE 'NO' END);
SELECT COUNT(*) INTO v_count FROM LineSegment WHERE lineID = 8000;
DBMS_OUTPUT.PUT_LINE('  Segment exists: ' || CASE WHEN v_count = 1 THEN 'YES' ELSE 'NO' END);
SELECT COUNT(*) INTO v_count FROM DoubleTrack WHERE segmentID = 88001;
DBMS_OUTPUT.PUT_LINE('  DoubleTrack exists: ' || CASE WHEN v_count = 1 THEN 'YES' ELSE 'NO' END);

EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('ERROR: ' || SQLERRM);
END;

    -- TESTE 2: SIMPLE track line
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 2: Add SIMPLE track line ---');
BEGIN
        v_result := addLineWithSegment(
                p_lineID          => 8001,
                p_startFacilityID => 1,
                p_endFacilityID   => 2,
                p_lineOwnerID     => 1,
                p_gaugeID         => 1668,
                p_isElectrified   => 'N',
                p_segmentID       => 88002,
                p_orderLine       => 1,
                p_maxWeight       => 8000,
                p_length          => 800,
                p_speedLimit      => 120,
                p_trackType       => 'SIMPLE'
                    );
        DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);

SELECT COUNT(*) INTO v_count FROM SimpleTrack WHERE segmentID = 88002;
DBMS_OUTPUT.PUT_LINE('  SimpleTrack exists: ' || CASE WHEN v_count = 1 THEN 'YES' ELSE 'NO' END);

EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('ERROR: ' || SQLERRM);
END;

    -- TESTE 3: Same start/end facility
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 3: Same start/end facility (should fail) ---');
BEGIN
        v_result := addLineWithSegment(
                p_lineID          => 8002,
                p_startFacilityID => 1,
                p_endFacilityID   => 1,
                p_lineOwnerID     => 1,
                p_gaugeID         => 1668,
                p_isElectrified   => 'Y',
                p_segmentID       => 88003,
                p_orderLine       => 1,
                p_maxWeight       => 9000,
                p_length          => 1200,
                p_speedLimit      => 140,
                p_trackType       => 'DOUBLE'
                    );
        DBMS_OUTPUT.PUT_LINE('Should have failed but got: ' || v_result);
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;

    -- TESTE 4: Non-existent facility
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 4: Non-existent facility (should fail) ---');
BEGIN
        v_result := addLineWithSegment(
                p_lineID          => 8003,
                p_startFacilityID => 99999,
                p_endFacilityID   => 2,
                p_lineOwnerID     => 1,
                p_gaugeID         => 1668,
                p_isElectrified   => 'Y',
                p_segmentID       => 88004,
                p_orderLine       => 1,
                p_maxWeight       => 9000,
                p_length          => 1200,
                p_speedLimit      => 140,
                p_trackType       => 'DOUBLE'
                    );
        DBMS_OUTPUT.PUT_LINE('Should have failed but got: ' || v_result);
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;

    -- TESTE 5: Non-existent gauge
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 5: Non-existent gauge (should fail) ---');
BEGIN
        v_result := addLineWithSegment(
                p_lineID          => 8004,
                p_startFacilityID => 1,
                p_endFacilityID   => 2,
                p_lineOwnerID     => 1,
                p_gaugeID         => 9999,
                p_isElectrified   => 'Y',
                p_segmentID       => 88005,
                p_orderLine       => 1,
                p_maxWeight       => 9000,
                p_length          => 1200,
                p_speedLimit      => 140,
                p_trackType       => 'DOUBLE'
                    );
        DBMS_OUTPUT.PUT_LINE('Should have failed but got: ' || v_result);
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;

    -- TESTE 6: Duplicate line ID
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 6: Duplicate line ID (should fail) ---');
BEGIN
        v_result := addLineWithSegment(
                p_lineID          => 8005,
                p_startFacilityID => 1,
                p_endFacilityID   => 2,
                p_lineOwnerID     => 1,
                p_gaugeID         => 1668,
                p_isElectrified   => 'Y',
                p_segmentID       => 88006,
                p_orderLine       => 1,
                p_maxWeight       => 9000,
                p_length          => 1200,
                p_speedLimit      => 140,
                p_trackType       => 'DOUBLE'
                    );
        DBMS_OUTPUT.PUT_LINE('First insert: ' || v_result);

        v_result := addLineWithSegment(
                p_lineID          => 8005,
                p_startFacilityID => 1,
                p_endFacilityID   => 2,
                p_lineOwnerID     => 1,
                p_gaugeID         => 1668,
                p_isElectrified   => 'Y',
                p_segmentID       => 88007,
                p_orderLine       => 1,
                p_maxWeight       => 9000,
                p_length          => 1200,
                p_speedLimit      => 140,
                p_trackType       => 'DOUBLE'
                    );
        DBMS_OUTPUT.PUT_LINE('Should have failed but got: ' || v_result);
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;

    -- TESTE 7: Invalid track type
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 7: Invalid track type (should fail) ---');
BEGIN
        v_result := addLineWithSegment(
                p_lineID          => 8006,
                p_startFacilityID => 1,
                p_endFacilityID   => 2,
                p_lineOwnerID     => 1,
                p_gaugeID         => 1668,
                p_isElectrified   => 'Y',
                p_segmentID       => 88008,
                p_orderLine       => 1,
                p_maxWeight       => 9000,
                p_length          => 1200,
                p_speedLimit      => 140,
                p_trackType       => 'INVALID'
                    );
        DBMS_OUTPUT.PUT_LINE('Should have failed but got: ' || v_result);
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;

    -- TESTE 8: Negative length
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 8: Negative length (should fail) ---');
BEGIN
        v_result := addLineWithSegment(
                p_lineID          => 8007,
                p_startFacilityID => 1,
                p_endFacilityID   => 2,
                p_lineOwnerID     => 1,
                p_gaugeID         => 1668,
                p_isElectrified   => 'Y',
                p_segmentID       => 88009,
                p_orderLine       => 1,
                p_maxWeight       => 9000,
                p_length          => -100,
                p_speedLimit      => 140,
                p_trackType       => 'DOUBLE'
                    );
        DBMS_OUTPUT.PUT_LINE('Should have failed but got: ' || v_result);
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;

    DBMS_OUTPUT.PUT_LINE('ALL TESTS COMPLETED!');

END;
/

-- Remover os dados inseridos durante os testes
-- DELETE FROM SimpleTrack WHERE segmentID IN (88002);
-- DELETE FROM DoubleTrack WHERE segmentID IN (88001,88003,88004,88005,88006,88007,88008,88009);
-- DELETE FROM LineSegment WHERE segmentID IN (88001,88002,88003,88004,88005,88006,88007,88008,88009);
-- DELETE FROM Line WHERE lineID IN (8000,8001,8002,8003,8004,8005,8006,8007);
-- COMMIT;