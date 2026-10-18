package pt.ipp.isep.dei.ui.console.menu;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Represents a single menu item in a console-based application.
 * <p>
 * Each menu item has a textual description and an associated {@link Runnable} UI action
 * that is executed when the item is selected.
 * </p>
 * <p>
 * The description cannot be null or empty, and the UI action cannot be null.
 * </p>
 */
public class MenuItem {
    private final String description;
    private final Runnable ui;

    /**
     * Constructs a new {@code MenuItem} with the given description and UI action.
     *
     * @param description the text to display for this menu item; must not be null or empty
     * @param ui          the {@link Runnable} action to execute when this item is selected; must not be null
     * @throws IllegalArgumentException if description is null/empty or ui is null
     */
    public MenuItem(String description, Runnable ui) {
        if (StringUtils.isBlank(description)) {
            throw new IllegalArgumentException("MenuItem description cannot be null or empty.");
        }
        if (Objects.isNull(ui)) {
            throw new IllegalArgumentException("MenuItem does not support a null UI.");
        }

        this.description = description;
        this.ui = ui;
    }


    /**
     * Executes the UI action associated with this menu item.
     */
    public void run() {
        this.ui.run();
    }

    /**
     * Checks if this menu item has the given description.
     *
     * @param description the description to compare
     * @return {@code true} if the descriptions are equal, {@code false} otherwise
     */
    public boolean hasDescription(String description) {
        return this.description.equals(description);
    }

    /**
     * Returns the description of this menu item.
     *
     * @return the description string
     */
    public String toString() {
        return this.description;
    }
}