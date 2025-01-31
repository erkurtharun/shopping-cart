package com.shoppingcart.io;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JSONFileOperations implements FileOperations {

    @Override
    public List<JSONObject> readCommandsFromFile(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONArray jsonArray = new JSONArray(content);
        List<JSONObject> jsonObjects = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObjects.add(jsonArray.getJSONObject(i));
        }

        return jsonObjects;
    }

    @Override
    public void writeResponsesToFile(List<JSONObject> responses, String filePath) throws IOException {
        JSONArray jsonArray = new JSONArray(responses);
        Files.write(Paths.get(filePath), jsonArray.toString(4).getBytes());
    }
}
