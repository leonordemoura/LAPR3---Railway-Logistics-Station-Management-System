package pt.ipp.isep.dei.ESINF.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate result of USEI02 processing: line eligibility details and produced
 * allocation rows. Provides convenience counters grouped by eligibility status.
 */
public class OrderEligibilityResult {
    private List<LineEligibility> eligibilityList;
    private List<Allocation> allocationList;

    /**
     * Creates an empty result container.
     */
    public OrderEligibilityResult() {
        this.eligibilityList = new ArrayList<>();
        this.allocationList = new ArrayList<>();
    }

    /** Adds a line eligibility record. */
    public void addLineEligibility(LineEligibility lineEligibility) {
        eligibilityList.add(lineEligibility);
    }

    /** Appends allocation rows to the current list. */
    public void addAllocations(List<Allocation> allocations) {
        allocationList.addAll(allocations);
    }

    /** @return defensive copy of eligibility list */
    public List<LineEligibility> getEligibilityList() {
        return new ArrayList<>(eligibilityList);
    }

    /** @return defensive copy of allocation list */
    public List<Allocation> getAllocationList() {
        return new ArrayList<>(allocationList);
    }
}
