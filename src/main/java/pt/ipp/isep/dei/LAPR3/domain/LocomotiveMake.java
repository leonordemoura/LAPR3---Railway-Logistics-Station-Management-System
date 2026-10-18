package pt.ipp.isep.dei.LAPR3.domain;

/**
 * Represents a locomotive manufacturer with a unique identifier and name.
 */
public class LocomotiveMake {
    private int makeID;
    private String makeName;

    /**
     * Constructs a new LocomotiveMake with the specified ID and name.
     *
     * @param makeID   the unique identifier of the locomotive make
     * @param makeName the name of the locomotive make
     */
    public LocomotiveMake(int makeID, String makeName) {
        this.makeID = makeID;
        this.makeName = makeName;
    }

    /**
     * Returns the unique identifier of the locomotive make.
     *
     * @return the make ID
     */
    public int getMakeID() {
        return makeID;
    }

    /**
     * Returns the name of the locomotive make.
     *
     * @return the make name
     */
    public String getMakeName() {
        return makeName;
    }

    /**
     * Returns a string representation of the locomotive make.
     *
     * @return a formatted string containing the make ID and name
     */
    @Override
    public String toString() {
        return String.format("Make %d:\n Name: %s\n", makeID, makeName);
    }
}