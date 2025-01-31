package com.shoppingcart.handlers;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommandProcessorUnitTest {

    private CommandProcessor processor;
    private ICommandHandler addItemHandler;
    private ICommandHandler removeItemHandler;

    @BeforeEach
    public void setUp() {
        processor = new CommandProcessor();
        addItemHandler = mock(ICommandHandler.class);
        removeItemHandler = mock(ICommandHandler.class);

        processor.registerHandler("addItem", addItemHandler);
        processor.registerHandler("removeItem", removeItemHandler);
    }

    @Test
    public void testProcessCommand_AddItem_Success() {
        JSONObject command = new JSONObject().put("command", "addItem");
        JSONObject expectedResponse = new JSONObject().put("result", true).put("message", "Item added successfully");

        when(addItemHandler.handleCommand(command)).thenReturn(expectedResponse);

        JSONObject response = processor.processCommand(command);
        assertEquals(expectedResponse.toString(), response.toString());
        verify(addItemHandler).handleCommand(command);
    }

    @Test
    public void testProcessCommand_RemoveItem_Success() {
        JSONObject command = new JSONObject().put("command", "removeItem");
        JSONObject expectedResponse = new JSONObject().put("result", true).put("message", "Item removed successfully");

        when(removeItemHandler.handleCommand(command)).thenReturn(expectedResponse);

        JSONObject response = processor.processCommand(command);
        assertEquals(expectedResponse.toString(), response.toString());
        verify(removeItemHandler).handleCommand(command);
    }

    @Test
    public void testProcessCommand_UnknownCommand() {
        JSONObject command = new JSONObject().put("command", "unknownCommand");

        JSONObject response = processor.processCommand(command);
        assertFalse(response.getBoolean("result"));
        assertEquals("Unknown command: unknownCommand", response.getString("message"));
    }

    @Test
    public void testProcessCommand_HandlerThrowsException() {
        JSONObject command = new JSONObject().put("command", "addItem");
        when(addItemHandler.handleCommand(command)).thenThrow(new RuntimeException("Test exception"));

        JSONObject response = processor.processCommand(command);
        assertFalse(response.getBoolean("result"));
        assertTrue(response.getString("message").contains("Error processing command"));
    }
}
