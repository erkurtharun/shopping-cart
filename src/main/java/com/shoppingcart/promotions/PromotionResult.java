package com.shoppingcart.promotions;

import java.math.BigDecimal;

public record PromotionResult(BigDecimal discount, int promotionId) {
}
