package com.shoppingcart.handlers;

import com.shoppingcart.cart.ICart;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetCartCommandHandler implements ICommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResetCartCommandHandler.class);
    private final ICart cart;

    public ResetCartCommandHandler(ICart cart) {
        this.cart = cart;
    }

    @Override
    public JSONObject handleCommand(JSONObject command) {
        JSONObject response = new JSONObject();
        if (command == null || !command.has("command")) {
            logger.warn("Invalid command structure received: {}", command);
            throw new JSONException("Invalid command structure.");
        }
        if (cart == null) {
            logger.error("Cart instance is null.");
            throw new RuntimeException("Cart is null.");
        }
        cart.reset();
        response.put("result", true);
        response.put("message", "Cart has been reset successfully");
        logger.info("Cart reset successfully.");
        return response;
    }
}
