-- dados existentes

-- facilityTypeID | name          | isIntermodal
-- 1              | STATION       | N
-- 2              | TERMINAL      | Y
-- 3              | TERMINAL      | N
-- 4              | FREIGHT_YARD  | N

------------------------------------------------------------
-- TESTE 1: TESTE POSITIVO
-- Objetivo:
--  Verificar se é possível registar uma Facility válida
--  do tipo STATION:
--   - O nome deve ser corretamente normalizado
--   - A Facility deve ser inserida na tabela Facility
--   - O tipo deve corresponder a STATION (isIntermodal = N)
------------------------------------------------------------
DECLARE
v_result NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('=== TEST USBD35: Register Facility ===');

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Positive Test: Valid STATION ---');

    v_result := registerFacility(
            p_facilityID     => 5001,
            p_name           => 'testname',
            p_facilityTypeID => 1,  -- STATION
            p_isIntermodal   => 'N'
                );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);
END;
/

------------------------------------------------------------
-- TESTE 2: TESTE NEGATIVO – NOME DUPLICADO
-- Objetivo:
--  Garantir que não é possível criar uma Facility
--  com um nome já existente (ignorando espaços e maiúsculas)
------------------------------------------------------------
DECLARE
v_result NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Negative Test: Duplicate name ---');

BEGIN
        v_result := registerFacility(
                p_facilityID     => 5002,
                p_name           => 'testName',
                p_facilityTypeID => 1,
                p_isIntermodal   => 'N'
                    );

        DBMS_OUTPUT.PUT_LINE(
                'ERROR: This message should not appear (duplicate accepted)'
        );
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/

------------------------------------------------------------
-- TESTE 3: TESTE NEGATIVO – FacilityType inexistente
-- Objetivo:
--  Garantir que não é possível usar um facilityTypeID inválido
------------------------------------------------------------
DECLARE
v_result NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Negative Test: Invalid facilityTypeID ---');

BEGIN
        v_result := registerFacility(
                p_facilityID     => 5003,
                p_name           => 'Lisboa Oriente',
                p_facilityTypeID => 999, -- não existe
                p_isIntermodal   => 'N'
                    );

        DBMS_OUTPUT.PUT_LINE(
                'ERROR: This message should not appear (invalid type accepted)'
        );
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/

------------------------------------------------------------
-- TESTE 4: TESTE NEGATIVO – Incompatibilidade tipo / isIntermodal
-- Objetivo:
--  Garantir que STATION não aceita isIntermodal = Y
------------------------------------------------------------
DECLARE
v_result NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Negative Test: Invalid intermodal for STATION ---');

BEGIN
        v_result := registerFacility(
                p_facilityID     => 5004,
                p_name           => 'Braga Station',
                p_facilityTypeID => 1,   -- STATION
                p_isIntermodal   => 'Y'  -- inválido
                    );

        DBMS_OUTPUT.PUT_LINE(
                'ERROR: This message should not appear (invalid intermodal accepted)'
        );
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/

------------------------------------------------------------
-- TESTE 5: TESTE POSITIVO – TERMINAL intermodal
-- Objetivo:
--  Verificar que TERMINAL com isIntermodal = Y é aceite
------------------------------------------------------------
DECLARE
v_result NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Positive Test: TERMINAL intermodal ---');

    v_result := registerFacility(
            p_facilityID     => 5005,
            p_name           => 'Lisboa Terminal',
            p_facilityTypeID => 2,  -- TERMINAL
            p_isIntermodal   => 'Y'
                );

    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);
END;
/

------------------------------------------------------------
-- TESTE 6: TESTE NEGATIVO – isIntermodal inválido
-- Objetivo:
--  Garantir que apenas 'Y' ou 'N' são aceites
------------------------------------------------------------
DECLARE
v_result NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Negative Test: Invalid isIntermodal ---');

BEGIN
        v_result := registerFacility(
                p_facilityID     => 5006,
                p_name           => 'Porto Terminal',
                p_facilityTypeID => 2,
                p_isIntermodal   => 'X'
                    );

        DBMS_OUTPUT.PUT_LINE(
                'ERROR: This message should not appear (invalid isIntermodal accepted)'
        );
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/

------------------------------------------------------------
-- TESTE 7: TESTE NEGATIVO – Facility ID NULL
-- Objetivo:
--  Garantir que não é possível criar uma Facility sem ID
------------------------------------------------------------
DECLARE
v_result NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Negative Test: NULL Facility ID ---');

BEGIN
        v_result := registerFacility(
                p_facilityID     => NULL,
                p_name           => 'Aveiro',
                p_facilityTypeID => 1,
                p_isIntermodal   => 'N'
                    );

        DBMS_OUTPUT.PUT_LINE(
                'ERROR: This message should not appear (NULL Facility ID accepted)'
        );
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/

------------------------------------------------------------
-- TESTE 8: TESTE NEGATIVO – Nome NULL
-- Objetivo:
--  Garantir que não é possível criar uma Facility sem nome
------------------------------------------------------------
DECLARE
v_result NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Negative Test: NULL Facility Name ---');

BEGIN
        v_result := registerFacility(
                p_facilityID     => 5007,
                p_name           => NULL,
                p_facilityTypeID => 1,
                p_isIntermodal   => 'N'
                    );

        DBMS_OUTPUT.PUT_LINE(
                'ERROR: This message should not appear (NULL name accepted)'
        );
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/
------------------------------------------------------------
-- TESTE 9: TESTE NEGATIVO – FacilityTypeID NULL
-- Objetivo:
--  Garantir que não é possível criar uma Facility sem tipo
------------------------------------------------------------
DECLARE
v_result NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Negative Test: NULL FacilityTypeID ---');

BEGIN
        v_result := registerFacility(
                p_facilityID     => 5008,
                p_name           => 'Coimbra',
                p_facilityTypeID => NULL,
                p_isIntermodal   => 'N'
                    );

        DBMS_OUTPUT.PUT_LINE(
                'ERROR: This message should not appear (NULL facilityTypeID accepted)'
        );
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/
------------------------------------------------------------
-- TESTE 10: TESTE NEGATIVO – isIntermodal NULL
-- Objetivo:
--  Garantir que não é possível criar uma Facility sem definir isIntermodal
------------------------------------------------------------
DECLARE
v_result NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Negative Test: NULL isIntermodal ---');

BEGIN
        v_result := registerFacility(
                p_facilityID     => 5009,
                p_name           => 'Faro',
                p_facilityTypeID => 2,
                p_isIntermodal   => NULL
                    );

        DBMS_OUTPUT.PUT_LINE(
                'ERROR: This message should not appear (NULL isIntermodal accepted)'
        );
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/
------------------------------------------------------------
-- TESTE 11: TESTE NEGATIVO – FacilityID DUPLICADO
-- Objetivo:
--  Garantir que não é possível criar uma Facility com ID já existente
------------------------------------------------------------
DECLARE
v_result NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Negative Test: DUPLICATE ID ---');

    -- Inserção inicial válida
    v_result := registerFacility(
            p_facilityID     => 6000,
            p_name           => 'Faro TERMINAL',
            p_facilityTypeID => 2,
            p_isIntermodal   => 'N'
        );

    -- Tentativa de inserir com o mesmo ID
BEGIN
        v_result := registerFacility(
                p_facilityID     => 6000,
                p_name           => 'Faro STATION',
                p_facilityTypeID => 1,
                p_isIntermodal   => 'N'
            );

        DBMS_OUTPUT.PUT_LINE(
            'ERROR: This message should not appear (duplicate ID accepted)'
        );
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
END;
/
