package pt.ipp.isep.dei.ESINF.services;

import pt.ipp.isep.dei.ESINF.domain.Allocation;
import pt.ipp.isep.dei.ESINF.domain.Item;
import pt.ipp.isep.dei.ESINF.domain.PickingPlan;

import java.util.List;
import java.util.Map;

/**
 * USEI03 - Picking Plans service API (plugável):
 * consumes allocation rows and item catalog to produce capacity-bounded picking waves.
 */
public interface PickingPlanService {

    /**
     * Enum with all the heuristics available.
     */
    enum PackingHeuristic {
        FIRST_FIT,
        FIRST_FIT_DECREASING,
        BEST_FIT_DECREASING
    }

    /**
     * Enum with all the overflow policies available.
     */
    enum OverflowPolicy {
        SPLIT, // split allocation across trolleys
        DEFER  // move entire allocation to next trolley
    }

    /**
     * Build a picking plan for a set of allocation rows.
     * @param heuristic packing heuristic
     * @param trolleyCapacity capacity (kg) per trolley
     * @param overflowPolicy split/defer policy when allocation doesn't fit fully
     * @param allocations allocation rows (from USEI02)
     * @param skuToItem map sku->Item to compute weights (qty * item.unitWeight)
     * @return picking plan with all trolleys
     */
    PickingPlan createPickingPlan(
            PackingHeuristic heuristic,
            double trolleyCapacity,
            OverflowPolicy overflowPolicy,
            List<Allocation> allocations,
            Map<String, Item> skuToItem);

    List<String> getEvents();
}
