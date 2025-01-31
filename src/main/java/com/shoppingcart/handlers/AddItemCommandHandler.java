package com.shoppingcart.handlers;

import com.shoppingcart.cart.ICart;
import com.shoppingcart.factories.ItemFactory;
import com.shoppingcart.models.IItem;
import org.json.JSONObject;

import java.math.BigDecimal;

public class AddItemCommandHandler implements ICommandHandler {

    private final ICart cart;

    public AddItemCommandHandler(ICart cart) {
        this.cart = cart;
    }

    @Override
    public JSONObject handleCommand(JSONObject command) {
        JSONObject response = new JSONObject();
        JSONObject payload = command.getJSONObject("payload");

        int itemId = payload.getInt("itemId");
        int categoryId = payload.getInt("categoryId");
        int sellerId = payload.getInt("sellerId");
        BigDecimal price = payload.getBigDecimal("price");
        int quantity = payload.getInt("quantity");

        IItem item = ItemFactory.getInstance().createItem(itemId, categoryId, sellerId, price, quantity);
        cart.addItem(item);
        response.put("result", true);
        response.put("message", "Item added successfully");
        return response;
    }
}
