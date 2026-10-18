# USEI08 - Search by Geographical Area

## Context

### User Story

> As a planner, I want to query the 2D-tree for all stations in a latitude/longitude range with optional filters, so I can extract relevant stations quickly.

### Description

Operations need to list all stations inside a map rectangle, optionally filtered by type or country. KD-tree pruning should avoid scanning the whole dataset. The search allows querying stations within a geographical bounding box defined by latitude and longitude ranges, with optional filters for country, city status, and main station status.

### Acceptance Criteria

- The region: `lat ∈ [latMin, latMax]`, `lon ∈ [lonMin, lonMax]` (inclusive)
- Optional filters:
  - `isCity` (true | false)
  - `isMainStation` (true | false)
  - `country` (PT | ES | all)
- Returns: 3–5 sample queries; Temporal analysis complexity

---

## Algorithms

### Main Algorithm: Range Search in KD-Tree

The core algorithm implements a recursive range search with pruning optimization:

```java
private void rangeSearch(
    KDNode<TrainStation> node,
    double latMin, double latMax,
    double lonMin, double lonMax,
    String countryFilter,
    Boolean isCityFilter,
    Boolean isMainStationFilter,
    int depth,
    List<TrainStation> output)
```

**Algorithm Steps:**

1. **Base Case**: If node is null, return (O(1))
2. **Check Node**: Verify if current node's coordinate is within range (O(1))
3. **Filter Stations**: For stations at this coordinate, apply filters and add to results (O(m) where m = stations at this coordinate)
4. **Pruning Decision**: Based on split dimension (latitude at even depth, longitude at odd depth):
   - Determine which subtrees might contain results
   - Prune subtrees that cannot contain results
5. **Recursive Calls**: Explore relevant subtrees recursively

**Key Optimization - Pruning:**

- When splitting by latitude: only explore left subtree if `latMin < lat`, right subtree if `latMax >= lat`
- When splitting by longitude: only explore left subtree if `lonMin < lon`, right subtree if `lonMax >= lon`
- This pruning avoids scanning entire subtrees that cannot contain results

### Filter Application Algorithm

```java
private boolean passesFilters(
    TrainStation station,
    String countryFilter,
    Boolean isCityFilter,
    Boolean isMainStationFilter)
```

**Complexity**: O(1) per station

- Each filter check is a constant-time operation (string comparison, boolean comparison)

---

## Temporal Complexity Analysis

### Overall Complexity

The temporal complexity of the geographical search depends on several factors:

#### Variables:

- **n**: Total number of nodes in the KD-tree
- **k**: Number of stations found within the geographical range
- **m**: Average number of stations per coordinate (bucket size)
- **h**: Height of the KD-tree

#### Complexity Breakdown:

1. **Tree Traversal**: O(√n + k) average case for balanced KD-tree

   - The range search visits nodes that intersect with the query rectangle
   - In a balanced KD-tree, the number of nodes visited is approximately O(√n + k)
   - Worst case: O(n) when the query rectangle covers the entire space or tree is unbalanced
2. **Filter Application**: O(k × m)

   - Applied to each station found in the range
   - Each filter check is O(1)
   - If multiple stations share coordinates (m > 1), we process m stations per node
3. **Result Collection**: O(k)

   - Adding k stations to the result list

### Complexity Summary


| Scenario         | Time Complexity | Explanation                                                                                          |
| ---------------- | --------------- | ---------------------------------------------------------------------------------------------------- |
| **Best Case**    | O(log n)        | Query region doesn't intersect tree or very small region with early pruning                          |
| **Average Case** | O(√n + k × m) | Balanced KD-tree with typical query region. Pruning is effective                                     |
| **Worst Case**   | O(n × m)       | Query covers entire space or tree is completely unbalanced. All nodes visited, all stations filtered |

### Detailed Analysis

#### Best Case Scenario

- **Condition**: Query region is very small and doesn't intersect with most of the tree
- **Complexity**: O(log n)
- **Explanation**: Pruning eliminates most subtrees, only a path from root to relevant leaf is traversed

#### Average Case Scenario

- **Condition**: Balanced KD-tree with typical query region
- **Complexity**: O(√n + k × m)
- **Explanation**:
  - Range search in balanced KD-tree visits approximately O(√n) nodes
  - For each node in range, we process m stations (average bucket size)
  - Filter application is O(1) per station
  - Total: O(√n) traversal + O(k × m) filtering

#### Worst Case Scenario

- **Condition**: Query rectangle covers entire dataset or tree is completely unbalanced
- **Complexity**: O(n × m)
- **Explanation**:
  - All n nodes must be visited
  - All stations at each node must be checked against filters
  - No pruning is possible

### Comparison with Linear Search


| Approach                 | Time Complexity         | Space Complexity        |
| ------------------------ | ----------------------- | ----------------------- |
| **Linear Search**        | O(n × m)               | O(1)                    |
| **KD-Tree Range Search** | O(√n + k × m) average | O(n) for tree structure |

**Advantage**: KD-tree provides significant speedup for selective queries (small k relative to n), especially with effective pruning.

---

## 5 Sample Queries

### Query 1: Search in Portugal (Small Region)

**Parameters:**

- Latitude: [36.8, 42.2]
- Longitude: [-9.5, -6.2]
- Country: PT
- isCity: null (all)
- isMainStation: null (all)

**Expected Result**: All Portuguese stations within the specified geographical bounds.

**Complexity**: O(√n + k × m) where k = 169 stations (typical for Portugal)

---

