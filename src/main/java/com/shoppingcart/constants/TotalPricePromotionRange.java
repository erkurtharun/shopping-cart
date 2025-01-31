package com.shoppingcart.constants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public enum TotalPricePromotionRange {
    RANGE_1(BigDecimal.valueOf(500.0), BigDecimal.valueOf(5000.0), BigDecimal.valueOf(250.0)),
    RANGE_2(BigDecimal.valueOf(5000.0), BigDecimal.valueOf(10000.0), BigDecimal.valueOf(500.0)),
    RANGE_3(BigDecimal.valueOf(10000.0), BigDecimal.valueOf(50000.0), BigDecimal.valueOf(1000.0)),
    RANGE_4(BigDecimal.valueOf(50000.0), BigDecimal.valueOf(Double.MAX_VALUE), BigDecimal.valueOf(2000.0));

    private final BigDecimal min;
    private final BigDecimal max;
    private final BigDecimal discount;

    TotalPricePromotionRange(BigDecimal min, BigDecimal max, BigDecimal discount) {
        this.min = min;
        this.max = max;
        this.discount = discount;
    }

    public BigDecimal getMin() {
        return min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public BigDecimal getDiscount() {
        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    public static TotalPricePromotionRange getRange(BigDecimal totalPrice) {
        return Arrays.stream(values())
                .filter(range -> totalPrice.compareTo(range.getMin()) >= 0 && totalPrice.compareTo(range.getMax()) < 0)
                .findFirst()
                .orElse(null);
    }
}
