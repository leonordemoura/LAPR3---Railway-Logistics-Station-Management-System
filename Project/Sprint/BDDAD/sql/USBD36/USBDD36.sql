CREATE OR REPLACE FUNCTION registerNewBuilding(
    thisBuildingID   IN Building.buildingID%TYPE,
    thisFacilityID        IN Building.facilityID%TYPE,
    thisType       IN Building.buildingTypeID%TYPE
)
    RETURN VARCHAR2
IS
    building_exists NUMBER;
    facility_exists NUMBER;
    building_type_exists NUMBER;
BEGIN
    -- 1. Validar que campos obrigatórios não são nulos
    IF thisFacilityID IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001, 'ERROR: O ID da Estação não pode ser nulo.');
    END if;

    IF thisType IS NULL THEN
        RAISE_APPLICATION_ERROR(-20003, 'ERROR: O ID do tipo de Edifício não pode ser nulo.');
    END if;

    IF thisBuildingID IS NULL THEN
        RAISE_APPLICATION_ERROR(-20006, 'ERROR: O ID do Edifício não pode ser nulo.');
    END if;

    -- 2. Validar se a facility existe
    SELECT COUNT(*) INTO facility_exists
    FROM Facility WHERE facilityID = thisFacilityID;

    IF facility_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'ERROR: O ID da Estação ' || thisFacilityID || ' é inválido.');
    END IF;

    -- 3. Validar se o buildingTypeID é válido
    SELECT COUNT(*) INTO building_type_exists
    FROM BuildingType WHERE buildingTypeID = thisType;
    IF building_type_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20004, 'ERROR: O ID do tipo de Edifício ' || thisType || ' é inválido.');
    END IF;

    -- 4. Verificar se o buildingID já existe
    SELECT COUNT(*) INTO building_exists
    FROM Building WHERE buildingID = thisBuildingID;

    IF building_exists > 0 THEN
        RAISE_APPLICATION_ERROR(-20005, 'ERROR: O ID do Edifício ' || thisBuildingID || ' já existe.');
    END IF;

    -- 5. Inserir o building
    INSERT INTO Building (buildingID, facilityID, buildingTypeID)
    VALUES (thisBuildingID, thisFacilityID, thisType);
    COMMIT;

    RETURN 'O Edifício ' || thisBuildingID || ' foi registado com sucesso.';

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END registerNewBuilding;