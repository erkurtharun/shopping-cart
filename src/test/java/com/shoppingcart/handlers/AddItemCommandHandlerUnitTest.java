package com.shoppingcart.handlers;

import com.shoppingcart.cart.Cart;
import com.shoppingcart.cart.ICart;
import com.shoppingcart.promotions.CategoryPromotion;
import com.shoppingcart.promotions.PromotionService;
import com.shoppingcart.promotions.SameSellerPromotion;
import com.shoppingcart.promotions.TotalPricePromotion;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class AddItemCommandHandlerUnitTest {

    private ICart cart;
    private AddItemCommandHandler handler;

    @BeforeEach
    public void setUp() {
        PromotionService promotionService = new PromotionService(Arrays.asList(
                new SameSellerPromotion(),
                new CategoryPromotion(),
                new TotalPricePromotion()
        ));
        cart = new Cart(promotionService);
        handler = new AddItemCommandHandler(cart);
    }

    @Test
    public void testHandleCommand_Success() {
        JSONObject command = new JSONObject();
        JSONObject payload = new JSONObject();
        payload.put("itemId", 1);
        payload.put("categoryId", 1001);
        payload.put("sellerId", 2001);
        payload.put("price", BigDecimal.valueOf(100.0));
        payload.put("quantity", 1);
        command.put("payload", payload);

        JSONObject response = handler.handleCommand(command);
        assertTrue(response.getBoolean("result"));
        assertEquals("Item added successfully", response.getString("message"));
        assertEquals(1, cart.getItems().size());
    }
}
