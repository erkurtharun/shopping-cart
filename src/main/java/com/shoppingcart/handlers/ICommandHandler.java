package com.shoppingcart.handlers;

import org.json.JSONObject;

public interface ICommandHandler {
    JSONObject handleCommand(JSONObject command);
}
