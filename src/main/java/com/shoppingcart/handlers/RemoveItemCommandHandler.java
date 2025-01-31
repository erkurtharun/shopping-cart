package com.shoppingcart.handlers;

import com.shoppingcart.cart.ICart;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveItemCommandHandler implements ICommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(RemoveItemCommandHandler.class);
    private final ICart cart;

    public RemoveItemCommandHandler(ICart cart) {
        this.cart = cart;
    }

    @Override
    public JSONObject handleCommand(JSONObject command) {
        JSONObject response = new JSONObject();

        JSONObject payload = command.getJSONObject("payload");
        int itemId = payload.getInt("itemId");
        logger.info("Attempting to remove item with ID: {}", itemId);

        cart.removeItem(itemId);
        response.put("result", true);
        response.put("message", "Item removed successfully");
        logger.info("Item removed successfully. ID: {}", itemId);
        return response;
    }
}
