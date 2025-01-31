package com.shoppingcart.handlers;

import com.shoppingcart.cart.ICart;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DisplayCartCommandHandlerUnitTest {

    private ICart cart;
    private DisplayCartCommandHandler handler;

    @BeforeEach
    public void setUp() {
        cart = mock(ICart.class);
        handler = new DisplayCartCommandHandler(cart);
    }

    @Test
    public void shouldReturnCartDetails_WhenCartHasItems() {
        // ✅ GIVEN
        JSONObject message = new JSONObject();
        message.put("items", new JSONArray());
        message.put("totalAmount", 100.0);
        message.put("appliedPromotionId", 1);
        message.put("totalDiscount", 10.0);

        JSONObject expectedCartJson = new JSONObject();
        expectedCartJson.put("result", true);
        expectedCartJson.put("message", message);

        when(cart.display()).thenReturn(expectedCartJson);

        // ✅ WHEN
        JSONObject command = new JSONObject();
        JSONObject actualResponse = handler.handleCommand(command);

        // ✅ THEN
        assertTrue(actualResponse.getBoolean("result"));
        assertEquals(message.toString(), actualResponse.getJSONObject("message").toString());
        verify(cart, times(1)).display();
    }

    @Test
    public void shouldReturnEmptyCartDetails_WhenCartIsEmpty() {
        // ✅ GIVEN
        JSONObject message = new JSONObject();
        message.put("items", new JSONArray());
        message.put("totalAmount", 0.0);
        message.put("appliedPromotionId", -1);
        message.put("totalDiscount", 0.0);

        JSONObject expectedEmptyCartJson = new JSONObject();
        expectedEmptyCartJson.put("result", true);
        expectedEmptyCartJson.put("message", message);

        when(cart.display()).thenReturn(expectedEmptyCartJson);

        // ✅ WHEN
        JSONObject command = new JSONObject();
        JSONObject actualResponse = handler.handleCommand(command);

        // ✅ THEN
        assertTrue(actualResponse.getBoolean("result"));
        assertEquals(message.toString(), actualResponse.getJSONObject("message").toString());
        verify(cart, times(1)).display();
    }
}