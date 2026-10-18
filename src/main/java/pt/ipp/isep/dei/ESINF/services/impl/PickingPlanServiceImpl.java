package pt.ipp.isep.dei.ESINF.services.impl;

import pt.ipp.isep.dei.ESINF.domain.Allocation;
import pt.ipp.isep.dei.ESINF.domain.Item;
import pt.ipp.isep.dei.ESINF.domain.PickingItem;
import pt.ipp.isep.dei.ESINF.domain.PickingPlan;
import pt.ipp.isep.dei.ESINF.domain.Trolley;
import pt.ipp.isep.dei.ESINF.services.PickingPlanService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * USEI03 — Picking Plans
 * <p>
 * Service implementation that creates picking plans from allocation rows
 * produced by USEI02. It supports multiple packing heuristics (FF, FFD, BFD)
 * and two overflow policies (SPLIT, DEFER). The service converts allocation
 * rows into {@link PickingItem}s, packs them into capacity-bounded
 * {@link Trolley} instances, and returns a {@link PickingPlan} with summary
 * information.
 * </p>
 */
public class PickingPlanServiceImpl implements PickingPlanService {

    private final List<String> events = new ArrayList<>();
    private int planCounter = 1;

    /**
     * Creates a picking plan for the given set of allocation rows using the
     * requested heuristic and overflow policy.
     *
     * @param heuristic        packing heuristic (FF, FFD, BFD)
     * @param trolleyCapacity  capacity of each trolley (kg)
     * @param overflowPolicy   overflow policy (SPLIT or DEFER)
     * @param allocations      allocation rows from USEI02
     * @param skuToItem        map SKU -> {@link Item} for weight lookup
     * @return a {@link PickingPlan} with all created trolleys and metadata
     */
    @Override
    public PickingPlan createPickingPlan(PackingHeuristic heuristic, double trolleyCapacity, OverflowPolicy overflowPolicy, List<Allocation> allocations, Map<String, Item> skuToItem) {
        // Basic validation
        if (heuristic == null) {
            throw new IllegalArgumentException("Heuristic cannot be null");
        }
        if (trolleyCapacity <= 0) {
            throw new IllegalArgumentException("Trolley capacity must be positive");
        }
        if (overflowPolicy == null) {
            throw new IllegalArgumentException("Overflow policy cannot be null");
        }
        if (allocations == null) {
            throw new IllegalArgumentException("Allocations cannot be null");
        }
        if (allocations.isEmpty()) {
            throw new IllegalStateException("No allocations available. Please run Order Allocation (USEI02) first.");
        }
        if (skuToItem == null) {
            throw new IllegalArgumentException("Items map cannot be null");
        }
        
        events.clear();

        // Always create a fresh copy of items for each heuristic
        List<PickingItem> originalItems = convertAllocations(allocations, skuToItem);

        List<Trolley> trolleys;
        switch (heuristic) {
            case FIRST_FIT:
                trolleys = createFirstFit(new ArrayList<>(originalItems), trolleyCapacity, overflowPolicy);
                break;
            case FIRST_FIT_DECREASING:
                List<PickingItem> sortedItems = new ArrayList<>(originalItems);
                sortedItems.sort((a, b) -> Double.compare(b.getWeight(), a.getWeight()));
                trolleys = createFirstFit(sortedItems, trolleyCapacity, overflowPolicy);
                break;
            case BEST_FIT_DECREASING:
                List<PickingItem> sortedItemsBFD = new ArrayList<>(originalItems);
                sortedItemsBFD.sort((a, b) -> Double.compare(b.getWeight(), a.getWeight()));
                trolleys = createBestFitDecreasing(sortedItemsBFD, trolleyCapacity, overflowPolicy);
                break;
            default:
                throw new IllegalArgumentException("Unknown heuristic");
        }

        return new PickingPlan("PLAN-" + planCounter++, heuristic.toString(), trolleys);
    }

    /**
     * Returns informational events produced during packing (e.g., partial
     * allocations, deferrals due to capacity).
     *
     * @return list of human-readable event messages
     */
    @Override
    public List<String> getEvents() { return new ArrayList<>(events); }

