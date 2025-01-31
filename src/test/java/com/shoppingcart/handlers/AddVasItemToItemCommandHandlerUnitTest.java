package com.shoppingcart.handlers;

import com.shoppingcart.cart.ICart;
import com.shoppingcart.models.DefaultItem;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class AddVasItemToItemCommandHandlerUnitTest {

    private ICart cart;
    private AddVasItemToItemCommandHandler handler;

    @BeforeEach
    public void setUp() {
        cart = mock(ICart.class); // Mock ICart interface
        handler = new AddVasItemToItemCommandHandler(cart);
    }

    @Test
    public void testHandleCommand_Success() {
        DefaultItem parentItem = new DefaultItem(1, 1001, 2001, BigDecimal.valueOf(100.0), 1);
        cart.addItem(parentItem);

        JSONObject command = new JSONObject();
        JSONObject payload = new JSONObject();
        payload.put("itemId", 1);
        payload.put("vasItemId", 2);
        payload.put("vasCategoryId", 3242);
        payload.put("vasSellerId", 5003);
        payload.put("price", BigDecimal.valueOf(20.0));
        payload.put("quantity", 1);
        command.put("payload", payload);

        JSONObject response = handler.handleCommand(command);
        assertTrue(response.getBoolean("result"));
        assertEquals("VAS item added successfully", response.getString("message"));
    }
}
