DECLARE
    wagons SYS_REFCURSOR;
    v_wagonID Wagon.wagonID%TYPE;
    v_count NUMBER := 0;
    v_trainCount NUMBER;
    no_trains_in_period EXCEPTION;

BEGIN

    SELECT COUNT(*) INTO v_trainCount
    FROM Train t
    WHERE t.startDate BETWEEN DATE '2025-10-03' AND DATE '2025-10-07';

    IF v_trainCount = 0 THEN
        RAISE no_trains_in_period;
    END IF;

    DBMS_OUTPUT.PUT_LINE('USBD27 - Grain Wagons Used in Period');
    DBMS_OUTPUT.PUT_LINE('Period: 2025-10-03 to 2025-10-07');
    DBMS_OUTPUT.PUT_LINE('----------------------------------------');

    wagons := getGrainWagonsUsedInAllTrains(
            DATE '2025-10-03',
            DATE '2025-10-07'
              );

    LOOP
        FETCH wagons INTO v_wagonID;
        EXIT WHEN wagons%NOTFOUND;

        v_count := v_count + 1;
        DBMS_OUTPUT.PUT_LINE('Wagon: ' || v_wagonID);
    END LOOP;

    CLOSE wagons;

    DBMS_OUTPUT.PUT_LINE('----------------------------------------');
    DBMS_OUTPUT.PUT_LINE('Total: ' || v_count || ' grain wagons');

EXCEPTION
    WHEN no_trains_in_period THEN
        RAISE_APPLICATION_ERROR(-20001, 'No trains exist in the selected period.');
END;
