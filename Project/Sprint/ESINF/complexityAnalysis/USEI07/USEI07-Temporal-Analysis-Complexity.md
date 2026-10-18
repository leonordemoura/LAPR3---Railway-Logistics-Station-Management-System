# USEI07 - Build a balanced 2D-Tree on Latitude/Longitude

## Context
### User Story
> As a data engineer, I want to build a balanced 2D-tree using (lat,lon) so that range and proximity searches run efficiently and deterministically.

### Description:
We need a spatial index over 64k (approx.) European train stations  to enable fast geographic queries. A KD-tree (k=2) on (latitude, longitude) supports range and nearest-neighbour operations much faster than scanning  all stations.

### Acceptance Criteria:
- The simplest way to ensure a balanced 2D-tree is to perform a bulk build; for this, develop a balanced build strategy that will use the BST/AVL of latitude and longitude created in USEI01 to efficiently construct the 2D-tree. 
- If multiple stations share the same coordinates, such as 38.71387, -9.122271, which are linked to Lisbon Santa Apolónia and Lisbon Oriente, the 2D-tree must preserve all these stations sorted by name in ascending order.

## Algorithms

### Main Algorithm: Balanced KD-Tree Construction

The core algorithm constructs a balanced 2D-tree by leveraging pre-sorted coordinate data from AVL trees:
```java
public KDTree<TrainStation> buildBalancedTree(List<TrainStation> stations);
```

**Algorithm Steps:**

1. **Group Stations by Coordinates**: Create a map of coordinates to station lists, sorting stations by name at each coordinate (O(n log n))
2. **Extract Sorted Values**: Retrieve sorted latitude and longitude values from AVL indices (O(n))
3. **Build Coordinate Mappings**: Create bidirectional mappings between coordinates and their lat/lon values (O(n))
4. **Recursive Tree Construction**: Build tree using median-split partitioning with alternating split dimensions (O(n log n))

**Key Features:**

- Alternates split dimension by depth (even = latitude, odd = longitude)
- Uses median selection to ensure balance
- Preserves all stations at shared coordinates, sorted by name
- Leverages pre-sorted AVL data to avoid repeated sorting

### Recursive Build Algorithm
```java
private KDNode<TrainStation> buildBalancedFromAVL(
    List<Double> sortedLats,
    List<Double> sortedLons,
    Map<Double, Set<Coordinate>> latToCoords,
    Map<Double, Set<Coordinate>> lonToCoords,
    Map<Coordinate, List<TrainStation>> stationsByCoordinates,
    Set<Coordinate> remainingCoordinates,
    int depth);
```

**Algorithm Steps:**

1. **Base Case**: If no remaining coordinates, return null (O(1))
2. **Determine Split Axis**: Even depth = latitude, odd depth = longitude (O(1))
3. **Filter Active Values**: Identify coordinates still to be processed on current axis (O(c) where c = remaining coordinates)
4. **Find Median**: Select median value on split axis (O(1) with pre-sorted data)
5. **Select Coordinate**: Among coordinates at median value, choose median on other axis (O(c log c) for sorting)
6. **Partition Remaining**: Split remaining coordinates into left and right subtrees (O(c))
7. **Build Subtree Lists**: Create filtered sorted lists for recursive calls (O(c))
8. **Recursive Calls**: Build left and right subtrees (T(c/2) each)

**Key Optimization - Median Split:**

- Uses median value from pre-sorted AVL data (O(1) selection)
- Ensures balanced partitioning at each level
- When multiple coordinates share the median value, uses secondary axis to break ties
- This guarantees tree height of O(log c) where c is the number of unique coordinates

### Station Grouping Algorithm
```java
private Map<Coordinate, List<TrainStation>> groupStationsByCoordinates(
    List<TrainStation> stations);
```

**Complexity**: O(n log n)

- Groups n stations by coordinate: O(n)
- Sorts stations at each coordinate by name: O(m log m) per coordinate, O(n log n) total
- Ensures deterministic ordering when multiple stations share coordinates

