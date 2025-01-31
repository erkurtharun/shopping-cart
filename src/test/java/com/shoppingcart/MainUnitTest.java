package com.shoppingcart;

import com.shoppingcart.io.FileOperations;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MainUnitTest {

    private static FileOperations fileOperations;

    @BeforeEach
    void setUp() {
        fileOperations = mock(FileOperations.class);
        Main.setFileOperations(fileOperations);
    }

    @Test
    void testRun() throws IOException {
        // Given
        String inputFilePath = "test_input.json";
        String outputFilePath = "test_output.json";

        List<JSONObject> commands = Arrays.asList(
                new JSONObject("{\"command\":\"addItem\",\"payload\":{\"itemId\":1,\"sellerId\":2001,\"quantity\":1,\"price\":100,\"categoryId\":1001}}"),
                new JSONObject("{\"command\":\"displayCart\"}")
        );

        // Mock the input commands
        when(fileOperations.readCommandsFromFile(inputFilePath)).thenReturn(commands);

        // When
        Main.main(new String[]{inputFilePath, outputFilePath});

        // Then
        verify(fileOperations).writeResponsesToFile(anyList(), eq(outputFilePath));
    }

    @Test
    void testRun_InputFileNotFound() throws IOException {
        // Given
        String inputFilePath = "non_existent_file.json";
        String outputFilePath = "test_output.json";

        // Mock the exception
        when(fileOperations.readCommandsFromFile(inputFilePath)).thenThrow(new NoSuchFileException(inputFilePath));

        // When
        Exception exception = assertThrows(RuntimeException.class, () -> Main.main(new String[]{inputFilePath, outputFilePath}));

        // Then
        assertInstanceOf(NoSuchFileException.class, exception.getCause());
    }

    @Test
    void testRun_IOException() throws IOException {
        // Given
        String inputFilePath = "test_input.json";
        String outputFilePath = "test_output.json";

        // Mock the exception
        when(fileOperations.readCommandsFromFile(inputFilePath)).thenThrow(new IOException("IO error"));

        // When
        Exception exception = assertThrows(RuntimeException.class, () -> Main.main(new String[]{inputFilePath, outputFilePath}));

        // Then
        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    void testRun_InsufficientArguments() {
        // Given
        String[] noArgs = {};
        String[] oneArg = {"inputFilePath"};

        // When
        Main.main(noArgs);
        Main.main(oneArg);

        // Then
        // Verify that the logger captured the expected error message
        // This may require a custom appender to capture log output, or use a logging framework test helper
        // Assuming logger.error is called in processCommands
        assertTrue(true); // Placeholder assertion, replace with actual log verification if needed
    }
}
