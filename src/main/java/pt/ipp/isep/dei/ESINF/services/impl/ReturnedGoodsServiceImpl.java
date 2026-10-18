package pt.ipp.isep.dei.ESINF.services.impl;

import pt.ipp.isep.dei.ESINF.domain.Return;
import pt.ipp.isep.dei.ESINF.domain.ReturnedGoods;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service class responsible for handling the processing of returned goods.
 * It retrieves all product returns from the repository, determines whether
 * each item should be restocked or discarded, and logs the results to a file.
 */
public class ReturnedGoodsServiceImpl {

    /**
     * Main method that manages the entire returned goods process.
     * 1. Retrieves all returns from the repository.
     * 2. Sorts them by timestamp (newest first), then by return ID.
     * 3. Converts them into ReturnedGoods objects (with restock/discard status).
     * 4. Writes the processed list to a log file.
     *
     * @return a list of processed ReturnedGoods objects
     */
    public List<ReturnedGoods> handleReturnedGoods() {

        List<Return> returnsList = getAllReturns();
        List<ReturnedGoods> returnedGoodsList = new ArrayList<>();

        // Sort returns: newest first, then by ID if timestamps are equal
        returnsList.sort(Comparator.comparing(Return::getTimestamp).reversed()
                .thenComparing(Return::getReturnId));

        // Convert Return objects into ReturnedGoods and apply business logic
        orderedReturnsInReturnedGoodsList(returnsList, returnedGoodsList);

        // Output results to a file
        printToFile(returnedGoodsList);

        return returnedGoodsList;
    }

    /**
     * Converts a list of Return objects into ReturnedGoods objects,
     * marking each as restocked or discarded depending on the reason.
     */
    private void orderedReturnsInReturnedGoodsList(List<Return> returnsList, List<ReturnedGoods> returnedGoodsList) {
        for (Return ret : returnsList) {
            // Check if return is invalid (damaged or expired)
            boolean valid = !isReturnInvalid(ret.getReason());
            ReturnedGoods returnedGoods = new ReturnedGoods(
                    ret.getReturnId(),
                    ret.getTimestamp(),
                    ret.getSku(),
                    ret.getQty(),
                    valid
            );
            addToList(returnedGoodsList, returnedGoods);
        }
    }

    /** Adds a ReturnedGoods item to the list. */
    private void addToList(List<ReturnedGoods> returnedGoodsList, ReturnedGoods returnedGoods) {
        returnedGoodsList.add(returnedGoods);
    }

    /**
     * Determines whether a return should be considered invalid,
     * meaning it cannot be restocked.
     *
     * @param reason the reason for the return
     * @return true if the item is damaged or expired
     */
    private boolean isReturnInvalid(String reason) {
        return (reason.equals("damaged") || reason.equals("expired"));
    }

    /**
     * Retrieves all Return objects from the warehouse repository.
     * Throws an exception if none are found.
     */
    public List<Return> getAllReturns() {
        List<Return> returnsGet = WarehouseRepository.getReturns();
        if (returnsGet.isEmpty()) throw new IllegalArgumentException("No returns found.");
        return returnsGet;
    }

    /**
     * Writes the processed returned goods data to a text file for auditing.
     * Each line includes the timestamp, return ID, SKU, action taken, and quantity.
     */
    public void printToFile(List<ReturnedGoods> returns) {
        Path out = Paths.get("dataFiles/Audit_Logs/returned_goods.txt");
        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            for (ReturnedGoods ret : returns) {
                // Determine the action taken
                String action = ret.getActionTaken() ? "Restocked |" : "Discarded |";

                // Format output line
                String line = String.format(" %s | returnId=%s | sku=%s | action=%s quantity=%d%n",
                        ret.getTimestamp(), ret.getReturnId(), ret.getSku(), action, ret.getQty());

                bw.write(line);
            }
        } catch (IOException e) {
            // Handle file writing errors
            System.err.println("Failed to write returned goods file: " + e.getMessage());
        }
    }
}
