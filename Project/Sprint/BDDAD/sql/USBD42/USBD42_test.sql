BEGIN
    DBMS_OUTPUT.PUT_LINE('==============================================');
    DBMS_OUTPUT.PUT_LINE(' USBD42 - TESTES: Register Train Movement');
    DBMS_OUTPUT.PUT_LINE('==============================================');
END;
/

--------------------------------------------------------------------------------
-- TESTE 1: POSITIVO
-- Objetivo:
--   Registar um movimento sem paragem.
--   departureTime = NULL → arrivalTime reutilizado.
-- Resultado esperado:
--   Movement successfully registered
--------------------------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TESTE 1: Movimento sem paragem ---');

    v_result := registerTrainMovement(
        p_pathID        => 1,
        p_facilityID    => 10,
        p_arrivalTime   => TIMESTAMP '2024-12-01 10:00:00',
        p_departureTime => NULL
    );

    DBMS_OUTPUT.PUT_LINE('Resultado esperado: Movement successfully registered');
    DBMS_OUTPUT.PUT_LINE('Resultado obtido:   ' || v_result);
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Erro inesperado: ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- TESTE 2: POSITIVO
-- Objetivo:
--   Registar um movimento com paragem na facility.
-- Resultado esperado:
--   Movement successfully registered
--------------------------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TESTE 2: Movimento com paragem ---');

    v_result := registerTrainMovement(
        p_pathID        => 1,
        p_facilityID    => 11,
        p_arrivalTime   => TIMESTAMP '2024-12-01 12:00:00',
        p_departureTime => TIMESTAMP '2024-12-01 12:20:00'
    );

    DBMS_OUTPUT.PUT_LINE('Resultado esperado: Movement successfully registered');
    DBMS_OUTPUT.PUT_LINE('Resultado obtido:   ' || v_result);
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Erro inesperado: ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- TESTE 3: NEGATIVO
-- Objetivo:
--   Tentar registar um movimento onde a hora de saída
--   é anterior à hora de chegada.
-- Resultado esperado:
--   Erro: Departure time cannot be earlier than arrival time.
--------------------------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TESTE 3: Departure < Arrival ---');

    v_result := registerTrainMovement(
        p_pathID        => 1,
        p_facilityID    => 12,
        p_arrivalTime   => TIMESTAMP '2024-12-01 14:00:00',
        p_departureTime => TIMESTAMP '2024-12-01 13:50:00'
    );
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Resultado esperado: Departure time cannot be earlier than arrival time.');
        DBMS_OUTPUT.PUT_LINE('Resultado obtido:   ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- TESTE 4: NEGATIVO
-- Objetivo:
--   Tentar registar um movimento para um Path inexistente.
-- Resultado esperado:
--   Erro: Path does not exist.
--------------------------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TESTE 4: Path inexistente ---');

    v_result := registerTrainMovement(
        p_pathID        => 9999,
        p_facilityID    => 10,
        p_arrivalTime   => TIMESTAMP '2024-12-01 15:00:00',
        p_departureTime => NULL
    );
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Resultado esperado: Path does not exist.');
        DBMS_OUTPUT.PUT_LINE('Resultado obtido:   ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- TESTE 5: NEGATIVO
-- Objetivo:
--   Tentar registar um movimento para uma Facility inexistente.
-- Resultado esperado:
--   Erro: Facility does not exist.
--------------------------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TESTE 5: Facility inexistente ---');

    v_result := registerTrainMovement(
        p_pathID        => 1,
        p_facilityID    => 9999,
        p_arrivalTime   => TIMESTAMP '2024-12-01 16:00:00',
        p_departureTime => NULL
    );
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Resultado esperado: Facility does not exist.');
        DBMS_OUTPUT.PUT_LINE('Resultado obtido:   ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- TESTE 6: POSITIVO
-- Objetivo:
--   Atualizar um movimento já existente para o mesmo
--   Path e Facility.
-- Resultado esperado:
--   Movement successfully updated
--------------------------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TESTE 6: Atualização de movimento ---');

    v_result := registerTrainMovement(
        p_pathID        => 1,
        p_facilityID    => 10,
        p_arrivalTime   => TIMESTAMP '2024-12-01 10:05:00',
        p_departureTime => TIMESTAMP '2024-12-01 10:10:00'
    );

    DBMS_OUTPUT.PUT_LINE('Resultado esperado: Movement successfully updated');
    DBMS_OUTPUT.PUT_LINE('Resultado obtido:   ' || v_result);
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Erro inesperado: ' || SQLERRM);
END;
/
