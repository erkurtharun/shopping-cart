package com.shoppingcart.constants;

public enum Category {
    FURNITURE(1001),
    ELECTRONICS(3004),
    DIGITAL_ITEM(7889),
    VAS_ITEM(3242);

    private final int id;

    Category(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
