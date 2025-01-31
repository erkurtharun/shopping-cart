package com.shoppingcart.validation;

import com.shoppingcart.cart.Cart;
import com.shoppingcart.cart.ICart;
import com.shoppingcart.config.Config;
import com.shoppingcart.constants.Category;
import com.shoppingcart.exceptions.ItemNotFoundException;
import com.shoppingcart.exceptions.ItemValidationException;
import com.shoppingcart.models.*;
import com.shoppingcart.promotions.CategoryPromotion;
import com.shoppingcart.promotions.PromotionService;
import com.shoppingcart.promotions.SameSellerPromotion;
import com.shoppingcart.promotions.TotalPricePromotion;
import com.shoppingcart.testutil.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class CartItemValidatorIntegrationTest {

    private CartItemValidator validator;
    private ICart cart;

    @BeforeEach
    public void setUp() {
        validator = new CartItemValidator();
        PromotionService promotionService = new PromotionService(Arrays.asList(
                new SameSellerPromotion(),
                new CategoryPromotion(),
                new TotalPricePromotion()
        ));
        cart = new Cart(promotionService);
    }

    @Test
    public void testValidateItem_ValidItem() {
        IItem item = new DefaultItem(1, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100.0), 1);
        assertDoesNotThrow(() -> validator.validateItem(item, cart));
    }

    @Test
    public void testValidateItem_NullItem() {
        assertThrows(ItemValidationException.class, () -> validator.validateItem(null, cart));
    }

    @Test
    public void testValidateItem_NullCart() {
        IItem item = TestUtils.createDefaultItem(1, 1001, 2001, 100.0, 1);
        assertThrows(ItemValidationException.class, () -> validator.validateItem(item, null));
    }

    @Test
    public void testValidateCategory_VasItemDirectlyAdded() {
        DefaultItem defaultItem = new DefaultItem(1, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100.0), 1);
        cart.addItem(defaultItem);
        VasItem vasItem = new VasItem(1, 2, Category.VAS_ITEM.getId(), Config.VAS_ITEM_SELLER_ID, BigDecimal.valueOf(20.0), 3);
        cart.addVasItem(vasItem);
        VasItem item = new VasItem(1, 3, Category.VAS_ITEM.getId(), Config.VAS_ITEM_SELLER_ID, BigDecimal.valueOf(20.0), 1);
        assertThrows(ItemValidationException.class, () -> validator.validateItem(item, cart), "VAS items cannot be added directly to the cart.");
    }

    @Test
    public void testValidateCategory_ValidDigitalItem() {
        IItem item = new DigitalItem(1, 2001, BigDecimal.valueOf(50.0), 2);
        assertDoesNotThrow(() -> validator.validateItem(item, cart));
    }

    @Test
    public void testValidateCategory_InvalidDigitalItem() {
        cart.addItem(new DigitalItem(1, 2001, BigDecimal.valueOf(50.0), Config.MAX_DIGITAL_ITEM_QUANTITY));
        IItem item = new DigitalItem(Config.MAX_DIGITAL_ITEM_QUANTITY + 1, 2001, BigDecimal.valueOf(50.0), 1);
        assertThrows(ItemValidationException.class, () -> validator.validateItem(item, cart), "Adding digital items exceeds the allowed limit.");
    }

    @Test
    public void testValidateVasItem_ValidVasItem() {
        IItem parentItem = TestUtils.createDefaultItem(1, Config.VALID_CATEGORIES_FOR_VAS.iterator().next(), 2001, 100.0, 1);
        cart.addItem(parentItem);
        VasItem vasItem = new VasItem(1, 2, BigDecimal.valueOf(50.0), 1);
        assertDoesNotThrow(() -> validator.validateVasItem(vasItem, cart));
    }

    @Test
    public void testValidateVasItem_NullVasItem() {
        assertThrows(ItemValidationException.class, () -> validator.validateVasItem(null, cart));
    }

    @Test
    public void testValidateVasItem_ParentItemNotFound() {
        VasItem vasItem = new VasItem(1, 2, BigDecimal.valueOf(50.0), 1);
        assertThrows(ItemNotFoundException.class, () -> validator.validateVasItem(vasItem, cart));
    }

    @Test
    public void testValidateVasItem_InvalidParentItem() {
        IItem parentItem = TestUtils.createDigitalItem(1, 2001, 100.0, 1);
        cart.addItem(parentItem);
        VasItem vasItem = new VasItem(1, 2, BigDecimal.valueOf(50.0), 1);
        assertThrows(ItemValidationException.class, () -> validator.validateVasItem(vasItem, cart));
    }

    @Test
    public void testValidateMaxTotalQuantity() {
        int quantityPerItem = Config.MAX_TOTAL_ITEMS / Config.MAX_UNIQUE_ITEMS;
        for (int i = 1; i < Config.MAX_UNIQUE_ITEMS; i++) {
            cart.addItem(new DefaultItem(i, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100.0),quantityPerItem));
        }
        IItem item = new DefaultItem(1, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100.0), quantityPerItem);
        assertDoesNotThrow(() -> validator.validateItem(item, cart));
    }

    @Test
    public void testValidateTotalQuantityInvalid() {
        for (int i = 4; i < Config.MAX_TOTAL_ITEMS; i+=4) {
            cart.addItem(new DefaultItem(i, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100.0), 4));
        }
        IItem item = new DefaultItem(Config.MAX_TOTAL_ITEMS + 1, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100.0), 4);
        assertThrows(ItemValidationException.class, () -> validator.validateItem(item, cart), "Adding items exceeds the total allowed limit.");
    }

    @Test
    public void testValidateMaxTotalAmount() {
        IItem item = new DefaultItem(1, Category.FURNITURE.getId(), 2001, Config.MAX_TOTAL_AMOUNT, 1);
        assertDoesNotThrow(() -> validator.validateItem(item, cart));
    }

    @Test
    public void testValidateItem_TotalAmountInvalid() {
        BigDecimal highPrice = Config.MAX_TOTAL_AMOUNT.divide(BigDecimal.valueOf(2), RoundingMode.DOWN);
        cart.addItem(new DefaultItem(1, Category.FURNITURE.getId(), 2001, highPrice, 2));
        IItem item = new DefaultItem(2, Category.FURNITURE.getId(), 2001, highPrice, 1);
        assertThrows(ItemValidationException.class, () -> validator.validateItem(item, cart), "Adding items should cause total amount to exceed the allowed limit.");
    }

    @Test
    public void testValidateItem_TotalAmountValidWithDiscount() {
        BigDecimal highPrice = Config.MAX_TOTAL_AMOUNT.divide(BigDecimal.valueOf(2), RoundingMode.DOWN);
        cart.addItem(new DefaultItem(1, Category.FURNITURE.getId(), 2001, highPrice, 2));
        IItem item = new DefaultItem(2, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(50000.00), 1);
        assertDoesNotThrow(() -> validator.validateItem(item, cart), "Adding items causes total amount to exceed the allowed limit.");
    }

    @Test
    public void testValidateMaxUniqueItems() {
        for (int i = 0; i < Config.MAX_UNIQUE_ITEMS - 1; i++) {
            cart.addItem(new DefaultItem(i, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100.0), 1));
        }
        IItem item = new DefaultItem(Config.MAX_UNIQUE_ITEMS, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100.0), 1);
        assertDoesNotThrow(() -> validator.validateItem(item, cart));
    }

    @Test
    public void testValidateItem_UniqueItemCountInvalid() {
        for (int i = 0; i < Config.MAX_UNIQUE_ITEMS; i++) {
            cart.addItem(new DefaultItem(i, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100.0), 1));
        }
        IItem item = new DefaultItem(Config.MAX_UNIQUE_ITEMS + 1, Category.FURNITURE.getId(), 2001, BigDecimal.valueOf(100.0), 1);
        assertThrows(ItemValidationException.class, () -> validator.validateItem(item, cart), "Adding this item causes unique item count to exceed the allowed limit.");
    }
}
