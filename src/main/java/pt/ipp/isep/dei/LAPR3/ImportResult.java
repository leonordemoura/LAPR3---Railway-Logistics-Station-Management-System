package pt.ipp.isep.dei.LAPR3;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of an import operation, tracking messages, errors, and the number of records processed.
 */
public class ImportResult {
    private final List<String> messages;
    private final List<String> errors;
    private int totalRecords;

    /**
     * Constructs a new ImportResult with empty message and error lists and zero records.
     */
    public ImportResult() {
        this.messages = new ArrayList<>();
        this.errors = new ArrayList<>();
        this.totalRecords = 0;
    }

    /**
     * Adds a message to the import result log.
     *
     * @param message the message to add
     */
    public void addMessage(String message) {
        messages.add(message);
    }

    /**
     * Adds an error to the import result log.
     *
     * @param error the error message to add
     */
    public void addError(String error) {
        errors.add(error);
    }

    /**
     * Increments the count of successfully imported records.
     */
    public void incrementRecords() {
        totalRecords++;
    }

    /**
     * Returns the list of messages logged during the import process.
     *
     * @return the list of messages
     */
    public List<String> getMessages() {
        return messages;
    }

    /**
     * Returns the list of errors encountered during the import process.
     *
     * @return the list of errors
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Returns the total number of records successfully imported.
     *
     * @return the total number of records
     */
    public int getTotalRecords() {
        return totalRecords;
    }

    /**
     * Checks if any errors were encountered during the import process.
     *
     * @return true if there are errors, false otherwise
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Returns a string representation of the import result, including messages and errors.
     *
     * @return a formatted string with all messages and errors
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (String msg : messages) {
            sb.append(msg).append("\n");
        }

        if (!errors.isEmpty()) {
            sb.append("\n=== ERRORS ===\n");
            for (String error : errors) {
                sb.append("ERROR: ").append(error).append("\n");
            }
        }

        return sb.toString();
    }
}