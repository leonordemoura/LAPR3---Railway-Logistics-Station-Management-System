DECLARE
segments SYS_REFCURSOR;
    segmentID LineSegment.segmentID%TYPE;
    lineID    LineSegment.lineID%TYPE;
    orderLine LineSegment.orderLine%TYPE;
    train_doesnt_exist EXCEPTION;
BEGIN
    IF NOT train_exists(5421) THEN
        RAISE train_doesnt_exist;
END IF;

    segments := getPlannedRouteLineSegments(5421);

    DBMS_OUTPUT.PUT_LINE('=== Line Segments for Train 5421 ===');

    LOOP
FETCH segments INTO segmentID, lineID, orderLine;
        EXIT WHEN segments%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Segment: ' || segmentID ||
                           ' | Line: ' || lineID ||
                           ' | Order: ' || orderLine);

END LOOP;

CLOSE segments;

EXCEPTION
    WHEN train_doesnt_exist THEN
        raise_application_error(-20001, 'Esse comboio não existe!');
END;