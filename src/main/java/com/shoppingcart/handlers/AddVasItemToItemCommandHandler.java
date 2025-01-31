package com.shoppingcart.handlers;

import com.shoppingcart.cart.ICart;
import com.shoppingcart.models.VasItem;
import org.json.JSONObject;

import java.math.BigDecimal;

public class AddVasItemToItemCommandHandler implements ICommandHandler {

    private final ICart cart;

    public AddVasItemToItemCommandHandler(ICart cart) {
        this.cart = cart;
    }

    @Override
    public JSONObject handleCommand(JSONObject command) {
        JSONObject response = new JSONObject();

        JSONObject payload = command.getJSONObject("payload");

        int parentItemId = payload.getInt("itemId");
        int vasItemId = payload.getInt("vasItemId");
        int vasCategoryId = payload.getInt("vasCategoryId");
        int vasSellerId = payload.getInt("vasSellerId");
        BigDecimal price = payload.getBigDecimal("price");
        int quantity = payload.getInt("quantity");

        VasItem vasItem = new VasItem(parentItemId, vasItemId, vasCategoryId, vasSellerId, price, quantity);

        cart.addVasItem(vasItem);
        response.put("result", true);
        response.put("message", "VAS item added successfully");

        return response;
    }
}
