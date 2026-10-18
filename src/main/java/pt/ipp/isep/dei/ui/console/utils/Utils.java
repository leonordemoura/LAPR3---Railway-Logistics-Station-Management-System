package pt.ipp.isep.dei.ui.console.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

/**
 * Utility class that provides various helper methods for console input,
 * input validation, and selecting options from lists. This class serves
 * to streamline and centralize frequently-used console input and output
 * operations.
 */
public class Utils {

    /**
     * Reads an integer from the console within the provided range.
     * The user is repeatedly prompted until a valid input is entered.
     *
     * @param prompt     The message displayed to prompt the user for input.
     * @param lowerLimit The lower limit of the valid range (inclusive).
     * @param upperLimit The upper limit of the valid range (inclusive).
     * @return A valid integer within the specified range.
     */
    public static int readIntFromConsole(String prompt, int lowerLimit, int upperLimit) {
        Scanner sc = new Scanner(System.in);
        boolean success = true;
        String input;
        int inputValue;
        do {
            if(!success) {
                System.out.printf("Error: Number must be between %d and %d! Please try again.\n", lowerLimit, upperLimit);
            }
            System.out.print(prompt);
            input = sc.nextLine();
            try {
                inputValue = Integer.parseInt(input);
            } catch(NumberFormatException e) {
                inputValue = -1;
            }
            success = (inputValue >= lowerLimit && inputValue <= upperLimit);
        } while(!success);
        return inputValue;
    }

    /**
     * Prompts the user to enter a float value within a specified range and validates the input.
     * <p>
     * Continues to prompt until the user provides a valid float within the range
     * {@code lowerLimit} to {@code upperLimit} (inclusive). Displays error messages for
     * invalid or out-of-range inputs.
     *
     * @param prompt      the message displayed to the user
     * @param lowerLimit  the minimum acceptable float value (inclusive)
     * @param upperLimit  the maximum acceptable float value (inclusive)
     * @return the valid float value entered by the user
     */
    public static float readFloatFromConsole(String prompt, float lowerLimit, float upperLimit) {
        Scanner sc = new Scanner(System.in);
        String input;
        float inputValue;
        do {
            System.out.print(prompt);
            input = sc.nextLine();
            try {
                inputValue = Float.parseFloat(input);

                if (inputValue < lowerLimit || inputValue > upperLimit) {
                    System.out.printf("Invalid input. Please enter a value between %.2f and %.2f.%n", lowerLimit, upperLimit);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid float.");
                inputValue = Float.NaN; // Use NaN to indicate an invalid state
            }
        } while (Float.isNaN(inputValue) || inputValue < lowerLimit || inputValue > upperLimit);
        return inputValue;
    }

    public static boolean checkForNoSpecialChars(String fileName) {
        if(fileName != null && !fileName.isBlank()) {
            char[] fileChars = fileName.toCharArray();
            for (char c : fileChars) {
                if (!Character.isLetterOrDigit(c)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Reads a single line of text from the console.
     *
     * @param prompt The message displayed to prompt the user for input.
     * @return The text entered by the user, or {@code null} if an exception occurs.
     */
    public static String readLineFromConsole(String prompt) {
        try {
            System.out.print(prompt);

            InputStreamReader converter = new InputStreamReader(System.in);
            BufferedReader in = new BufferedReader(converter);

            return in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reads a single line of text from the console in a loop, displaying an error
     * message until a valid input is provided.
     *
     * @param prompt  The message displayed to prompt the user for input.
     * @param success Indicator of whether the previous input was valid.
     * @return The text entered by the user, or {@code null} if an exception occurs.
     */
    public static String readLineFromConsoleLoop(String prompt, boolean success) {
        try {
            if(!success) {
                System.out.println("Error: Invalid input. Try again.");
            }
            System.out.print(prompt);

            InputStreamReader converter = new InputStreamReader(System.in);
            BufferedReader in = new BufferedReader(converter);

            return in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Displays a list of options with a header and allows the user to select an object,
     * repeating the input until a valid selection is made.
     *
     * @param list   The list of options to display.
     * @param header A header title displayed above the list.
     * @return The object selected by the user, or {@code null} if the selection is canceled.
     */
    public static Object showAndSelectOne(List list, String header) {
        Object result = null;
        boolean success = true;
        do {
            showList(list, header);
            try {
                result = selectsObject(list, success);
                success = true;
            } catch(IllegalArgumentException e) {
                success = false;
            }
        } while(!success);

        // result can return null, which means the "cancel" option was selected
        return result;
    }

    /**
     * Displays a list of options with a header and allows the user to select the index of an item.
     *
     * @param list   The list of options to display.
     * @param header A header title displayed above the list.
     * @return The index of the selected item, or -1 if the selection is canceled.
     */
    public static int showAndSelectIndex(List<?> list, String header) {
        int result;
        boolean success = true;
        do {
            showList(list, header);
            result = selectsIndexLoop(success);
            success = (result >= -1 && result < list.size());
        } while(!success);
        return result;
    }

    public static String createHeader(String headerText) {
        final int HEADER_LENGTH = 100;
        return String.format("\n\n--- %s %s\n", headerText, String.format("%" + (HEADER_LENGTH - 5 - headerText.length()) + "s", "").replace(" ", "-"));
    }

    /**
     * Displays a list of options with numerical indexing and a cancel option.
     *
     * @param list   The list of options to display.
     * @param header A header title displayed above the list.
     */
    public static void showList(List<?> list, String header) {
        System.out.println(header);

        int index = 0;
        for (Object o : list) {
            index++;

            System.out.println("  " + index + " - " + o.toString());
        }
        //System.out.println();
        System.out.println("  0 - Cancel\n");
    }

    /**
     * Prompts the user in a loop to select an object from a list by numerical index.
     *
     * @param list    The list of objects to select from.
     * @param success Indicator of whether the previous input was valid.
     * @return The selected object, or {@code null} if the selection is canceled.
     * @throws IllegalArgumentException If the provided index is invalid.
     */
    public static Object selectsObject(List list, boolean success) {
        String input = Utils.readLineFromConsoleLoop("Type your option: ", success);
        int value;
        try {
            value = Integer.parseInt(input);
        } catch(NumberFormatException e) {
            value = -1;
        }

        if(value < 0 || value > list.size()) {
            throw new IllegalArgumentException();
        } else if(value == 0) {
            return null;
        } else {
            return list.get(value - 1);
        }
    }

    /**
     * Prompts the user in a loop to select an index by numerical input.
     *
     * @param success Indicator of whether the previous input was valid.
     * @return The selected index minus one for zero-based indexing, or -1 if invalid.
     */
    public static int selectsIndexLoop(boolean success) {
        String input = Utils.readLineFromConsoleLoop("Type your option: ", success);
        int value;
        try {
            value = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            value = -1;
        }
        return value - 1;
    }

    /**
     * Prompts the user to select an index by numerical input.
     *
     * @param list The list of objects to select from.
     * @return The selected index minus one for zero-based indexing, or -1 if the selection is canceled.
     */
    public static int selectsIndex(List list) {
        String input;
        int value;
        do {
            input = Utils.readLineFromConsole("Type your option: ");
            try {
                value = Integer.valueOf(input);
            } catch (NumberFormatException ex) {
                value = -1;
            }
        } while (value < 0 || value > list.size());

        return value - 1;
    }
}