---

## Temporal Complexity Analysis

### Overall Complexity

The temporal complexity of building the balanced KD-tree depends on several factors:

#### Variables:

- **n**: Total number of train stations
- **c**: Number of unique coordinates
- **m**: Average number of stations per coordinate (bucket size, m = n/c)

#### Complexity Breakdown:

1. **Grouping Stations by Coordinates**: O(n log n)
    - Iterate through n stations: O(n)
    - For each coordinate's station list, sort by name: O(m log m) per coordinate
    - Total across all c coordinates: O(n log n) in aggregate

2. **Extracting Sorted Values from AVL**: O(c)
    - In-order traversal of latitude AVL: O(c)
    - In-order traversal of longitude AVL: O(c)
    - Total: O(c)

3. **Building Coordinate Mappings**: O(c)
    - Iterate through c coordinates to create lat/lon to coordinate maps: O(c)
    - Hash map insertions are O(1) average

4. **Recursive Tree Construction**: O(c log c)
    - Each level processes all c coordinates once
    - Tree has O(log c) levels (balanced tree)
    - Per level work: O(c) for filtering, partitioning, and list building
    - Total: O(c log c)

### Complexity Summary

| Scenario | Time Complexity | Explanation |
|----------|----------------|-------------|
| **Best Case** | O(n log n) | Single station per coordinate (c = n), optimal case |
| **Average Case** | O(n log n) | Dominated by initial station grouping and sorting |
| **Worst Case** | O(n log n) | All complexity factors resolve to O(n log n) |

### Detailed Analysis

#### Best Case Scenario

- **Condition**: Each coordinate has exactly one station (c = n, m = 1)
- **Complexity**: O(n log n)
- **Explanation**:
    - Grouping: O(n) (no sorting needed per coordinate)
    - AVL extraction: O(n)
    - Coordinate mappings: O(n)
    - Recursive construction: O(n log n)
    - **Dominant term**: O(n log n)

#### Average Case Scenario

- **Condition**: Multiple stations share some coordinates (m > 1, c < n)
- **Complexity**: O(n log n)
- **Explanation**:
    - Grouping: O(n log n) - sorting m stations at each of c coordinates
    - AVL extraction: O(c) where c ≤ n
    - Coordinate mappings: O(c)
    - Recursive construction: O(c log c) where c ≤ n
    - **Dominant term**: O(n log n) from grouping phase

#### Worst Case Scenario

- **Condition**: Many stations share the same coordinates (large m values)
- **Complexity**: O(n log n)
- **Explanation**:
    - Grouping: O(n log n) - even with large buckets, total sorting across all coordinates is O(n log n)
    - Other phases remain O(c) or O(c log c)
    - **Dominant term**: O(n log n)

### Comparison with Alternatives

| Approach                  | Time Complexity       | Space Complexity | Balance Guarantee |
|---------------------------|-----------------------|------------------|-------------------|
| **AVL-based Bulk Build**  | O(n log n)            | O(n)             | **YES**           |
| Incremental Insertion     | O(n log n) average    | O(n)             | **NO**            |
| Sort-based Build (no AVL) | O(n log n)            | O(n)             | **YES**           |
| Random Insertion          | O(n log n) expected   | O(n)             | **NO**            |

**Advantages of Current Approach:**

1. **Guaranteed Balance**: Median-split ensures balanced tree
2. **Leverages Existing AVL**: Reuses sorted data from USEI01
3. **Deterministic**: Same input produces same tree structure
4. **Efficient**: O(n log n) is optimal for comparison-based construction
5. **Handles Duplicates**: Preserves all stations at shared coordinates

---

## Implementation Details

### Key Methods

1. **`buildBalancedTree()`** - Entry point
    - Groups stations by coordinates: O(n log n)
    - Extracts sorted coordinates from AVL: O(c)
    - Creates coordinate mappings: O(c)
    - Initiates recursive construction: O(c log c)
    - **Total**: O(n log n)

