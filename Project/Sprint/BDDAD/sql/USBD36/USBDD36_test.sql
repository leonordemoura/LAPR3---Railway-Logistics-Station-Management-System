DECLARE
    v_facilityId Building.facilityID%TYPE;
    v_buildingTypeId Building.buildingTypeID%TYPE;
    v_buildingID Building.buildingID%TYPE;
    result VARCHAR2(200);
    v_count NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('=== TESTE USBD36: Registo de um Edifício ===');

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Positivo: Todos os valores inseridos são válidos ---');

    BEGIN
        -- Insere os valores nas variáveis
        v_buildingID := 8;  -- buildingID válido
        v_facilityId := 1;  -- facilityID válido
        v_buildingTypeId := 1;  -- buildingTypeID válido
        result := registerNewBuilding(v_buildingID, v_facilityId, v_buildingTypeId);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);

        SELECT COUNT(*) INTO v_count FROM BUILDING WHERE buildingID = v_buildingID;
        DBMS_OUTPUT.PUT_LINE('Edifício inserido na Base de Dados: ' ||
                             CASE WHEN v_count = 1 THEN 'SIM' ELSE 'NAO' END);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('ERRO INESPERADO => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo: ID do Edifício Duplicado ---');

    BEGIN
        -- Insere os valores nas variáveis
        v_buildingID := 8;
        v_facilityId := 1;
        v_buildingTypeId := 1;
        result := registerNewBuilding(v_buildingID ,v_facilityId, v_buildingTypeId);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);

        SELECT COUNT(*) INTO v_count FROM BUILDING WHERE buildingID = v_buildingID;
        DBMS_OUTPUT.PUT_LINE('Edifício inserido na Base de Dados: ' ||
                             CASE WHEN v_count = 1 THEN 'SIM' ELSE 'NAO' END);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;


    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo: ID do Edifício nulo ---');

    BEGIN
        -- Insere os valores nas variáveis
        v_buildingID := NULL;
        v_facilityId := 1;
        v_buildingTypeId := 1;
        result := registerNewBuilding(v_buildingID ,v_facilityId, v_buildingTypeId);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);

        SELECT COUNT(*) INTO v_count FROM BUILDING WHERE buildingID = v_buildingID;
        DBMS_OUTPUT.PUT_LINE('Edifício inserido na Base de Dados: ' ||
                             CASE WHEN v_count = 1 THEN 'SIM' ELSE 'NAO' END);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo: ID da Estação nulo ---');

    BEGIN
        -- Insere os valores nas variáveis
        v_buildingID := 9;
        v_facilityId := NULL;  -- facilityID nulo
        v_buildingTypeId := 1;  -- buildingTypeID válido
        result := registerNewBuilding(v_buildingID ,v_facilityId, v_buildingTypeId);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);

        SELECT COUNT(*) INTO v_count FROM BUILDING WHERE buildingID = v_buildingID;
        DBMS_OUTPUT.PUT_LINE('Edifício inserido na Base de Dados: ' ||
                             CASE WHEN v_count = 1 THEN 'SIM' ELSE 'NAO' END);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo: ID do Tipo de Edifício nulo ---');

    BEGIN
        -- Insere os valores nas variáveis
        v_buildingID := 9;
        v_facilityId := 1;
        v_buildingTypeId := NULL;
        result := registerNewBuilding(v_buildingID, v_facilityId, v_buildingTypeId);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);

        SELECT COUNT(*) INTO v_count FROM BUILDING WHERE buildingID = v_buildingID;
        DBMS_OUTPUT.PUT_LINE('Edifício inserido na Base de Dados: ' ||
                             CASE WHEN v_count = 1 THEN 'SIM' ELSE 'NAO' END);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo: ID do Edifício inválido ---');

    BEGIN
        -- Insere os valores nas variáveis
        v_buildingID := -1;
        v_facilityId := 0;
        v_buildingTypeId := 1;
        result := registerNewBuilding( v_buildingID,v_facilityId, v_buildingTypeId);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);

        SELECT COUNT(*) INTO v_count FROM BUILDING WHERE buildingID = v_buildingID;
        DBMS_OUTPUT.PUT_LINE('Edifício inserido na Base de Dados: ' ||
                             CASE WHEN v_count = 1 THEN 'SIM' ELSE 'NAO' END);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo: ID da Estação inválido ---');

    BEGIN
        -- Insere os valores nas variáveis
        v_buildingID := 9;
        v_facilityId := -1;
        v_buildingTypeId := 1;
        result := registerNewBuilding( v_buildingID,v_facilityId, v_buildingTypeId);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);

        SELECT COUNT(*) INTO v_count FROM BUILDING WHERE buildingID = v_buildingID;
        DBMS_OUTPUT.PUT_LINE('Edifício inserido na Base de Dados: ' ||
                             CASE WHEN v_count = 1 THEN 'SIM' ELSE 'NAO' END);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo: ID do Tipo de Edifício inválido ---');

    BEGIN
        v_buildingID := 8;
        v_facilityId := 1;
        v_buildingTypeId := 4;
        result := registerNewBuilding( v_buildingID,v_facilityId, v_buildingTypeId);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);

        SELECT COUNT(*) INTO v_count FROM BUILDING WHERE buildingID = v_buildingID;
        DBMS_OUTPUT.PUT_LINE('Edifício inserido na Base de Dados: ' ||
                             CASE WHEN v_count = 1 THEN 'SIM' ELSE 'NAO' END);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '=== Fim do Teste USBD36 ===');
    DELETE FROM BUILDING WHERE BUILDINGID > 7;
    COMMIT;
END;