package pt.ipp.isep.dei.ESINF.domain;

import java.util.List;

/**
 * Represents a picking plan that coordinates multiple {@link Trolley} instances.
 * <p>
 * A plan is the result of running a packing heuristic (e.g., FF/FFD/BFD) over
 * allocation rows, grouping {@link PickingItem}s into capacity-bounded trolleys.
 * It stores a plan identifier, the strategy name used to produce it, and the
 * full list of resulting trolleys.
 * </p>
 */
public class PickingPlan {
    private String planId;
    private String strategy;
    private List<Trolley> trolleys;

    /**
     * Creates a new picking plan.
     *
     * @param planId    unique identifier assigned to this plan
     * @param strategy  strategy/heuristic name used (e.g., "FIRST_FIT")
     * @param trolleys  list of trolleys with their assigned picking items
     */
    public PickingPlan(String planId, String strategy, List<Trolley> trolleys) {
        this.planId = planId;
        this.strategy = strategy;
        this.trolleys = trolleys;
    }

    /**
     * @return the unique plan identifier
     */
    public String getPlanId() { return planId; }
    /**
     * @return the packing strategy/heuristic name used to create the plan
     */
    public String getStrategy() { return strategy; }
    /**
     * @return the trolleys that compose this plan
     */
    public List<Trolley> getTrolleys() { return trolleys; }

    /**
     * @return total number of trolleys in this plan
     */
    public int getTotalTrolleys() {
        return trolleys.size();
    }

    /**
     * Computes the average utilization across all trolleys in this plan.
     * Utilization is expressed in percentage.
     *
     * @return average utilization percentage; 0.0 when there are no trolleys
     */
    public double getAverageUtilization() {
        if (trolleys.isEmpty()) return 0.0;
        double sum = trolleys.stream().mapToDouble(Trolley::getUtilization).sum();
        return sum / trolleys.size();
    }

    @Override
    public String toString() {
        return String.format("PickingPlan{id=%s, strategy=%s, trolleys=%d, avgUtilization=%.1f%%}",
                planId, strategy, getTotalTrolleys(), getAverageUtilization());
    }
}
