package pt.ipp.isep.dei.LAPR3.domain;

/**
 * Represents a line owner entity with a unique identifier, VAT number, and name.
 */
public class LineOwner {
    private int lineOwnerID;
    private String vatNumber;
    private String name;

    /**
     * Constructs a new LineOwner with the specified ID, VAT number, and name.
     *
     * @param lineOwnerID the unique identifier of the line owner
     * @param vatNumber   the VAT number of the line owner
     * @param name        the name of the line owner
     */
    public LineOwner(int lineOwnerID, String vatNumber, String name) {
        this.lineOwnerID = lineOwnerID;
        this.vatNumber = vatNumber;
        this.name = name;
    }

    /**
     * Returns the unique identifier of the line owner.
     *
     * @return the line owner ID
     */
    public int getLineOwnerID() {
        return lineOwnerID;
    }

    /**
     * Returns the VAT number of the line owner.
     *
     * @return the VAT number
     */
    public String getVatNumber() {
        return vatNumber;
    }

    /**
     * Returns the name of the line owner.
     *
     * @return the line owner name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a string representation of the line owner.
     *
     * @return a formatted string containing the line owner ID, VAT number, and name
     */
    @Override
    public String toString() {
        return String.format("Line Owner %d:\n VAT Number: %s\n Name: %s", lineOwnerID, vatNumber, name);
    }
}