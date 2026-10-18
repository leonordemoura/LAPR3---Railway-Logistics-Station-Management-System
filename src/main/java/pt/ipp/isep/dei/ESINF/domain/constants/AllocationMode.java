package pt.ipp.isep.dei.ESINF.domain.constants;

/**
 * Enum for allocation modes with STRICT as default
 */
public enum AllocationMode {
    /** Default: line is ELIGIBLE only if fully allocated */
    STRICT,
    /** Accept partial fulfillment */
    PARTIAL;

    /**
     * Gets the default mode (STRICT)
     * @return default allocation mode
     */
    public static AllocationMode getDefault() {
        return STRICT;
    }
}
