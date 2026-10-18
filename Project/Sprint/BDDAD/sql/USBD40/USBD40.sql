CREATE OR REPLACE FUNCTION registerFreight(
    thisFreightID        IN Freight.freightID%TYPE,
    thisStartFacilityID  IN Freight.startFacilityID%TYPE,
    thisEndFacilityID    IN Freight.endFacilityID%TYPE,
    thisDate             IN Freight."date"%TYPE,
    thisWagonType        IN WagonType.name%TYPE,
    numberWagons         IN NUMBER
)
    RETURN VARCHAR2
IS
    freight_exists NUMBER;
    start_facility_exists NUMBER;
    end_facility_exists NUMBER;
    wagon_type_exists NUMBER;
    wagon_type_name WagonType.name%TYPE;
    available_wagons NUMBER;
    wagonList SYS_REFCURSOR;
    wagonID Freight_Wagon.wagonID%TYPE;
BEGIN
    -- 1. Validar que campos obrigatórios não são nulos
    IF thisFreightID IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001, 'ERRO: O ID do Frete não pode ser nulo.');
    END if;

    IF thisStartFacilityID IS NULL THEN
        RAISE_APPLICATION_ERROR(-20002, 'ERRO: O ID da Estação de Partida não pode ser nulo.');
    END if;

    IF thisEndFacilityID IS NULL THEN
        RAISE_APPLICATION_ERROR(-20003, 'ERRO: O ID da Estação de Chegada não pode ser nulo.');
    END if;

    IF thisDate IS NULL THEN
        RAISE_APPLICATION_ERROR(-20004, 'ERRO: A data não pode ser nula.');
    END if;

    IF thisWagonType IS NULL THEN
        RAISE_APPLICATION_ERROR(-20005, 'ERRO: O Tipo de Vagão não pode ser nulo.');
    END if;

    IF numberWagons IS NULL THEN
        RAISE_APPLICATION_ERROR(-20006, 'ERRO: O número de Vagões não pode ser nulo.');
    END if;

    -- 2. Validar se o freight já existe
    SELECT COUNT(*) INTO freight_exists
    FROM Freight WHERE freightID = thisFreightID;
    IF freight_exists > 0 THEN
        RAISE_APPLICATION_ERROR(-20007, 'ERRO: O Frete ' || thisFreightID || ' já existe.');
    END IF;

    -- 3. Validar se a start facility existe
    SELECT COUNT(*) INTO start_facility_exists
    FROM Facility WHERE facilityID = thisStartFacilityID;
    IF start_facility_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20008, 'ERRO: A Estação de Partida ' || thisStartFacilityID || ' não existe.');
    END IF;

    -- 4. Validar se a end facility existe
    SELECT COUNT(*) INTO end_facility_exists
    FROM Facility WHERE facilityID = thisEndFacilityID;
    IF end_facility_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20009, 'ERRO: A Estação de Chegada ' || thisEndFacilityID || ' não existe.');
    END IF;

    -- 5. Validar se a Estação de Origem e Estação de Destino são diferentes
    IF thisStartFacilityID = thisEndFacilityID THEN
        RAISE_APPLICATION_ERROR(-20010, 'ERRO: A Estação de Partida e a de Chegada não podem ser as mesmas.');
    END IF;

    -- 6. Normalizar o nome do wagon type
    wagon_type_name := TRIM(LOWER(thisWagonType));

    -- 6. Validar se o wagon type existe
    SELECT COUNT(*) INTO wagon_type_exists
    FROM WagonType WHERE TRIM(LOWER(name)) = wagon_type_name;
    IF wagon_type_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20011, 'ERRO: O Tipo de Vagão "' || thisWagonType || '" não existe.');
    END IF;

    -- 7. Validar se o número de wagons é positivo
    IF numberWagons <= 0 THEN
        RAISE_APPLICATION_ERROR(-20012, 'ERRO: O Frete tem de ter pelo menos um.');
    END IF;

    -- 8. Verificar disponibilidade de wagons do tipo especificado na estação de início do freight
    SELECT COUNT(*) INTO available_wagons
    FROM Wagon w
        INNER JOIN WagonModel wm ON w.wagonModelID = wm.wagonModelID
        INNER JOIN WagonType wt ON wm.wagonTypeID = WT.wagonTypeID
        LEFT JOIN (
            SELECT
                fw.wagonID,
                f.endFacilityID AS lastEndFacility
            FROM Freight_Wagon fw
                INNER JOIN Freight f ON fw.freightID = f.freightID
            WHERE f."date" < thisDate
            AND f."date" = (
                    SELECT MAX(f2."date")
                    FROM Freight_Wagon fw2
                    INNER JOIN Freight f2 ON fw2.freightID = f2.freightID
                    WHERE fw2.wagonID = fw. wagonID
                    AND f2."date" < thisDate
            )
        )lastFreight ON w.wagonID = lastFreight.wagonID
    WHERE TRIM(LOWER(wt.name)) = wagon_type_name
        AND (
        (lastFreight.wagonID IS NULL AND w.startFacility = thisStartFacilityID)
       OR
        (lastFreight.wagonID IS NOT NULL AND lastFreight.lastEndFacility = thisStartFacilityID)
        );
    IF available_wagons < numberWagons THEN
        RAISE_APPLICATION_ERROR(-20013, 'ERRO: Não existem wagons suficientes do tipo "' || thisWagonType || '" na Estação ' || thisStartFacilityID || '. Disponíveis: ' || available_wagons || ', Requeridos: ' || numberWagons || '.');
    END IF;

    -- 9. Inserir o freight
    INSERT INTO Freight(freightID, startFacilityID, endFacilityID, "date")
    VALUES (thisFreightID, thisStartFacilityID, thisEndFacilityID, thisDate);

    -- 10. Associar wagons ao freight
    wagonList := wagonsToAssign(numberWagons, wagon_type_name, thisStartFacilityID, thisDate);
    LOOP
        BEGIN
            FETCH wagonList INTO wagonID;
            EXIT WHEN wagonList%NOTFOUND;
            INSERT INTO Freight_Wagon(freightID, wagonID)
            VALUES (thisFreightID, wagonID);
        END;
    END LOOP;
    CLOSE wagonList;

    COMMIT;

    RETURN 'O Frete ' || thisFreightID || ' foi registado com sucesso.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END registerFreight;

