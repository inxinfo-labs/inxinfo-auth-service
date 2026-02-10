package com.satishlabs.puja.entity;

/**
 * Ritual / Puja types used in Puja catalog (PujaType) and Pandit specializations.
 * Use when creating Puja services or configuring what a Pandit offers.
 */
public enum RitualType {
    GRIHA_PRAVESH("Griha Pravesh"),
    MARRIAGE_CEREMONY("Marriage Ceremony"),
    HAVAN_HOMA("Havan/Homa"),
    SATYANARAYAN_PUJA("Satyanarayan Puja"),
    GANESH_PUJA("Ganesh Puja"),
    DURGA_PUJA("Durga Puja"),
    LAKSHMI_PUJA("Lakshmi Puja"),
    SHIVA_PUJA("Shiva Puja"),
    HANUMAN_PUJA("Hanuman Puja"),
    NAVRATRI_PUJA("Navratri Puja"),
    DIWALI_PUJA("Diwali Puja"),
    KARVA_CHAUTH("Karva Chauth"),
    THREAD_CEREMONY("Thread Ceremony"),
    NAMING_CEREMONY("Naming Ceremony"),
    MUNDAN_CEREMONY("Mundan Ceremony"),
    FUNERAL_RITES("Funeral Rites"),
    ANNIVERSARY_PUJA("Anniversary Puja"),
    BUSINESS_OPENING("Business Opening"),
    VEHICLE_PUJA("Vehicle Puja"),
    FESTIVAL_CELEBRATIONS("Festival Celebrations"),
    PERSONAL_CONSULTATION("Personal Consultation"),
    KUNDLI_READING("Kundli Reading"),
    OTHER("Other");

    private final String displayName;

    RitualType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
