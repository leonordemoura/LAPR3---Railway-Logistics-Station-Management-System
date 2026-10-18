# USEI06 - Índices AVL para estações de trem

## Context
### User Story
As a planner, I want a set of BST/AVL trees that lets me quickly search by:  
– latitude  
– longitude  
– time zone group and country

So I can efficiently retrieve all stations of a time zone group by ascending order of country or in a window of time zone groups, such as ['CET', 'WET/GMT'].

### Acceptance Criteria
- Validate: non-empty station name, country, and time zone group.
- Latitude ∈ [-90, 90], longitude ∈ [-180, 180].
- Reject invalid rows.
- If multiple stations share the same coordinates (e.g., Lisbon Santa Apolónia and Lisbon Oriente), the tree must preserve all stations sorted by name in ascending order.

---

## Implementation Overview

- **Three AVL Trees** are used:
    - `latitudeIndex`: AVL<Double> for latitude → inner AVL of stations.
    - `longitudeIndex`: AVL<Double> for longitude → inner AVL of stations.
    - `timeZoneCountryIndex`: AVL<String> for timeZoneGroup → inner AVL<String> for country → inner AVL<TrainStation> for station.

- **Mixed Comparator (`mixedComparator`)**:
    - Used in latitude and longitude AVLs where the hierarchy mixes numeric values (`Double`) and station names (`String`).
    - Rules:
        - Double vs Double → compare numerically.
        - String vs String → compare lexicographically.
        - Double vs String → Double < String (ensures consistent ordering across mixed types).
    - Implemented using `Comparator<Object>`.
    - **Why `Object`?**
        - Allows flexibility to store mixed types in the hierarchy array (`Object[] hierarchy`) for `insertAVL`.
        - Each level of the hierarchy may have a different type: e.g., first level Double (latitude), second level String (station name).

- **Hierarchical insert**:
    - `insertAVL(Object[] hierarchy, TrainStation station)` inserts a station through the AVL hierarchy.
    - Uses recursion (`insertRec`) to traverse each level.
    - `insertHelper` is called for inner nodes.
    - **Example hierarchies**:
        1. Latitude → TrainStation
            - Level 1: AVL<Double> latitude
            - Level 2: AVL<TrainStation>
        2. TimeZoneGroup → Country → TrainStation
            - Level 1: AVL<String> timeZoneGroup
            - Level 2: AVL<String> country
            - Level 3: AVL<TrainStation>

---

## Complexity Analysis

### Insert

- **Per level of hierarchy**: O(log n) to descend from root to leaf in AVL.
- **h levels of hierarchy**: total O(h * log n).
- `insertHelper` maintains same complexity per level.
- **Example**:
    - Latitude → TrainStation → h = 2 → O(2 * log n) ≈ O(log n) (constants ignored)
    - TimeZoneGroup → Country → TrainStation → h = 3 → O(3 * log n) ≈ O(log n)

### Find Node (`findNode`)

- AVL is balanced: O(log n) to descend from root to target node.

### Search Exact (`searchExact`)

- Calls `findNode`: O(log n)
- If innerNode exists, collects all stations recursively (`collectStations`): O(m), where m = number of stations in sub-AVL.
- **Total**: O(log n + m)

### Collect Stations (`collectStations`)

- Traverses inner AVL recursively.
- Visits each node exactly once.
- **Complexity**: O(m)

### Rotations (left, right, double)

- Operate on a single node with constant children references.
- Update heights for parent and child: O(1)
- **Total**: O(1) per rotation.

### InOrder Traversal

- Visits all n nodes exactly once.
- **Complexity**: O(n)

### Contains

- Calls `findNode`: O(log n)

---

## Mixed Comparator Complexity

- Comparing two elements: O(1)
- Each comparison involves checking type and comparing Double or String values.
- **Used in**: insert, search, and tree balancing operations.

---

## Summary

- The hierarchical AVL approach ensures:
    - Efficient retrieval by latitude, longitude, or time zone/country.
    - Inner AVLs preserve sorting of stations by name.
    - MixedComparator allows mixed-type hierarchies (numeric + string) in a single AVL.
- **Total insert complexity for a station**: O(h * log n)
- **Total search complexity**: O(log n + m)  

