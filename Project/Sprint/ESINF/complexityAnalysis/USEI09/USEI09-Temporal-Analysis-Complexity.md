# USEI09 - Proximity Search (Nearest-N with Filters)

## Context

### User Story

> As an analyst, I want to find the nearest N stations to a given (lat, lon) using the 2D-tree, with optional filters, so I can get accurate nearby results efficiently.

### Description

The system should return the nearest N stations based on Haversine distance. Optional filters can be applied for and time zone
### Acceptance Criteria

- The region: `lat ∈ [latMin, latMax]`, `lon ∈ [lonMin, lonMax]` (inclusive)
- Optional filters:
    - `time zone` 
- Returns: A list with n nearest neighbours; Temporal analysis complexity.

---

## Algorithms

### Main Algorithm: N-Nearest Neighbors on KD-Tree

The core algorithm implements a recursive range search with pruning optimization:

```java
public static List<StationDistance> findNearestN(
        KDTree<TrainStation> kdTree,
        double targetLat,
        double targetLon,
        int n,
        String timeZoneFilter);
```

**Algorithm Steps:**

1. **Base Case**: If node is null, return (O(1))
2. **Compute Distance**: Calculate Haversine distance between target and current node (O(1))
3. **Filter by TimeZone**: For stations at this coordinate, apply filters and add to results (O(m) where m = stations at this coordinate)
4. **Pruning Decision**: Based on split dimension (latitude at even depth, longitude at odd depth):
    - Determine which subtree may contain closer neighbors based on the splitting axis
    - Skip subtrees that cannot contain closer stations, reducing unnecessary exploration.
5. **Recursive Calls**: Recursively explore subtree closer to target first

**Key Optimization - Pruning:**

- Only visit a subtree if it can contain stations closer than the current farthest in the heap.
- This pruning avoids scanning subtrees that cannot improve the result, significantly reducing nodes explored.

### Filter Application Algorithm

```java
private boolean passesFilters(
    TrainStation station,
    String countryFilter,
    Boolean isCityFilter,
    Boolean isMainStationFilter);
```

**Complexity**: O(1) per station

- Each filter check is a constant-time operation (string comparison, boolean comparison)

---

## Temporal Complexity Analysis

### Overall Complexity

The temporal complexity of the geographical search depends on several factors:

#### Variables:

- **n**: Total number of nodes in the KD-tree
- **m**: Average number of stations per node (size of node.getInfo() list)
- **N**: Number of nearest stations to find

#### Complexity Breakdown:

1. **Tree Traversal**: O(√n + k) average case for balanced KD-tree

    - Visits nodes recursively based on KD-tree split axis
    - In a balanced KD-tree, the number of nodes visited is approximately O(√n + k)
    - Worst case: O(n) when the query rectangle covers the entire space or tree is unbalanced

2. **Processing Stations per Node**: O(m × log N)
    - For each station in the node’s info list (m stations):
      - Calculate Haversine distance: O(1)
      - Apply time zone filter: O(1)
      - Update max-heap (size N): O(log N)

3. **Result Collection**: O(N × log N)
    - Converting the max-heap to a sorted list of N nearest stations

### Complexity Summary


| Scenario         | Time Complexity  | Explanation                                                     |
| ---------------- |------------------|-----------------------------------------------------------------|
| **Best Case**    | O(log N)         | Target coordinate extremely localized; very few nodes visited         |
| **Worst Case**   | O(n × m × log N) | All nodes visited; all stations processed; pruning ineffective  |

### Detailed Analysis

#### Best Case Scenario

- **Condition**: Target coordinate is very localized; all N neighbors are found in a single node or a very small set of nodes.
- **Complexity**: O(log N)
- **Explanation**:
    - Each station processed with O(1) Haversine distance + O(log N) heap update

#### Worst Case Scenario
KD-tree is completely unbalanced, or pruning is ineffective (all no
- **Condition**: des must be visited).
- **Complexity**: O(n × m × log N)
- **Explanation**:
    - All n nodes must be visited
    - Each node has m stations that must be processed
    - Updating the max-heap of size N requires O(log N) per station

## Implementation Details

### Key Methods

1. **`findNearestN()`** - Entry point

    - Validates KD-tree (O(1))
    - Initializes max-heap to keep track of N closest stations (O(1))
    - Calls recursive `searchNearestN()`(O(n) worst case, O(log n) best case for balanced tree)

2. **`searchNearestN()`** - Recursive search with pruning

    - Determines split dimension based on depth (O(1))
    - Calculates Haversine distance to target coordinate (O(1) per station)
    - Applies time zone filter (O(1) per station)
    - Decides which subtree(s) to explore based on pruning logic
   
3. **`StationDistance()`** - Helper class to store station and its distance

    - Used in the max-heap to track N closest stations
    - Comparison by distance is O(1)

4. **`SearchStatistics()`** - Tracks complexity metrics

    - Counts nodes explored and skipped 
    - Records execution time in milliseconds

### Pruning Strategy

The pruning strategy ensures efficiency by exploring only subtrees that may contain closer stations than the current farthest in the heap:

- **Split by depth:**:

    - Even depth → latitude axis
    - Odd depth → longitude axis
  
- **Subtree selection:**:

    - Explore the subtree closer to the target coordinate first
    - Only explore the farther subtree if it might contain a station closer than the farthest in the max-heap

This avoids unnecessary traversal of subtrees that cannot improve the N nearest stations result.

---

## Conclusion

The USEI09 implementation provides an efficient nearest-N search using a KD-tree with pruning optimization.

**Filter overhead**: O(1) per station, since each station is checked only once against the optional timezone filter
**Pruning strategy**: Avoids exploring subtrees that cannot contain closer neighbors, improving efficiency in practice for typical nearest-N searches.
**Final Verdict**: The current algorithm is optimal for nearest-N search in a balanced KD-tree. No changes are needed; the implementation achieves the best possible temporal complexity for this data structure.