### Query 2: Main Stations in Spain (Medium Region)

**Parameters:**

- Latitude: [36.0, 43.8]
- Longitude: [-9.3, 3.3]
- Country: ES
- isCity: null (all)
- isMainStation: true

**Expected Result**: Only main stations in Spain within the specified region.

**Complexity**: O(√n + k × m) where k ≈ 50-150 main stations (filtered from larger set)

---

### Query 3: City Stations in Iberian Peninsula (Large Region)

**Parameters:**

- Latitude: [36.0, 43.8]
- Longitude: [-9.5, 3.3]
- Country: all
- isCity: true
- isMainStation: null (all)

**Expected Result**: All city stations in Portugal and Spain.

**Complexity**: O(√n + k × m) where k ≈ 100-300 city stations

---

### Query 4: Very Small Region (Lisbon Area)

**Parameters:**

- Latitude: [38.6, 38.8]
- Longitude: [-9.3, -9.0]
- Country: PT
- isCity: null (all)
- isMainStation: null (all)

**Expected Result**: Stations in the Lisbon metropolitan area.

**Complexity**: O(log n + k × m) - approaches best case due to effective pruning, k ≈ 5-20 stations

---

### Query 5: All Stations in Europe (Worst Case - Full Coverage)

**Parameters:**

- Latitude: [-90, 90]
- Longitude: [-180, 180]
- Country: all
- isCity: null (all)
- isMainStation: null (all)

**Expected Result**: All stations in the dataset.

**Complexity**: O(n × m) - worst case, no pruning possible, all nodes visited

---

## Implementation Details

### Key Methods

1. **`searchInRange()`** - Entry point

   - Validates KD-tree (O(1))
   - Initializes result list (O(1))
   - Calls recursive `rangeSearch()` (O(√n + k × m))
2. **`rangeSearch()`** - Recursive search with pruning

   - Determines split dimension based on depth (O(1))
   - Checks if node is in range (O(1))
   - Applies filters to stations at node (O(m))
   - Prunes and recursively explores subtrees (O(√n) average)
3. **`passesFilters()`** - Filter application

   - Country filter: O(1) string comparison
   - isCity filter: O(1) boolean comparison
   - isMainStation filter: O(1) boolean comparison
   - Total: O(1) per station

### Pruning Strategy

The pruning strategy is the key to efficiency:

- **Even depth (latitude split)**:

  - Left subtree explored if `latMin < lat` (might contain lower latitudes)
  - Right subtree explored if `latMax >= lat` (might contain higher latitudes)
- **Odd depth (longitude split)**:

  - Left subtree explored if `lonMin < lon` (might contain lower longitudes)
  - Right subtree explored if `lonMax >= lon` (might contain higher longitudes)

This ensures that only subtrees that could potentially contain results are explored, avoiding unnecessary traversal.

---

## Algorithm Evaluation

### Is the Current Implementation Optimal?

**YES** - The current implementation is the **best algorithm** for range search in KD-tree considering temporal complexity analysis.

#### Why the Current Algorithm is Optimal:

1. **Optimal Complexity**: O(√n + k × m) average case is the **best possible** for balanced KD-tree range search
2. **Correct Pruning**: The pruning strategy correctly avoids exploring subtrees that cannot contain results
3. **Standard Algorithm**: Follows the well-established classical KD-tree range search algorithm
4. **Efficient Filters**: Filter application is O(1) per station and doesn't significantly impact complexity

#### Comparison with Alternatives:


| Algorithm                          | Average Complexity | Best For                         | Recommendation            |
| ---------------------------------- | ------------------ | -------------------------------- | ------------------------- |
| **Current (KD-tree Range Search)** | O(√n + k × m)    | Selective queries, balanced tree | **BEST CHOICE**           |
| Linear Search                      | O(n × m)          | Very small datasets              | Much worse                |
| R-tree                             | O(log n + k)       | Very large datasets (>1M)        | Overhead too high for 64k |
| Grid/Quadtree                      | O(k + grid_size)   | Uniform distributions            | Poor for non-uniform data |
| Hash-based                         | O(n)               | Exact matches only               | Doesn't support ranges    |

#### Potential Micro-Optimizations:

Minor optimizations are possible but provide **marginal gains (<5%)**:

1. **Filter Order**: Apply most selective filter first for early exit
2. **Method Call Optimization**: Cache coordinate object to avoid repeated calls
3. **Early Exit**: Already implemented correctly

**Conclusion**: These micro-optimizations are **optional** and not necessary. The current implementation is already optimal for the use case.

### When Current Algorithm is Ideal:

- KD-tree is balanced (guaranteed by USEI07)
- Queries are selective (k << n)
- Dataset size ~64k stations
- Optional filters applied post-range search

### When Other Approaches Might Be Better:

- KD-tree is unbalanced (but USEI07 ensures balance)
- Queries cover entire space frequently (rare case)
- Dataset is much larger (>1M points) - consider R-tree

---

## Conclusion

The USEI08 implementation provides efficient geographical range search using KD-tree with pruning optimization. The average case complexity of O(√n + k × m) is significantly better than linear search O(n × m) for selective queries, especially when the result set k is much smaller than the total dataset size n.

The optional filters add minimal overhead (O(1) per station) and allow for flexible querying without significant performance impact. The pruning strategy ensures that even for large datasets (~64k stations), queries remain efficient for typical use cases.

**Final Verdict**: The current algorithm is **optimal** for range search in balanced KD-tree. No algorithm changes are needed - the implementation already achieves the best possible complexity for this data structure.
