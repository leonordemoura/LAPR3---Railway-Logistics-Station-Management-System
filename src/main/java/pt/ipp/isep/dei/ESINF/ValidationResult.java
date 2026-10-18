package pt.ipp.isep.dei.ESINF;

/**
 * Represents the result of a validation operation.
 * <p>
 * This class encapsulates the outcome of data validation, indicating whether
 * the validation succeeded or failed. In case of failure, it provides a
 * descriptive error message explaining what went wrong.
 */
class ValidationResult {
    /**
     * Indicates whether the validation was successful.
     */
    private boolean valid;

    /**
     * The error message describing why validation failed.
     * Will be null if validation succeeded.
     */
    private String errorMessage;

    /**
     * Constructs a new ValidationResult with the specified validity and error message.
     *
     * @param valid true if validation succeeded, false otherwise
     * @param errorMessage the error message if validation failed, or null if successful
     */
    public ValidationResult(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    /**
     * Checks whether the validation was successful.
     *
     * @return true if validation passed, false otherwise
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Retrieves the error message associated with this validation result.
     *
     * @return the error message if validation failed, or null if validation succeeded
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Creates a successful validation result.
     * <p>
     * This is a factory method that returns a ValidationResult instance indicating that validation passed with no errors.
     * </p>
     *
     * @return a ValidationResult indicating successful validation
     */
    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }

    /**
     * Creates a failed validation result with the specified error message.
     * <p>
     * This is a factory method that returns a ValidationResult instance indicating that validation failed, along with a descriptive error message.
     * </p>
     *
     * @param message the error message describing why validation failed
     * @return a ValidationResult indicating failed validation with the given message
     */
    public static ValidationResult error(String message) {
        return new ValidationResult(false, message);
    }
}