    /**
     * Converts allocation rows into {@link PickingItem}s, computing total line
     * weight from unit weight and quantity.
     */
    private List<PickingItem> convertAllocations(List<Allocation> allocations, Map<String, Item> skuToItem) {
        List<PickingItem> items = new ArrayList<>();
        for (Allocation a : allocations) {
            Item item = skuToItem.get(a.getSku());
            double unitWeight = (item != null) ? item.getUnitWeight() : 0.0;
            double totalWeight = unitWeight * a.getQty();
            items.add(new PickingItem(a.getOrderId(), a.getLineNo(), a.getAisle(), a.getBay(), a.getBoxId(), a.getSku(), a.getQty(), totalWeight));
        }
        return items;
    }

    /**
     * Packs items using First Fit (FF), scanning items in the given order and
     * placing each in the first trolley where it fits (respecting capacity and
     * overflow policy).
     */
    private List<Trolley> createFirstFit(List<PickingItem> items, double capacity, OverflowPolicy policy) {
        List<Trolley> trolleys = new ArrayList<>();
        int trolleyId = 1;
        List<PickingItem> remainingItems = new ArrayList<>(items);

        while (!remainingItems.isEmpty()) {
            PickingItem item = remainingItems.remove(0);
            boolean placed = false;

            // Try adding to existing cart
            for (Trolley trolley : trolleys) {
                if (canFit(trolley, item, capacity)) {
                    add(trolley, item);
                    placed = true;
                    break;
                } else if (policy == OverflowPolicy.SPLIT) {
                    int qty = qtyThatFits(trolley, item, capacity);
                    if (qty > 0) {
                        addPartial(trolley, item, qty);
                        events.add(String.format("partial allocation: %s-%d box %s qty %d -> remainder %d",
                                item.getOrderId(), item.getLineNo(), item.getBoxId(), qty, item.getQuantity() - qty));

                        // Create remainder if there is quantity left
                        PickingItem remainder = remainder(item, qty);
                        if (remainder != null) {
                            remainingItems.add(0, remainder);
                        }
                        placed = true;
                        break;
                    }
                }
            }

            // If not placed, create new trolley
            if (!placed) {
                Trolley newTrolley = new Trolley("T" + trolleyId++, capacity, 0.0, new ArrayList<>());
                if (item.getWeight() <= capacity) {
                    add(newTrolley, item);
                } else if (policy == OverflowPolicy.SPLIT && item.getQuantity() > 1) {
                    int qty = qtyThatFits(newTrolley, item, capacity);
                    if (qty > 0) {
                        addPartial(newTrolley, item, qty);
                        events.add(String.format("partial allocation: %s-%d box %s qty %d -> remainder %d",
                                item.getOrderId(), item.getLineNo(), item.getBoxId(), qty, item.getQuantity() - qty));

                        PickingItem remainder = remainder(item, qty);
                        if (remainder != null) {
                            remainingItems.add(0, remainder);
                        }
                    } else {
                        events.add(String.format("skipped due to capacity: %s-%d box %s qty %d",
                                item.getOrderId(), item.getLineNo(), item.getBoxId(), item.getQuantity()));
                    }
                } else {
                    events.add(String.format("skipped due to capacity: %s-%d box %s qty %d",
                            item.getOrderId(), item.getLineNo(), item.getBoxId(), item.getQuantity()));
                }
                // Add the trolley only if something has been placed
                if (!newTrolley.getItems().isEmpty()) {
                    trolleys.add(newTrolley);
                }
            }
        }

        return trolleys;
    }


