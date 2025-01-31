package com.shoppingcart.handlers;

import com.shoppingcart.cart.ICart;
import org.json.JSONObject;

public class DisplayCartCommandHandler implements ICommandHandler {

    private final ICart cart;

    public DisplayCartCommandHandler(ICart cart) {
        this.cart = cart;
    }

    @Override
    public JSONObject handleCommand(JSONObject command) {
        JSONObject response = new JSONObject();
        JSONObject cartJson = cart.display();
        response.put("result", true);
        response.put("message", cartJson.getJSONObject("message"));
        return response;
    }
}
