CREATE OR REPLACE TRIGGER trg_train_maxlength_loco
BEFORE INSERT OR UPDATE ON Train_Locomotive
                            FOR EACH ROW
DECLARE
v_train_max   NUMBER;
    v_loco_sum    NUMBER;
    v_freight_sum NUMBER;
    v_new_len     NUMBER;
BEGIN
    -- Max length do comboio
SELECT maxLenght
INTO v_train_max
FROM Train
WHERE trainID = :NEW.trainID;

-- Comprimento da nova locomotiva
SELECT v.length
INTO v_new_len
FROM Locomotive l
         JOIN LocomotiveModel lm ON l.locomotiveModelID = lm.locomotiveModelID
         JOIN Vehicle v ON lm.vehicleID = v.vehicleID
WHERE l.locomotiveID = :NEW.locomotiveID;

-- Soma das locomotivas já no comboio (excluindo a antiga em UPDATE)
SELECT NVL(SUM(v.length), 0)
INTO v_loco_sum
FROM Train_Locomotive tl
         JOIN Locomotive l ON tl.locomotiveID = l.locomotiveID
         JOIN LocomotiveModel lm ON l.locomotiveModelID = lm.locomotiveModelID
         JOIN Vehicle v ON lm.vehicleID = v.vehicleID
WHERE tl.trainID = :NEW.trainID
  AND (:OLD.locomotiveID IS NULL OR tl.locomotiveID <> :OLD.locomotiveID);

-- Soma dos vagões já associados
SELECT NVL(SUM(v.length), 0)
INTO v_freight_sum
FROM Train_Freight tf
         JOIN Freight_Wagon fw ON tf.freightID = fw.freightID
         JOIN Wagon w ON fw.wagonID = w.wagonID
         JOIN WagonModel wm ON w.wagonModelID = wm.wagonModelID
         JOIN Vehicle v ON wm.vehicleID = v.vehicleID
WHERE tf.trainID = :NEW.trainID;

-- Validação final
IF v_loco_sum + v_freight_sum + v_new_len > v_train_max THEN
        RAISE_APPLICATION_ERROR(-20050, 'Train maximum length exceeded (locomotive)');
END IF;
END;
/


CREATE OR REPLACE TRIGGER trg_train_maxlength_freight
BEFORE INSERT OR UPDATE ON Train_Freight
                            FOR EACH ROW
DECLARE
v_train_max    NUMBER;
    v_loco_sum     NUMBER;
    v_freight_sum  NUMBER;
    v_new_sum      NUMBER;
BEGIN
    -- Max length do comboio
SELECT maxLenght
INTO v_train_max
FROM Train
WHERE trainID = :NEW.trainID;

-- Comprimento do freight a adicionar
SELECT NVL(SUM(v.length), 0)
INTO v_new_sum
FROM Freight_Wagon fw
         JOIN Wagon w ON fw.wagonID = w.wagonID
         JOIN WagonModel wm ON w.wagonModelID = wm.wagonModelID
         JOIN Vehicle v ON wm.vehicleID = v.vehicleID
WHERE fw.freightID = :NEW.freightID;

-- Soma locomotivas
SELECT NVL(SUM(v.length), 0)
INTO v_loco_sum
FROM Train_Locomotive tl
         JOIN Locomotive l ON tl.locomotiveID = l.locomotiveID
         JOIN LocomotiveModel lm ON l.locomotiveModelID = lm.locomotiveModelID
         JOIN Vehicle v ON lm.vehicleID = v.vehicleID
WHERE tl.trainID = :NEW.trainID;

-- Soma vagões já existentes
SELECT NVL(SUM(v.length), 0)
INTO v_freight_sum
FROM Train_Freight tf
         JOIN Freight_Wagon fw ON tf.freightID = fw.freightID
         JOIN Wagon w ON fw.wagonID = w.wagonID
         JOIN WagonModel wm ON w.wagonModelID = wm.wagonModelID
         JOIN Vehicle v ON wm.vehicleID = v.vehicleID
WHERE tf.trainID = :NEW.trainID
  AND (:OLD.freightID IS NULL OR tf.freightID <> :OLD.freightID);

IF v_loco_sum + v_freight_sum + v_new_sum > v_train_max THEN
        RAISE_APPLICATION_ERROR(
            -20051,
            'Train maximum length exceeded (freight)'
        );
END IF;
END;
/
