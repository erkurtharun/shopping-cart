package com.shoppingcart.promotions;

import com.shoppingcart.cart.Cart;
import com.shoppingcart.cart.ICart;
import com.shoppingcart.config.Config;
import com.shoppingcart.constants.PromotionType;
import com.shoppingcart.exceptions.ItemValidationException;
import com.shoppingcart.models.DefaultItem;
import com.shoppingcart.models.IItem;
import com.shoppingcart.models.VasItem;
import com.shoppingcart.testutil.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PromotionServiceIntegrationTest {
    private ICart cart;
    private PromotionService promotionService;

    @BeforeEach
    public void setUp() {
        IPromotion categoryPromotion = new CategoryPromotion();
        IPromotion totalPricePromotion = new TotalPricePromotion();
        IPromotion sameSellerPromotion = new SameSellerPromotion();
        promotionService = new PromotionService(List.of(categoryPromotion, totalPricePromotion, sameSellerPromotion));
        cart = new Cart(promotionService);
    }

    @Test
    public void testNoItems_NoPromotion() {
        PromotionResult result = promotionService.calculateBestPromotion(cart);
        assertEquals(0, result.discount().compareTo(BigDecimal.ZERO), "No discount should be applied if there are no items.");
    }

    @Test
    public void testNoPromotionsApplicable() {
        IItem item = TestUtils.createDefaultItem(1, 1001, 2001, 100.0, 1);
        cart.addItem(item);
        PromotionResult result = promotionService.calculateBestPromotion(cart);
        assertEquals(0, result.discount().compareTo(BigDecimal.ZERO), "No discount should be applied.");
    }

    @Test
    public void testBestPromotionWithNewItem_EmptyCart() {
        IItem newItem = TestUtils.createDefaultItem(1, 3003, 2001, 3000.0, 1);
        PromotionResult resultWithNewItem = promotionService.calculateBestPromotionWithNewItem(cart, newItem);
        assertEquals(PromotionType.TOTAL_PRICE.id, resultWithNewItem.promotionId(), "TotalPricePromotion should be the best promotion for an empty cart with the new item.");
        assertEquals(0, resultWithNewItem.discount().compareTo(BigDecimal.valueOf(250.0)), "Total discount should be 500.0 with new item from TotalPricePromotion.");
    }

    @Test
    public void testNoPromotionWithNewItem_EmptyCart() {
        IItem newItem = TestUtils.createDefaultItem(1, 0, 2001, 100.0, 1); // Price does not trigger any promotion
        PromotionResult resultWithNewItem = promotionService.calculateBestPromotionWithNewItem(cart, newItem);
        assertEquals(-1, resultWithNewItem.promotionId(), "No promotion should be applicable for the new item with low price.");
        assertEquals(0, resultWithNewItem.discount().compareTo(BigDecimal.ZERO), "Total discount should be 0.0 with new item.");
    }

    @Test
    public void testCalculateBestPromotion_NullCart() {
        Exception exception = assertThrows(ItemValidationException.class, () -> promotionService.calculateBestPromotion(null));

        assertEquals("Cart cannot be null.", exception.getMessage());
    }

    @Test
    public void testCalculateBestPromotionWithNewItem_NullCart() {
        IItem newItem = mock(IItem.class);

        Exception exception = assertThrows(ItemValidationException.class, () -> promotionService.calculateBestPromotionWithNewItem(null, newItem));

        assertEquals("Cart cannot be null.", exception.getMessage());
    }

    @Test
    public void testTotalPricePromotion() {
        DefaultItem item1 = TestUtils.createCategoryDiscountEligibleItem(1, 250.0);
        IItem item2 = TestUtils.createDefaultItem(2, 3003, 2001, 50.0, 1);
        cart.addItem(item1);
        cart.addItem(item2);

        VasItem vasItem = TestUtils.createVasItem(1, 3, 100.0, 2);
        cart.addVasItem(vasItem);

        PromotionResult result = promotionService.calculateBestPromotion(cart);
        assertEquals(PromotionType.TOTAL_PRICE.id, result.promotionId(), "TotalPricePromotion should be the best promotion.");
        assertEquals(0, result.discount().compareTo(BigDecimal.valueOf(250.0)), "Total discount should be 250.0 from TotalPricePromotion as the best promotion.");
    }

    @Test
    public void testTotalPricePromotionWithNewItem() {
        IItem item1 = TestUtils.createDefaultItem(1, 3003, 2001, 2000.0, 1);
        IItem item2 = TestUtils.createDefaultItem(2, 3003, 2001, 2000.0, 1);
        cart.addItem(item1);
        cart.addItem(item2);

        IItem newItem = TestUtils.createDefaultItem(3, 3003, 2002, 3000.0, 1);
        PromotionResult resultWithNewItem = promotionService.calculateBestPromotionWithNewItem(cart, newItem);
        assertEquals(PromotionType.TOTAL_PRICE.id, resultWithNewItem.promotionId(), "TotalPricePromotion should be the best promotion, as the new item triggers it.");
        assertEquals(0, resultWithNewItem.discount().compareTo(BigDecimal.valueOf(500.0)), "Total discount should be 500.00 with new Item from TotalPricePromotion as the best promotion.");
    }

    @Test
    public void testSameSellerPromotion() {
        IItem item1 = TestUtils.createDefaultItem(1, 3003, 2001, 2000.0, 1);
        IItem item2 = TestUtils.createDefaultItem(2, 3003, 2001, 2000.0, 1);
        cart.addItem(item1);
        cart.addItem(item2);

        PromotionResult result = promotionService.calculateBestPromotion(cart);
        assertEquals(PromotionType.SAME_SELLER.id, result.promotionId(), "SameSellerPromotion should be the best promotion.");
        assertEquals(0, result.discount().compareTo(BigDecimal.valueOf(400.0)), "Total discount should be 400.0 from SameSellerPromotion as the best promotion.");
    }

    @Test
    public void testSameSellerPromotionWithNewItem() {
        int discountCategoryId = Config.VALID_CATEGORIES_FOR_VAS.iterator().next();
        IItem item1 = TestUtils.createDefaultItem(1, discountCategoryId, 2001, 250.0, 1);
        IItem item2 = TestUtils.createDefaultItem(2, 3003, 2001, 50.0, 1);
        cart.addItem(item1);
        cart.addItem(item2);

        VasItem vasItem = new VasItem(1, 3, BigDecimal.valueOf(100.0), 2);
        cart.addVasItem(vasItem);

        IItem newItem = TestUtils.createDefaultItem(3, 3003, 2001, 3000.0, 1);
        PromotionResult resultWithNewItem = promotionService.calculateBestPromotionWithNewItem(cart, newItem);
        assertEquals(PromotionType.SAME_SELLER.id, resultWithNewItem.promotionId(), "SameSellerPromotion should be the best promotion, as the new item triggers it.");
        assertEquals(0, resultWithNewItem.discount().compareTo(BigDecimal.valueOf(330.0)), "Total discount should be 330.00 with new Item from SameSellerPromotion as the best promotion.");
    }

    @Test
    public void testCategoryPromotion() {
        IItem item1 = TestUtils.createDefaultItem(1, 3003, 2001, 200.0, 1);
        IItem item2 = TestUtils.createDefaultItem(2, 3003, 2001, 200.0, 1);
        IItem item3 = TestUtils.createDefaultItem(3, 3004, 2002, 50.0, 1);
        cart.addItem(item1);
        cart.addItem(item2);
        cart.addItem(item3);
        PromotionResult result = promotionService.calculateBestPromotion(cart);
        assertEquals(PromotionType.CATEGORY.id, result.promotionId(), "CategoryPromotion should be the best promotion.");
        assertEquals(0, result.discount().compareTo(BigDecimal.valueOf(20.0)), "Total discount should be 20.0 from CategoryPromotion as the best promotion.");
    }

    @Test
    public void testCategoryPromotionWithNewItem() {
        IItem item1 = TestUtils.createDefaultItem(1, 3003, 2001, 200.0, 1);
        IItem item2 = TestUtils.createDefaultItem(2, 3003, 2001, 200.0, 1);
        IItem item3 = TestUtils.createDefaultItem(3, 3004, 2002, 50.0, 1);
        cart.addItem(item1);
        cart.addItem(item2);
        cart.addItem(item3);

        IItem newItem = TestUtils.createDefaultItem(4, 3003, 2002, 40.0, 1);
        PromotionResult resultWithNewItem = promotionService.calculateBestPromotionWithNewItem(cart, newItem);
        assertEquals(PromotionType.CATEGORY.id, resultWithNewItem.promotionId(), "CategoryPromotion should be the best promotion, as the new item triggers it.");
        assertEquals(0, resultWithNewItem.discount().compareTo(BigDecimal.valueOf(22.0)), "Total discount should be 22.0 from CategoryPromotion with new item.");
    }

}