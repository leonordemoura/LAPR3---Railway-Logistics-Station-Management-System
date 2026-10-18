DECLARE
    v_freightID Freight.freightID%TYPE;
    v_startFacilityID Freight.startFacilityID%TYPE;
    v_endFacilityID Freight.endFacilityID%TYPE;
    v_date Freight."date"%TYPE;
    v_wagonType WagonType.name%TYPE;
    v_numberWagons NUMBER;
    result VARCHAR2(200);
    v_count NUMBER;
    v_count_wagons_before NUMBER;
    v_count_wagons_after NUMBER;
BEGIN
    DBMS_OUTPUT. PUT_LINE('=== TESTE USBD40:  Registo de um Frete ===');

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Positivo: Todos os valores inseridos são válidos ---');

    BEGIN
        v_freightID := 1000;
        v_startFacilityID := 50;
        v_endFacilityID := 2;
        v_date := TO_DATE('2025-12-31', 'YYYY-MM-DD');
        v_wagonType := 'Container wagon (max 40" HC)';
        v_numberWagons := 3;

        SELECT COUNT(*) INTO v_count_wagons_before FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;

        result := registerFreight(v_freightID, v_startFacilityID, v_endFacilityID, v_date, v_wagonType, v_numberWagons);

        DBMS_OUTPUT. PUT_LINE('Resultado: ' || result);

        SELECT COUNT(*) INTO v_count FROM Freight WHERE freightID = v_freightID;
        DBMS_OUTPUT.PUT_LINE('Frete inserido na Base de Dados: ' ||
                             CASE WHEN v_count = 1 THEN 'SIM' ELSE 'NAO' END);

        SELECT COUNT(*) INTO v_count_wagons_after FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;
        DBMS_OUTPUT.PUT_LINE('O Frete continha ' || v_count_wagons_before || ' vagões associados. Agora o Frete tem ' || v_count_wagons_after || ' vagões associados.');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('ERRO INESPERADO => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo:  Wagons insuficientes na estação ---');

    BEGIN
        v_freightID := 1001;
        v_startFacilityID := 50;
        v_endFacilityID := 2;
        v_date := TO_DATE('2025-12-31', 'YYYY-MM-DD');
        v_wagonType := 'grain wagon';
        v_numberWagons := 999;

        SELECT COUNT(*) INTO v_count_wagons_before FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;

        result := registerFreight(v_freightID, v_startFacilityID, v_endFacilityID, v_date, v_wagonType, v_numberWagons);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);
        SELECT COUNT(*) INTO v_count_wagons_after FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;
        DBMS_OUTPUT.PUT_LINE('O Frete continha ' || v_count_wagons_before || ' vagões associados. Agora o Frete tem ' || v_count_wagons_after || ' vagões associados.');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo: ID do Frete Duplicado ---');

    BEGIN
        v_freightID := 1000;
        v_startFacilityID := 1;
        v_endFacilityID := 3;
        v_date := TO_DATE('2025-12-31', 'YYYY-MM-DD');
        v_wagonType := 'grain wagon';
        v_numberWagons := 2;

        SELECT COUNT(*) INTO v_count_wagons_before FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;

        result := registerFreight(v_freightID, v_startFacilityID, v_endFacilityID, v_date, v_wagonType, v_numberWagons);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);
        SELECT COUNT(*) INTO v_count_wagons_after FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;
        DBMS_OUTPUT.PUT_LINE('O Frete continha ' || v_count_wagons_before || ' vagões associados. Agora o Frete tem ' || v_count_wagons_after || ' vagões associados.');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo: ID do Frete nulo ---');

    BEGIN
        v_freightID := NULL;
        v_startFacilityID := 1;
        v_endFacilityID := 2;
        v_date := TO_DATE('2025-12-31', 'YYYY-MM-DD');
        v_wagonType := 'grain wagon';
        v_numberWagons := 2;

        SELECT COUNT(*) INTO v_count_wagons_before FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;

        result := registerFreight(v_freightID, v_startFacilityID, v_endFacilityID, v_date, v_wagonType, v_numberWagons);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);
        SELECT COUNT(*) INTO v_count_wagons_after FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;
        DBMS_OUTPUT.PUT_LINE('O Frete continha ' || v_count_wagons_before || ' vagões associados. Agora o Frete tem ' || v_count_wagons_after || ' vagões associados.');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo:  Estação de Partida nula ---');

    BEGIN
        v_freightID := 1001;
        v_startFacilityID := NULL;
        v_endFacilityID := 2;
        v_date := TO_DATE('2025-12-31', 'YYYY-MM-DD');
        v_wagonType := 'grain wagon';
        v_numberWagons := 2;

        SELECT COUNT(*) INTO v_count_wagons_before FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;

        result := registerFreight(v_freightID, v_startFacilityID, v_endFacilityID, v_date, v_wagonType, v_numberWagons);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);
        SELECT COUNT(*) INTO v_count_wagons_after FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;
        DBMS_OUTPUT.PUT_LINE('O Frete continha ' || v_count_wagons_before || ' vagões associados. Agora o Frete tem ' || v_count_wagons_after || ' vagões associados.');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo: Estação de Chegada nula ---');

    BEGIN
        v_freightID := 1001;
        v_startFacilityID := 1;
        v_endFacilityID := NULL;
        v_date := TO_DATE('2025-12-31', 'YYYY-MM-DD');
        v_wagonType := 'grain wagon';
        v_numberWagons := 2;

        SELECT COUNT(*) INTO v_count_wagons_before FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;

        result := registerFreight(v_freightID, v_startFacilityID, v_endFacilityID, v_date, v_wagonType, v_numberWagons);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);
        SELECT COUNT(*) INTO v_count_wagons_after FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;
        DBMS_OUTPUT.PUT_LINE('O Frete continha ' || v_count_wagons_before || ' vagões associados. Agora o Frete tem ' || v_count_wagons_after || ' vagões associados.');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo: Data nula ---');

    BEGIN
        v_freightID := 1001;
        v_startFacilityID := 1;
        v_endFacilityID := 2;
        v_date := NULL;
        v_wagonType := 'grain wagon';
        v_numberWagons := 2;

        SELECT COUNT(*) INTO v_count_wagons_before FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;

        result := registerFreight(v_freightID, v_startFacilityID, v_endFacilityID, v_date, v_wagonType, v_numberWagons);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);
        SELECT COUNT(*) INTO v_count_wagons_after FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;
        DBMS_OUTPUT.PUT_LINE('O Frete continha ' || v_count_wagons_before || ' vagões associados. Agora o Frete tem ' || v_count_wagons_after || ' vagões associados.');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo: Tipo de Vagão nulo ---');

    BEGIN
        v_freightID := 1001;
        v_startFacilityID := 1;
        v_endFacilityID := 2;
        v_date := TO_DATE('2025-12-31', 'YYYY-MM-DD');
        v_wagonType := NULL;
        v_numberWagons := 2;

        SELECT COUNT(*) INTO v_count_wagons_before FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;

        result := registerFreight(v_freightID, v_startFacilityID, v_endFacilityID, v_date, v_wagonType, v_numberWagons);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);
        SELECT COUNT(*) INTO v_count_wagons_after FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;
        DBMS_OUTPUT.PUT_LINE('O Frete continha ' || v_count_wagons_before || ' vagões associados. Agora o Frete tem ' || v_count_wagons_after || ' vagões associados.');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo:  Número de Vagões nulo ---');

    BEGIN
        v_freightID := 1001;
        v_startFacilityID := 1;
        v_endFacilityID := 2;
        v_date := TO_DATE('2025-12-31', 'YYYY-MM-DD');
        v_wagonType := 'grain wagon';
        v_numberWagons := NULL;

        SELECT COUNT(*) INTO v_count_wagons_before FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;

        result := registerFreight(v_freightID, v_startFacilityID, v_endFacilityID, v_date, v_wagonType, v_numberWagons);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);
        SELECT COUNT(*) INTO v_count_wagons_after FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;
        DBMS_OUTPUT.PUT_LINE('O Frete continha ' || v_count_wagons_before || ' vagões associados. Agora o Frete tem ' || v_count_wagons_after || ' vagões associados.');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT. PUT_LINE(CHR(10) || '--- Teste Negativo: Estação de Partida inválida ---');

    BEGIN
        v_freightID := 1001;
        v_startFacilityID := 9999;
        v_endFacilityID := 2;
        v_date := TO_DATE('2025-12-31', 'YYYY-MM-DD');
        v_wagonType := 'grain wagon';
        v_numberWagons := 2;

        SELECT COUNT(*) INTO v_count_wagons_before FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;

        result := registerFreight(v_freightID, v_startFacilityID, v_endFacilityID, v_date, v_wagonType, v_numberWagons);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);
        SELECT COUNT(*) INTO v_count_wagons_after FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;
        DBMS_OUTPUT.PUT_LINE('O Frete continha ' || v_count_wagons_before || ' vagões associados. Agora o Frete tem ' || v_count_wagons_after || ' vagões associados.');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT. PUT_LINE(CHR(10) || '--- Teste Negativo: Estação de Chegada inválida ---');

    BEGIN
        v_freightID := 1001;
        v_startFacilityID := 1;
        v_endFacilityID := 9999;
        v_date := TO_DATE('2025-12-31', 'YYYY-MM-DD');
        v_wagonType := 'grain wagon';
        v_numberWagons := 2;

        SELECT COUNT(*) INTO v_count_wagons_before FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;

        result := registerFreight(v_freightID, v_startFacilityID, v_endFacilityID, v_date, v_wagonType, v_numberWagons);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);
        SELECT COUNT(*) INTO v_count_wagons_after FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;
        DBMS_OUTPUT.PUT_LINE('O Frete continha ' || v_count_wagons_before || ' vagões associados. Agora o Frete tem ' || v_count_wagons_after || ' vagões associados.');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo:  Estação de Partida igual à de Chegada ---');

    BEGIN
        v_freightID := 1001;
        v_startFacilityID := 1;
        v_endFacilityID := 1;  -- Mesma estação
        v_date := TO_DATE('2025-12-31', 'YYYY-MM-DD');
        v_wagonType := 'grain wagon';
        v_numberWagons := 2;

        SELECT COUNT(*) INTO v_count_wagons_before FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;

        result := registerFreight(v_freightID, v_startFacilityID, v_endFacilityID, v_date, v_wagonType, v_numberWagons);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);
        SELECT COUNT(*) INTO v_count_wagons_after FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;
        DBMS_OUTPUT.PUT_LINE('O Frete continha ' || v_count_wagons_before || ' vagões associados. Agora o Frete tem ' || v_count_wagons_after || ' vagões associados.');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT. PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo: Tipo de Vagão inválido ---');

    BEGIN
        v_freightID := 1001;
        v_startFacilityID := 1;
        v_endFacilityID := 2;
        v_date := TO_DATE('2025-12-31', 'YYYY-MM-DD');
        v_wagonType := 'TipoInexistente';
        v_numberWagons := 2;

        SELECT COUNT(*) INTO v_count_wagons_before FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;

        result := registerFreight(v_freightID, v_startFacilityID, v_endFacilityID, v_date, v_wagonType, v_numberWagons);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);
        SELECT COUNT(*) INTO v_count_wagons_after FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;
        DBMS_OUTPUT.PUT_LINE('O Frete continha ' || v_count_wagons_before || ' vagões associados. Agora o Frete tem ' || v_count_wagons_after || ' vagões associados.');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo: Número de Vagões zero ---');

    BEGIN
        v_freightID := 1001;
        v_startFacilityID := 1;
        v_endFacilityID := 2;
        v_date := TO_DATE('2025-12-31', 'YYYY-MM-DD');
        v_wagonType := 'grain wagon';
        v_numberWagons := 0;

        SELECT COUNT(*) INTO v_count_wagons_before FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;

        result := registerFreight(v_freightID, v_startFacilityID, v_endFacilityID, v_date, v_wagonType, v_numberWagons);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);
        SELECT COUNT(*) INTO v_count_wagons_after FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;
        DBMS_OUTPUT.PUT_LINE('O Frete continha ' || v_count_wagons_before || ' vagões associados. Agora o Frete tem ' || v_count_wagons_after || ' vagões associados.');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Teste Negativo: Número de Vagões negativo ---');

    BEGIN
        v_freightID := 1001;
        v_startFacilityID := 1;
        v_endFacilityID := 2;
        v_date := TO_DATE('2025-12-31', 'YYYY-MM-DD');
        v_wagonType := 'GRAIN WAGON';
        v_numberWagons := -5;

        SELECT COUNT(*) INTO v_count_wagons_before FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;

        result := registerFreight(v_freightID, v_startFacilityID, v_endFacilityID, v_date, v_wagonType, v_numberWagons);

        DBMS_OUTPUT.PUT_LINE('Resultado: ' || result);
        SELECT COUNT(*) INTO v_count_wagons_after FROM FREIGHT_WAGON fw WHERE fw.FREIGHTID = v_freightID;
        DBMS_OUTPUT.PUT_LINE('O Frete continha ' || v_count_wagons_before || ' vagões associados. Agora o Frete tem ' || v_count_wagons_after || ' vagões associados.');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro Esperado => ' || SQLERRM);
    END;

    DBMS_OUTPUT.PUT_LINE(CHR(10) || '=== Fim do Teste registerFreight ===');

    -- Limpeza dos dados de teste
    DELETE FROM Freight_Wagon WHERE freightID = 1000;
    DELETE FROM Freight WHERE freightID = 1000;
    COMMIT;
END;