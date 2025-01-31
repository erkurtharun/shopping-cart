package com.shoppingcart.io;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JSONFileOperationsUnitTest {

    private JSONFileOperations fileOperations;

    @BeforeEach
    public void setUp() {
        fileOperations = new JSONFileOperations();
    }

    @Test
    public void testReadCommandsFromFile() throws IOException {
        String testFilePath = "test_input.json";
        String fileContent = "[{\"command\":\"addItem\",\"payload\":{\"itemId\":1}}]";
        Path path = Paths.get(testFilePath);
        Files.write(path, fileContent.getBytes());

        List<JSONObject> commands = fileOperations.readCommandsFromFile(testFilePath);

        assertNotNull(commands);
        assertEquals(1, commands.size());
        assertEquals("addItem", commands.getFirst().getString("command"));

        Files.delete(path);
    }

    @Test
    public void testWriteResponsesToFile() throws IOException {
        String testFilePath = "test_output.json";
        List<JSONObject> responses = List.of(
                new JSONObject().put("result", true).put("message", "Success")
        );

        fileOperations.writeResponsesToFile(responses, testFilePath);

        Path path = Paths.get(testFilePath);
        String fileContent = new String(Files.readAllBytes(path));
        JSONArray jsonArray = new JSONArray(fileContent);

        assertEquals(1, jsonArray.length());
        assertTrue(jsonArray.getJSONObject(0).getBoolean("result"));

        Files.delete(path);
    }
}
