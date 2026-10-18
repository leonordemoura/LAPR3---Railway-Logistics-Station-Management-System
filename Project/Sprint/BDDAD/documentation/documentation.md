# Documentation

## User Story
**USBD33**  
As a Freight Manager, I want the database to not allow a train to exceed its predefined maximum length.

---

## Objective
Ensure that the total length of a train **never exceeds the value defined in `Train.maxLenght`**, regardless of:
- locomotives being added or updated
- freight wagons being added or updated
- INSERT or UPDATE operations


---

## Problem Analysis

The total length of a train is the sum of:
- the length of all **locomotives** assigned to the train (`Train_Locomotive`)
- the length of all **freight wagons** associated through freights (`Train_Freight → Freight_Wagon`)


**Conclusion:**  
This business rule **cannot be implemented using constraints alone**, making the use of **TRIGGERS mandatory**.

---

## Adopted Solution

Two triggers were created, one for each type of train composition element:

| Trigger | Table | Purpose |
|------|------|------|
| `trg_train_maxlength_loco` | `Train_Locomotive` | Validates train length when locomotives are added or updated |
| `trg_train_maxlength_freight` | `Train_Freight` | Validates train length when freight wagons are added or updated |

Both triggers:
- execute **BEFORE INSERT OR UPDATE**
- calculate the current total length of the train
- add the length of the new element
- prevent the operation if the maximum length is exceeded

---

# Database Constraints – Global Integrity Rules

## Overview

The database schema relies heavily on **constraints** to guarantee:
- data integrity
- consistency between entities
- enforcement of domain rules
- prevention of invalid or contradictory states

The constraints used can be grouped into:
- Primary Keys
- Foreign Keys
- UNIQUE constraints
- CHECK constraints
- Composite Keys

---

## Primary Key Constraints

Every table defines a **PRIMARY KEY** to ensure:
- unique identification of each record
- entity integrity
- efficient joins between tables

### Examples

- `Train(trainID)`
- `Vehicle(vehicleID)`
- `Facility(facilityID)`
- `LineSegment(segmentID)`
- `Route(routeID)`

### Composite Primary Keys

Some associative tables use **composite primary keys** to model many-to-many relationships and prevent duplicates:

- `Train_Locomotive(trainID, locomotiveID)`
- `Train_Freight(trainID, freightID)`
- `Freight_Wagon(wagonID, freightID)`
- `Gauge_Vehicle(dimension, vehicleID)`
- `Path_Facility(pathID, facilityID)`
- `Path_Route(pathID, routeID)`

These constraints guarantee that:
- the same association cannot be inserted twice
- relationships remain unambiguous and consistent

---

## Foreign Key Constraints

Foreign keys enforce **referential integrity** by ensuring that references between tables are always valid.

They prevent:
- orphan records
- invalid relationships
- inconsistent deletions or insertions

---

## UNIQUE Constraints

UNIQUE constraints prevent duplicate values in attributes that must be globally unique.

### Examples

- `Facility.name`
- `Operator.vatNumber`
- `LineOwner.vatNumber`

These constraints ensure:
- prevention of duplicate real-world entities
- data consistency across the system

---

## CHECK Constraints

CHECK constraints enforce **domain rules** and restrict attribute values to valid sets.

### Binary Flags
Several attributes are limited to `'Y'` or `'N'`:
- `Line.isElectrified`
- `LineSegment.maintenanceStatus`
- `FacilityType.isIntermodal`

This guarantees uniform representation of boolean values across the database.

---

### Enumerated States
Some attributes represent predefined states:

- `Train.status IN ('PLANNED','AVAILABLE','RUNNING')`
- `Locomotive.status IN ('ASSIGNED','AVAILABLE')`
- `Wagon.status IN ('ASSIGNED','AVAILABLE')`

These constraints:
- prevent invalid states
- simplify business logic
- improve data readability and consistency

---

### Semantic Constraints

#### FacilityType Intermodal Rule

CHECK (
    (name = 'TERMINAL' AND isIntermodal IN ('Y','N')) OR
    (name IN ('STATION','FREIGHT_YARD') AND isIntermodal = 'N')
)

This constraint enforces a real-world business rule:
- Only terminals may be intermodal
- Stations and freight yards can never be intermodal
