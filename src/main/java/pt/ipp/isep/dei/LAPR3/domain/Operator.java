package pt.ipp.isep.dei.LAPR3.domain;

/**
 * Represents an operator entity with a unique identifier, VAT number, and name.
 */
public class Operator {
    private int operatorId;
    private String vatNumber;
    private String operatorName;

    /**
     * Constructs a new Operator with the specified ID, VAT number, and name.
     *
     * @param operatorId   the unique identifier of the operator
     * @param vatNumber    the VAT number of the operator
     * @param operatorName the name of the operator
     */
    public Operator(int operatorId, String vatNumber, String operatorName) {
        this.operatorId = operatorId;
        this.vatNumber = vatNumber;
        this.operatorName = operatorName;
    }

    /**
     * Returns the unique identifier of the operator.
     *
     * @return the operator ID
     */
    public int getOperatorId() {
        return operatorId;
    }

    /**
     * Returns the VAT number of the operator.
     *
     * @return the VAT number
     */
    public String getVatNumber() {
        return vatNumber;
    }

    /**
     * Returns the name of the operator.
     *
     * @return the operator name
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * Returns a string representation of the operator.
     *
     * @return a formatted string containing the operator ID, name, and VAT number
     */
    @Override
    public String toString() {
        return String.format("Operator %d:\n Name: %s\n VAT Number: %s", operatorId, operatorName, vatNumber);
    }
}