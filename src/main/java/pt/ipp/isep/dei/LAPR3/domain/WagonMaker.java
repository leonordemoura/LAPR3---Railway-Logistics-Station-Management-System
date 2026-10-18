package pt.ipp.isep.dei.LAPR3.domain;

/**
 * Represents a wagon manufacturer with a unique identifier and name.
 */
public class WagonMaker {
    private int wagonMakerID;
    private String wagonMakerName;

    /**
     * Constructs a new WagonMaker with the specified ID and name.
     *
     * @param wagonMakerID   the unique identifier of the wagon maker
     * @param wagonMakerName the name of the wagon maker
     */
    public WagonMaker(int wagonMakerID, String wagonMakerName) {
        this.wagonMakerID = wagonMakerID;
        this.wagonMakerName = wagonMakerName;
    }

    /**
     * Returns the unique identifier of the wagon maker.
     *
     * @return the wagon maker ID
     */
    public int getWagonMakerID() {
        return wagonMakerID;
    }

    /**
     * Returns the name of the wagon maker.
     *
     * @return the wagon maker name
     */
    public String getWagonMakerName() {
        return wagonMakerName;
    }

    /**
     * Returns a string representation of the wagon maker.
     *
     * @return a formatted string containing the wagon maker ID and name
     */
    @Override
    public String toString() {
        return String.format("Wagon Maker %d:\n Name: %s", wagonMakerID, wagonMakerName);
    }
}