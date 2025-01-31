package com.shoppingcart.handlers;

import com.shoppingcart.exceptions.ItemNotFoundException;
import com.shoppingcart.exceptions.ItemValidationException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Processor for handling commands and dispatching them to appropriate handlers.
 */
public class CommandProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CommandProcessor.class);
    private final Map<String, ICommandHandler> handlers = new HashMap<>();

    /**
     * Registers a handler for a specific command type.
     *
     * @param commandType The type of command.
     * @param handler     The handler to register.
     */
    public void registerHandler(String commandType, ICommandHandler handler) {
        handlers.put(commandType, handler);
    }

    /**
     * Processes a command and dispatches it to the appropriate handler.
     *
     * @param command The command to process.
     * @return The response as a JSON object.
     */
    public JSONObject processCommand(JSONObject command) {
        String commandType = command.getString("command");
        ICommandHandler handler = handlers.get(commandType);
        JSONObject response = new JSONObject();
        try {
            if (handler != null) {
                response = handler.handleCommand(command);
            } else {
                logger.warn("Unknown command: {}", commandType);
                response.put("result", false);
                response.put("message", "Unknown command: " + commandType);
            }
        } catch (JSONException e) {
            response.put("result", false);
            response.put("message", "JSON parsing error: " + e.getMessage());
        } catch (ItemValidationException e) {
            response.put("result", false);
            response.put("message", "Item validation failed: " + e.getMessage());
        } catch (ItemNotFoundException e) {
            response.put("result", false);
            response.put("message", "Parent item not found: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing command: {}", commandType, e);
            response.put("result", false);
            response.put("message", "Error processing command: " + e.getMessage());
        }
        return response;
    }
}
