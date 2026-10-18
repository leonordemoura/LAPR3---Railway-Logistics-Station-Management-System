## USEI01 — Wagons Unloading (Inventory Replenishment)

### User Story
As a terminal operator I want unloading operations of wagons to automatically store inventory using FEFO and/or FIFO logic So that I can ensure the correct dispatch order and minimize product spoilage

### Context
When wagons arrive at the terminal and are unloaded into warehouses, the sequence in which products are stored and dispatched is critical for:
- Inventory accuracy
- Traceability
- Operational efficiency

### Storage Principles
1. Perishable Goods (FEFO - First-Expired-First-Out)
   - Products with expiry dates must be dispatched based on expiration date
   - Goods closest to expiration are dispatched first
   - Minimizes spoilage and waste
2. Non-Perishable Goods (FIFO - First-In-First-Out)
   - Products without expiry dates follow reception order
   - Oldest stock is dispatched first
   - Prevents inventory stagnation

### Key Concepts
- **SKU** (Stock Keeping Unit): Unique identifier for each product type
- **Box**: Physical container with a unique boxId that stores products of a single SKU
- **Location Code**: Warehouse/Aisle/Bay structure for storage organization

## Implementation Details
### 1. FEFO/FIFO Comparator

![image](svg/fifo_fefo.svg)

### Summary — compareBoxesForBayOrder
This method compares two boxes (Box a and Box b) to decide which one should come first when sorting them in FEFO/FIFO order.
It follows these rules:
- Expiry date (FEFO rule) —
   - Boxes with an earlier expiry date come first.
   - Boxes without an expiry date (null) come last.

- Received date (FIFO rule) —
   - If expiry dates are the same (or both null), the box that was received earlier comes first.

- Box ID —
   - If both expiry and received dates are identical, it compares the box IDs just to keep a consistent order.

#### Return values:
- -1 - a comes before b
- 0 - a and b are equal in order
- 1 - a comes after b

### 2. Insert box ordered

![image](svg/boxOrdered.svg)

### Summary — insertBoxOrdered
The method insertBoxOrdered inserts a new Box into a list (boxesInBay) in the correct order, based on the rules defined in compareBoxesForBayOrder() (FEFO/FIFO order).

#### How it works
1. Start at the beginning of the list (idx = 0).
2. Loop through each existing box:

![image](svg/loop.svg)

   - cmp <= 0 - new box should be inserted here or before this box - stop loop
   - cmp > 0 - new box comes after this one - continue to next

3. Insert the new box at index idx.

### 3. Remove box from bay

![image](svg/removebox.svg)

### Summary — removeBoxFromBay
Removes a given box from both:
- Its specific bay list (where it’s physically stored),
- The overall warehouse collection of boxes.
It ensures the box is completely deleted from the system.

