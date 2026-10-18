CREATE OR REPLACE FUNCTION associateLocomotiveToTrain(
    p_trainID      Train_Locomotive.trainID%TYPE,
    p_locomotiveID Train_Locomotive.locomotiveID%TYPE
)
RETURN VARCHAR2
IS
    v_count      NUMBER;

BEGIN
    ----------------------------------------------------------------
    -- Verificar se o comboio existe
    ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM Train
WHERE trainID = p_trainID;

IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20020, 'Train does not exist.');
END IF;

    ----------------------------------------------------------------
    -- Verificar o estado do comboio (Planed)
    ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM Train
WHERE trainID = p_trainID
  AND status = 'PLANNED';

IF v_count = 0 THEN
    RAISE_APPLICATION_ERROR(
        -20024,
        'Locomotive can only be associated with a planned train.'
    );
END IF;

    ----------------------------------------------------------------
    -- Verificar se a locomotiva existe
    ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM Locomotive
WHERE locomotiveID = p_locomotiveID;

IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20021, 'Locomotive does not exist.');
END IF;

    ----------------------------------------------------------------
    -- Verificar se a locomotiva já está atribuída a outro comboio (status Available)
    ----------------------------------------------------------------
SELECT COUNT(*)
INTO v_count
FROM Locomotive
WHERE locomotiveID = p_locomotiveID
    AND status = 'AVAILABLE';

IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20023, 'Locomotive is already assigned to another train.');
END IF;

    ----------------------------------------------------------------
    -- Associar locomotiva ao comboio
    ----------------------------------------------------------------
INSERT INTO Train_Locomotive (trainID, locomotiveID)
VALUES (p_trainID, p_locomotiveID);

----------------------------------------------------------------
-- Atualizar status da locomotiva para ASSIGNED
----------------------------------------------------------------
UPDATE Locomotive
SET status = 'ASSIGNED'
WHERE locomotiveID = p_locomotiveID;

RETURN 'Locomotive successfully associated with train';


END;
/
