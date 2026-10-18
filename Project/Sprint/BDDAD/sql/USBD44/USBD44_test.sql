------------------------------------------------------------
-- TESTE 1: SimpleTrack com Siding válido
-- Objetivo:
--  Garantir que um SimpleTrack com siding completo é aceito
------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 1: SimpleTrack with valid Siding ---');

    v_result := addLineSegment(
            p_segmentID         => 9000,
            p_lineID            => 1,
            p_orderLine         => 1,
            p_maxWeight         => 200,
            p_length            => 10,
            p_speedLimit        => 120,
            p_isSimple          => 'Y',
            p_maintenanceStatus => 'N',
            p_sidingID          => 9001,
            p_sidingPos         => 5,
            p_sidingLen         => 200
        );

    DBMS_OUTPUT.PUT_LINE('Expected result: success');
    DBMS_OUTPUT.PUT_LINE('Actual result:   ' || v_result);
END;
/

------------------------------------------------------------
-- TESTE 2: SimpleTrack com Siding incompleto
-- Objetivo:
--  Garantir que não é possível criar SimpleTrack quando
--  apenas um dos atributos obrigatórios do Siding é fornecido
------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 2: SimpleTrack with incomplete Siding ---');

BEGIN
        -- Apenas p_sidingPos fornecido, p_sidingLen NULL
        v_result := addLineSegment(
                p_segmentID         => 9002,
                p_lineID            => 1,
                p_orderLine         => 2,
                p_maxWeight         => 200,
                p_length            => 10,
                p_speedLimit        => 120,
                p_isSimple          => 'Y',
                p_maintenanceStatus => 'N',
                p_sidingID          => 9003,
                p_sidingPos         => 5,
                p_sidingLen         => NULL
        );

        DBMS_OUTPUT.PUT_LINE('ERROR: This line should not appear (incomplete siding accepted)');

EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/

------------------------------------------------------------
-- TESTE 3: DoubleTrack sem Siding (válido)
-- Objetivo:
--  Garantir que DoubleTrack sem Siding é aceito
------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 3: DoubleTrack without Siding ---');

    v_result := addLineSegment(
            p_segmentID         => 9004,
            p_lineID            => 1,
            p_orderLine         => 3,
            p_maxWeight         => 200,
            p_length            => 15,
            p_speedLimit        => 100,
            p_isSimple          => 'N',
            p_maintenanceStatus => 'Y'
        );

    DBMS_OUTPUT.PUT_LINE('Expected result: success');
    DBMS_OUTPUT.PUT_LINE('Actual result:   ' || v_result);
END;
/

------------------------------------------------------------
-- TESTE 4: DoubleTrack com Siding (inválido)
-- Objetivo:
--  Garantir que não é possível criar DoubleTrack com Siding
------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 4: DoubleTrack with Siding ---');

BEGIN
        v_result := addLineSegment(
                p_segmentID         => 9005,
                p_lineID            => 1,
                p_orderLine         => 4,
                p_maxWeight         => 200,
                p_length            => 10,
                p_speedLimit        => 120,
                p_isSimple          => 'N',
                p_maintenanceStatus => 'N',
                p_sidingID          => 9006,
                p_sidingPos         => 5,
                p_sidingLen         => 200
        );

        DBMS_OUTPUT.PUT_LINE('ERROR: This line should not appear (DoubleTrack with siding accepted)');

EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/

------------------------------------------------------------
-- TESTE 5: Linha inexistente
-- Objetivo:
--  Garantir que não é possível criar Segment se lineID não existe
------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 5: Non-existent Line ---');

BEGIN
        v_result := addLineSegment(
                p_segmentID         => 9006,
                p_lineID            => 9999,
                p_orderLine         => 1,
                p_maxWeight         => 200,
                p_length            => 10,
                p_speedLimit        => 100,
                p_isSimple          => 'N',
                p_maintenanceStatus => 'Y'
        );

        DBMS_OUTPUT.PUT_LINE('ERROR: This line should not appear (non-existent line accepted)');

EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/

------------------------------------------------------------
-- TESTE 6: SegmentID duplicado
-- Objetivo:
--  Garantir que não é possível criar Segment com segmentID já existente
------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 6: Duplicate SegmentID ---');

BEGIN
        v_result := addLineSegment(
                p_segmentID         => 9000,
                p_lineID            => 1,
                p_orderLine         => 5,
                p_maxWeight         => 200,
                p_length            => 10,
                p_speedLimit        => 120,
                p_isSimple          => 'Y',
                p_maintenanceStatus => 'N'
        );

        DBMS_OUTPUT.PUT_LINE('ERROR: This line should not appear (duplicate segmentID accepted)');

EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/

------------------------------------------------------------
-- TESTE 7: Campos obrigatórios NULL
-- Objetivo:
--  Garantir que campos obrigatórios não podem ser NULL
------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 7: Mandatory fields NULL ---');

BEGIN
        v_result := addLineSegment(
                p_segmentID         => NULL,
                p_lineID            => 1,
                p_orderLine         => 1,
                p_maxWeight         => 200,
                p_length            => 10,
                p_speedLimit        => 120,
                p_isSimple          => 'Y',
                p_maintenanceStatus => 'N'
        );

        DBMS_OUTPUT.PUT_LINE('ERROR: This line should not appear (NULL mandatory accepted)');

EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/

------------------------------------------------------------
-- TESTE 8: p_isSimple inválido
-- Objetivo:
--  Garantir que apenas 'Y' ou 'N' são aceites para p_isSimple
------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 8: Invalid p_isSimple ---');

BEGIN
        v_result := addLineSegment(
                p_segmentID         => 9007,
                p_lineID            => 1,
                p_orderLine         => 1,
                p_maxWeight         => 200,
                p_length            => 10,
                p_speedLimit        => 120,
                p_isSimple          => 'X',
                p_maintenanceStatus => 'N'
        );

        DBMS_OUTPUT.PUT_LINE('ERROR: This line should not appear (invalid p_isSimple accepted)');

EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/

------------------------------------------------------------
-- TESTE 9: maintenanceStatus inválido
-- Objetivo:
--  Garantir que apenas 'Y' ou 'N' são aceites para maintenanceStatus
------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 9: Invalid maintenanceStatus ---');

BEGIN
        v_result := addLineSegment(
                p_segmentID         => 9008,
                p_lineID            => 1,
                p_orderLine         => 1,
                p_maxWeight         => 200,
                p_length            => 10,
                p_speedLimit        => 120,
                p_isSimple          => 'Y',
                p_maintenanceStatus => 'X'
        );

        DBMS_OUTPUT.PUT_LINE('ERROR: This line should not appear (invalid maintenanceStatus accepted)');

EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/
------------------------------------------------------------
-- TESTE 10: maxWeight negativo
-- Objetivo:
--  Garantir que maxWeight negativo ou zero não é aceito
------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 10: Negative maxWeight ---');

BEGIN
        v_result := addLineSegment(
                p_segmentID         => 9010,
                p_lineID            => 1,
                p_orderLine         => 1,
                p_maxWeight         => -100,
                p_length            => 10,
                p_speedLimit        => 120,
                p_isSimple          => 'Y',
                p_maintenanceStatus => 'N'
        );

        DBMS_OUTPUT.PUT_LINE('ERROR: This line should not appear (negative maxWeight accepted)');

EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/

------------------------------------------------------------
-- TESTE 11: length negativo
-- Objetivo:
--  Garantir que length negativo ou zero não é aceito
------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 11: Negative length ---');

BEGIN
        v_result := addLineSegment(
                p_segmentID         => 9011,
                p_lineID            => 1,
                p_orderLine         => 2,
                p_maxWeight         => 200,
                p_length            => -10,
                p_speedLimit        => 120,
                p_isSimple          => 'Y',
                p_maintenanceStatus => 'N'
        );

        DBMS_OUTPUT.PUT_LINE('ERROR: This line should not appear (negative length accepted)');

EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/

------------------------------------------------------------
-- TESTE 12: speedLimit negativo
-- Objetivo:
--  Garantir que speedLimit negativo ou zero não é aceito
------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 12: Negative speedLimit ---');

BEGIN
        v_result := addLineSegment(
                p_segmentID         => 9012,
                p_lineID            => 1,
                p_orderLine         => 3,
                p_maxWeight         => 200,
                p_length            => 10,
                p_speedLimit        => 0,
                p_isSimple          => 'Y',
                p_maintenanceStatus => 'N'
        );

        DBMS_OUTPUT.PUT_LINE('ERROR: This line should not appear (zero/negative speedLimit accepted)');

EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/

------------------------------------------------------------
-- TESTE 13: orderLine negativo
-- Objetivo:
--  Garantir que orderLine negativo ou zero não é aceito
------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TEST 13: Negative orderLine ---');

BEGIN
        v_result := addLineSegment(
                p_segmentID         => 9013,
                p_lineID            => 1,
                p_orderLine         => 0,
                p_maxWeight         => 200,
                p_length            => 10,
                p_speedLimit        => 120,
                p_isSimple          => 'Y',
                p_maintenanceStatus => 'N'
        );

        DBMS_OUTPUT.PUT_LINE('ERROR: This line should not appear (zero/negative orderLine accepted)');

EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/