CREATE OR REPLACE FUNCTION wagonsToAssign(numberWagons IN NUMBER, wagonType IN WagonType.name%TYPE, startFacilityID IN Freight.startFacilityID%TYPE, thisDate IN Freight."date"%TYPE)
    RETURN SYS_REFCURSOR
IS
    wagonsList SYS_REFCURSOR;
BEGIN
    OPEN wagonsList FOR
        SELECT w.wagonID
        FROM Wagon w
                 INNER JOIN WagonModel wm ON w.wagonModelID = wm.wagonModelID
                 INNER JOIN WagonType wt ON wm.wagonTypeID = WT.wagonTypeID
                 LEFT JOIN (SELECT fw.wagonID,
                                   f.endFacilityID AS lastEndFacility
                            FROM Freight_Wagon fw
                                     INNER JOIN Freight f ON fw.freightID = f.freightID
                            WHERE f."date" < thisDate
                              AND f."date" = (SELECT MAX(f2."date")
                                              FROM Freight_Wagon fw2
                                                       INNER JOIN Freight f2 ON fw2.freightID = f2.freightID
                                              WHERE fw2.wagonID = fw.wagonID
                                                AND f2."date" < thisDate))
            lastFreight ON w.wagonID = lastFreight.wagonID
        WHERE TRIM(LOWER(wt.name)) = wagonType
          AND (
            (lastFreight.wagonID IS NULL AND w.startFacility = startFacilityID)
            OR
            (lastFreight.wagonID IS NOT NULL AND lastFreight.lastEndFacility = startFacilityID)
          )
        FETCH FIRST numberWagons ROWS ONLY;
    RETURN wagonsList;
END;