package com.shoppingcart.models;

import java.math.BigDecimal;

public interface IItem {
    int getId();

    int getCategoryId();

    int getSellerId();

    BigDecimal getPrice();

    int getQuantity();

    void setQuantity(int quantity);

    boolean equals(Object o);
}