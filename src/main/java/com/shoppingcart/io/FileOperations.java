package com.shoppingcart.io;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public interface FileOperations {

    List<JSONObject> readCommandsFromFile(String filePath) throws IOException;

    void writeResponsesToFile(List<JSONObject> responses, String filePath) throws IOException;
}
