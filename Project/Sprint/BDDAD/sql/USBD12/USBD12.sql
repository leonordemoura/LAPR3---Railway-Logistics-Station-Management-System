SELECT
    w.wagonID,
    wt.name AS wagon_type,
    g.dimension AS gauge_dimension,
    o.name AS operator_name
FROM Wagon w
         INNER JOIN Operator o ON w.operatorID = o.operatorID
         INNER JOIN WagonModel wm ON w.wagonModelID = wm.wagonModelID
         INNER JOIN WagonType wt ON wm.wagonTypeID = wt.wagonTypeID
         INNER JOIN Gauge_WagonModel gwm ON wm.wagonModelID = gwm.wagonModelID
         INNER JOIN Gauge g ON gwm.gaugeID = g.gaugeID
WHERE wt.name = 'Flatcar'      -- options ('Boxcar', 'Tank Car', 'Flatcar', 'Hopper car', 'Refrigerated car')
  AND g.dimension = 1668       -- gauge (1435, 1668)
  AND o.operatorID = 1
ORDER BY w.wagonID;