    /**
     * Packs items using Best Fit Decreasing (BFD), assuming items are already
     * sorted by weight descending; places each item in the trolley whose
     * remaining capacity after placement would be minimal (tightest fit),
     * respecting the overflow policy.
     */
    private List<Trolley> createBestFitDecreasing(List<PickingItem> items, double capacity, OverflowPolicy policy) {
        List<Trolley> trolleys = new ArrayList<>();
        int trolleyId = 1;
        List<PickingItem> remainingItems = new ArrayList<>(items);

        while (!remainingItems.isEmpty()) {
            PickingItem item = remainingItems.remove(0);
            Trolley best = null;
            double bestRem = Double.MAX_VALUE;

            // Find the best trolley to fit the item
            for (Trolley trolley : trolleys) {
                if (canFit(trolley, item, capacity)) {
                    double rem = capacity - trolley.getUsedWeight() - item.getWeight();
                    if (rem < bestRem) {
                        bestRem = rem;
                        best = trolley;
                    }
                } else if (policy == OverflowPolicy.SPLIT && item.getQuantity() > 1) {
                    int qty = qtyThatFits(trolley, item, capacity);
                    if (qty > 0) {
                        double rem = capacity - trolley.getUsedWeight() - (item.getWeightPerUnit() * qty);
                        if (rem < bestRem) {
                            bestRem = rem;
                            best = trolley;
                        }
                    }
                }
            }

            if (best != null) {
                if (canFit(best, item, capacity)) {
                    add(best, item);
                } else if (policy == OverflowPolicy.SPLIT) {
                    int qty = qtyThatFits(best, item, capacity);
                    if (qty > 0) {
                        addPartial(best, item, qty);
                        events.add(String.format("partial allocation: %s-%d box %s qty %d -> remainder %d",
                                item.getOrderId(), item.getLineNo(), item.getBoxId(), qty, item.getQuantity() - qty));

                        PickingItem remainder = remainder(item, qty);
                        if (remainder != null) {
                            remainingItems.add(0, remainder);
                        }
                    }
                }
            } else {
                // Create new trolley if it doesn't fit in an existing one
                Trolley newTrolley = new Trolley("T" + trolleyId++, capacity, 0.0, new ArrayList<>());
                boolean addedSomething = false;

                if (item.getWeight() <= capacity) {
                    add(newTrolley, item);
                    addedSomething = true;
                } else if (policy == OverflowPolicy.SPLIT) {
                    int qty = qtyThatFits(newTrolley, item, capacity);
                    if (qty > 0) {
                        addPartial(newTrolley, item, qty);
                        addedSomething = true;

                        events.add(String.format("partial allocation: %s-%d box %s qty %d -> remainder %d",
                                item.getOrderId(), item.getLineNo(), item.getBoxId(), qty, item.getQuantity() - qty));

                        PickingItem remainder = remainder(item, qty);
                        if (remainder != null) {
                            remainingItems.add(0, remainder);
                        }
                    } else {
                        events.add(String.format("skipped due to capacity: %s-%d box %s qty %d",
                                item.getOrderId(), item.getLineNo(), item.getBoxId(), item.getQuantity()));
                    }
                } else {
                    events.add(String.format("skipped due to capacity: %s-%d box %s qty %d",
                            item.getOrderId(), item.getLineNo(), item.getBoxId(), item.getQuantity()));
                }

                if (addedSomething) {
                    trolleys.add(newTrolley);
                }
            }
        }

        return trolleys;
    }

    /**
     * Checks if the full item fits in the trolley given the capacity.
     */
    private boolean canFit(Trolley trolley, PickingItem item, double capacity) {
        return trolley.getUsedWeight() + item.getWeight() <= capacity;
    }

    /**
     * Computes the maximum quantity of the item that can fit in the trolley
     * given its remaining capacity.
     */
    private int qtyThatFits(Trolley trolley, PickingItem item, double capacity) {
        double remaining = capacity - trolley.getUsedWeight();
        if (item.getWeightPerUnit() <= 0) return 0;
        return (int)Math.floor(remaining / item.getWeightPerUnit());
    }

    /**
     * Adds an entire item to the trolley, updating used weight.
     */
    private void add(Trolley trolley, PickingItem item) {
        trolley.getItems().add(item);
        trolley.setUsedWeight(trolley.getUsedWeight() + item.getWeight());
    }

    /**
     * Adds a partial quantity of an item to the trolley, updating used weight
     * and creating a new {@link PickingItem} instance that represents the
     * partial allocation.
     */
    private void addPartial(Trolley trolley, PickingItem base, int qty) {
        double partialWeight = base.getWeightPerUnit() * qty;
        PickingItem partial = new PickingItem(base.getOrderId(), base.getLineNo(), base.getAisle(), base.getBay(), base.getBoxId(), base.getSku(), qty, partialWeight);
        add(trolley, partial);
    }

    /**
     * Creates a remainder item from a partially allocated item.
     */
    private PickingItem remainder(PickingItem base, int qtyTaken) {
        int remainQty = base.getQuantity() - qtyTaken;
        double remainWeight = base.getWeight() - base.getWeightPerUnit() * qtyTaken;
        return new PickingItem(base.getOrderId(), base.getLineNo(), base.getAisle(), base.getBay(), base.getBoxId(), base.getSku(), remainQty, remainWeight);
    }

}
