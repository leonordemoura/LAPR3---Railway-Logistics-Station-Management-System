SET SERVEROUTPUT ON;

BEGIN
    DBMS_OUTPUT.PUT_LINE('==============================================');
    DBMS_OUTPUT.PUT_LINE(' USBD39 - TESTES: Associate Locomotive to a Planned Train');
    DBMS_OUTPUT.PUT_LINE('==============================================');
END;
/

--------------------------------------------------------------------------------
-- TESTE 1: POSITIVO
-- Objetivo:
--   Associar uma locomotiva válida a um comboio válido
--   que ainda não tem locomotiva atribuída.
-- Resultado esperado:
--   Sucesso
--------------------------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TESTE 1: Associação válida ---');

    v_result := associateLocomotiveToTrain(
        p_trainID      => 1000,
        p_locomotiveID => 2000
    );

    DBMS_OUTPUT.PUT_LINE('Resultado esperado: sucesso');
    DBMS_OUTPUT.PUT_LINE('Resultado obtido:  ' || v_result);

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Erro obtido: ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- TESTE 2: NEGATIVO
-- Objetivo:
--   Tentar associar uma locomotiva a um comboio inexistente.
-- Resultado esperado:
--   Erro: Train does not exist.
--------------------------------------------------------------------------------
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TESTE 2: Comboio inexistente ---');

    associateLocomotiveToTrain(
        p_trainID      => 9999,
        p_locomotiveID => 2000
    );

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Resultado esperado: Train does not exist.');
        DBMS_OUTPUT.PUT_LINE('Resultado obtido:   ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- TESTE 3: NEGATIVO
-- Objetivo:
--   Tentar associar uma locomotiva inexistente.
-- Resultado esperado:
--   Erro: Locomotive does not exist.
--------------------------------------------------------------------------------
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TESTE 3: Locomotiva inexistente ---');

    associateLocomotiveToTrain(
        p_trainID      => 1000,
        p_locomotiveID => 9999
    );

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Resultado esperado: Locomotive does not exist.');
        DBMS_OUTPUT.PUT_LINE('Resultado obtido:   ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- TESTE 4: POSITIVO
-- Objetivo:
--   Tentar associar uma locomotiva a um comboio
--   que já tem locomotiva atribuída.
-- Resultado esperado:
--   Sucesso
--------------------------------------------------------------------------------
DECLARE
v_result VARCHAR2(200);
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TESTE 4: Comboio já tem uma locomotiva associada ---');

    v_result := associateLocomotiveToTrain(
        p_trainID      => 1000,
        p_locomotiveID => 2001
    );

    DBMS_OUTPUT.PUT_LINE('Resultado esperado: Sucesso');
    DBMS_OUTPUT.PUT_LINE('Resultado obtido:  ' || v_result);

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Erro obtido: ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- TESTE 5: NEGATIVO
-- Objetivo:
--   Tentar associar uma locomotiva (assigned) a um comboio
-- Resultado esperado:
--   Locomotive is not available
--------------------------------------------------------------------------------
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TESTE 5: Locomotiva já ASSIGNED ---');

    associateLocomotiveToTrain(
        p_trainID      => 1001,
        p_locomotiveID => 2000
    );

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Resultado esperado: Locomotive is not available for assignment.');
        DBMS_OUTPUT.PUT_LINE('Resultado obtido:   ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- TESTE 6: NEGATIVO
-- Objetivo:
--   Tentar associar uma locomotiva (available) a um comboio
--   que não está PLANNED.
-- Resultado esperado:
--   Train is not available
--------------------------------------------------------------------------------
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TESTE 6: Comboio que não está PLANNED ---');

    associateLocomotiveToTrain(
        p_trainID      => 1002,
        p_locomotiveID => 2002
    );

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Resultado esperado: Locomotive can only be associated with a planned train.');
        DBMS_OUTPUT.PUT_LINE('Resultado obtido:   ' || SQLERRM);
END;
/
