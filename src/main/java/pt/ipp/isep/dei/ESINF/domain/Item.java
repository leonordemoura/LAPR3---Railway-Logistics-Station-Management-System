package pt.ipp.isep.dei.ESINF.domain;

/**
 * Catalog item metadata used to compute weights in USEI03 and to reference
 * products in orders and boxes.
 */
public class Item {
    private String sku;
    private String name;
    private String category;
    private String unit;
    private double volume;
    private double unitWeight;

    /**
     * Creates a catalog item.
     * @param sku         unique SKU
     * @param name        display name
     * @param category    category name
     * @param unit        unit of measure (e.g., kg, unit)
     * @param volume      unit volume
     * @param unitWeight  unit weight (kg)
     */
    public Item(String sku, String name, String category, String unit, double volume, double unitWeight) {
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.volume = volume;
        this.unitWeight = unitWeight;
    }

    /**
     * Returns the SKU identifier.
     * @return SKU
     */
    public String getSku() { return sku; }

    /**
     * Returns the unit weight.
     * @return unit weight
     */
    public double getUnitWeight() { return unitWeight; }

    /**
     * Returns the product name.
     * @return name
     */
    public String getName() { return name; }

    /**
     * Returns the product category.
     * @return category
     */
    public String getCategory() { return category; }

    /**
     * Returns the unit of measure.
     * @return unit
     */
    public String getUnit() { return unit; }

    /**
     * Returns the unit volume in cubic meters.
     * @return volume
     */
    public double getVolume() { return volume; }
}
