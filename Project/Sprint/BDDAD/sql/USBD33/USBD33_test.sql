
------------------------------------------------------------
------------------------------------------------------------
-- TESTE POSITIVO 1: Adicionar locomotiva (válido)
------------------------------------------------------------
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TESTE POSITIVO: LOCOMOTIVA ---');

INSERT INTO Train_Locomotive (trainID, locomotiveID)
VALUES (1, 1);

DBMS_OUTPUT.PUT_LINE('SUCCESS: Locomotive added successfully');
END;
/

------------------------------------------------------------
-- TESTE POSITIVO 2: Adicionar freight (válido)
------------------------------------------------------------
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TESTE POSITIVO: FREIGHT ---');

INSERT INTO Train_Freight (trainID, freightID)
VALUES (1, 1);

DBMS_OUTPUT.PUT_LINE('SUCCESS: Freight added successfully');
END;
/
------------------------------------------------------------
-- TESTE NEGATIVO 1: Locomotiva excede maxLenght
------------------------------------------------------------
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TESTE NEGATIVO: LOCOMOTIVA ---');

INSERT INTO Locomotive (locomotiveID, operatorID, locomotiveModelID, makeId,
                        startFacilityID, serviceYear, operationalSpeed, status)
VALUES (2, 1, 10, 1, 1, 2016, 160, 'AVAILABLE');

INSERT INTO Train_Locomotive (trainID, locomotiveID)
VALUES (1, 2);

DBMS_OUTPUT.PUT_LINE('ERROR: This should not be allowed');
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
/
------------------------------------------------------------
-- TESTE NEGATIVO 2: Freight excede maxLenght
------------------------------------------------------------
BEGIN
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- TESTE NEGATIVO: FREIGHT ---');

    -- Novo wagon
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility,
                   serviceYear, payloadTons, status)
VALUES (2, 1, 20, 1, 2019, 50, 'AVAILABLE');

-- Novo freight
INSERT INTO Freight (freightID, startFacilityID, endFacilityID, "date")
VALUES (2, 1, 2, SYSTIMESTAMP);

INSERT INTO Freight_Wagon (freightID, wagonID)
VALUES (2, 2);

INSERT INTO Train_Freight (trainID, freightID)
VALUES (1, 2);

DBMS_OUTPUT.PUT_LINE('ERROR: This should not be allowed');
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Expected error: ' || SQLERRM);
END;
/
