package com.shoppingcart.promotions;

import com.shoppingcart.cart.ICart;
import com.shoppingcart.constants.Category;
import com.shoppingcart.constants.PromotionType;
import com.shoppingcart.constants.TotalPricePromotionRange;
import com.shoppingcart.models.DefaultItem;
import com.shoppingcart.testutil.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TotalPricePromotionUnitTest {
    private ICart mockCart;
    private TotalPricePromotion totalPricePromotion;

    @BeforeEach
    public void setUp() {
        totalPricePromotion = new TotalPricePromotion();
        mockCart = mock(ICart.class);
    }

    @Test
    public void testTotalPromotionId() {
        assertEquals(PromotionType.TOTAL_PRICE.id, totalPricePromotion.getId());
    }

    @Test
    public void testIsApplicable_InRange() {
        // Given
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.valueOf(5000));

        // When
        boolean result = totalPricePromotion.isApplicable(mockCart);

        // Then
        assertTrue(result);
    }

    @Test
    public void testIsApplicable_OutOfRange() {
        // Given
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.valueOf(100));

        // When
        boolean result = totalPricePromotion.isApplicable(mockCart);

        // Then
        assertFalse(result);
    }

    @Test
    public void testCalculatePromotion_OutOfRange() {
        // Given
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.valueOf(100));

        // When
        BigDecimal discount = totalPricePromotion.calculatePromotion(mockCart);

        // Then
        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    public void testCalculatePromotion_LowRange() {
        // Given
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.valueOf(1000));

        // When
        BigDecimal discount = totalPricePromotion.calculatePromotion(mockCart);

        // Then
        assertEquals(BigDecimal.valueOf(250).setScale(2, RoundingMode.HALF_UP), discount);
    }

    @Test
    public void testCalculatePromotion_MidRange() {
        // Given
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.valueOf(9000));

        // When
        BigDecimal discount = totalPricePromotion.calculatePromotion(mockCart);

        // Then
        assertEquals(BigDecimal.valueOf(500).setScale(2, RoundingMode.HALF_UP), discount);
    }

    @Test
    public void testCalculatePromotion_HighRange() {
        // Given
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.valueOf(30000));

        // When
        BigDecimal discount = totalPricePromotion.calculatePromotion(mockCart);

        // Then
        assertEquals(BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP), discount);
    }

    @Test
    public void testCalculatePromotion_TopRange() {
        // Given
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.valueOf(60000));

        // When
        BigDecimal discount = totalPricePromotion.calculatePromotion(mockCart);

        // Then
        assertEquals(BigDecimal.valueOf(2000).setScale(2, RoundingMode.HALF_UP), discount);
    }

    @Test
    public void testCalculatePromotionWithNewItemOutOfRange() {
        // Given
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.valueOf(100));
        DefaultItem newItem = (DefaultItem) TestUtils.createDefaultItem(2, Category.FURNITURE.getId(), 2001, 200, 1);

        // When
        BigDecimal discountWithNewItem = totalPricePromotion.calculatePromotionWithNewItem(mockCart, newItem);

        // Then
        assertEquals(BigDecimal.ZERO, discountWithNewItem);

    }

    @Test
    public void testCalculatePromotionWithNewItem() {
        // Given
        when(mockCart.getTotalPrice()).thenReturn(BigDecimal.valueOf(4000));

        DefaultItem newItem = (DefaultItem) TestUtils.createDefaultItem(2, Category.FURNITURE.getId(), 2001, 1000, 1);
        BigDecimal newTotalPrice = mockCart.getTotalPrice().add(newItem.getPrice());

        // When
        BigDecimal discountWithNewItem = totalPricePromotion.calculatePromotionWithNewItem(mockCart, newItem);

        // Then
        assertEquals(TotalPricePromotionRange.getRange(newTotalPrice).getDiscount(), discountWithNewItem);
    }
}