2. **`groupStationsByCoordinates()`** - Grouping and sorting
    - Creates coordinate → station list map: O(n)
    - Sorts stations by name at each coordinate: O(n log n) aggregate
    - **Total**: O(n log n)

3. **`buildBalancedFromAVL()`** - Recursive construction
    - Filters active coordinates: O(c) per level
    - Finds median: O(1) with pre-sorted data
    - Partitions coordinates: O(c) per level
    - Recursive calls: T(c/2) for each subtree
    - **Recurrence**: T(c) = 2T(c/2) + O(c) → **T(c) = O(c log c)**

4. **`buildByLatitude()` / `buildByLongitude()`** - Axis-specific splitting
    - Filters and finds median on split axis: O(c)
    - Selects coordinate using secondary axis: O(c log c) for sorting remaining at median
    - Partitions for subtrees: O(c)
    - **Total per call**: O(c log c)

### Balance Guarantee

The median-split strategy ensures a balanced tree:

- **Height**: O(log c) where c is the number of unique coordinates
- **Balance Factor**: At each level, coordinates are split as evenly as possible
- **Worst-Case Search**: O(log c) due to guaranteed balance

This is critical for efficient range searches (USEI08) and nearest-N searches (USEI09).

### Median Selection Strategy

The algorithm handles tie-breaking when multiple coordinates share the median value:

- **Split by latitude (even depth)**:
    - Find median latitude from active coordinates
    - Among all coordinates at median latitude, sort by longitude
    - Select median longitude as the splitting coordinate

- **Split by longitude (odd depth)**:
    - Find median longitude from active coordinates
    - Among all coordinates at median longitude, sort by latitude
    - Select median latitude as the splitting coordinate

This ensures deterministic splits and maintains balance even with coordinate clusters.

---

## Algorithm Evaluation

The current implementation achieves optimal time complexity for building a balanced KD-tree.

#### Why the Current Algorithm is Optimal:

1. **Optimal Time Complexity**: O(n log n) is the best possible for comparison-based construction
2. **Guaranteed Balance**: Median-split ensures O(log c) height
3. **Efficient Use of Pre-sorted Data**: Leverages AVL trees from USEI01
4. **Handles Duplicates Correctly**: Preserves and sorts stations at shared coordinates
5. **Deterministic**: Produces consistent tree structure

#### Theoretical Lower Bound:

- Building any comparison-based spatial index requires Ω(n log n) time
- Cannot do better than O(n log n) without additional assumptions
- Current implementation meets this lower bound

#### Practical Considerations:

- Pre-sorted AVL data eliminates repeated sorting cost
- Median selection is O(1) with sorted input
- Recursion depth is O(log c), not O(n)
- Memory locality is good due to batch processing

#### Comparison with Alternative Approaches:

| Algorithm | Time Complexity | Balance | Determinism | Best For |
|-----------|----------------|---------|-------------|----------|
| **Current (AVL-based)** | O(n log n) | Guaranteed | Yes | **BEST CHOICE** |
| Sort each level | O(n log² n) | Guaranteed | Yes | Slower, no advantage |
| Incremental insert | O(n log n) avg | Not guaranteed | No | Online construction |
| Randomized | O(n log n) exp | Probabilistic | No | Simple implementation |

---

## Conclusion

The USEI07 implementation provides an efficient and deterministic method for constructing a balanced KD-tree. The O(n log n) time complexity is optimal for comparison-based construction, and the guaranteed balance ensures that subsequent range searches (USEI08) and nearest-N searches (USEI09) operate efficiently with O(√n) average complexity.

The algorithm correctly handles the requirement to preserve multiple stations at shared coordinates, sorted by name, while maintaining overall tree balance. The use of pre-sorted AVL trees from USEI01 provides an elegant solution that avoids redundant sorting operations.

**Final Verdict**: The current algorithm is **optimal** for balanced KD-tree construction. No algorithm changes are needed - the implementation achieves the best possible time complexity of O(n log n) with guaranteed balance and correct handling of duplicate coordinates.