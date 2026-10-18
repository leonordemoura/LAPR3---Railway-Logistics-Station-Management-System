SELECT
    lo.name AS owner_name,
    l.lineID,
    ls.segmentID,
    ls.orderLine
FROM LineOwner lo
         INNER JOIN Line l ON lo.lineOwnerID = l.lineOwnerID
         INNER JOIN LineSegment ls ON l.lineID = ls.lineID
WHERE lo.lineOwnerID = 1
ORDER BY l.lineID, ls.orderLine;