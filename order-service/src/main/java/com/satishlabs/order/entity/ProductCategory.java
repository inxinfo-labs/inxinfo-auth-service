package com.satishlabs.order.entity;

/**
 * Product (Item) categories. Items are physical products, distinct from Puja Types (ritual services).
 */
public enum ProductCategory {
    PUJA_SAMAGRI("Puja Samagri"),
    IDOLS_MURTIS("Idols & Murtis"),
    INCENSE_DHOOP("Incense & Dhoop"),
    OILS_GHEE("Oils & Ghee"),
    FLOWERS_GARLANDS("Flowers & Garlands"),
    PRASADAM("Prasadam"),
    BOOKS_MANTRAS("Books & Mantras"),
    VESSELS_CONTAINERS("Vessels & Containers"),
    DECORATIVE_ITEMS("Decorative Items"),
    FESTIVAL_SPECIALS("Festival Specials"),
    YANTRA_RUDRAKSHA("Yantra & Rudraksha"),
    GIFT_HAMPERS("Gift Hampers"),
    CLOTHING("Clothing"),
    OTHER("Other");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
