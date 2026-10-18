DECLARE
    endpoints SYS_REFCURSOR;
    name Facility.name%TYPE;
    facilityID Facility.facilityID%TYPE;
    train_doesnt_exist EXCEPTION;

BEGIN
    begin
        if not train_exists(5421) then
            RAISE train_doesnt_exist;
        else
            endpoints := countEndPoints(5421);
            DBMS_OUTPUT.PUT_LINE('The end points where the train does not stop are: ');
            LOOP
                FETCH endpoints INTO name, facilityID;
                EXIT WHEN endpoints%NOTFOUND;
                DBMS_OUTPUT.PUT_LINE(facilityID || ' - ' || name);
            end loop;
            CLOSE endpoints;
        end if;
    end;

EXCEPTION
    WHEN train_doesnt_exist then
        RAISE_APPLICATION_ERROR(-20001, 'The train you selected does not exist.');
end;

