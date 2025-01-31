package com.shoppingcart;

import com.shoppingcart.cart.Cart;
import com.shoppingcart.handlers.*;
import com.shoppingcart.io.FileOperations;
import com.shoppingcart.io.JSONFileOperations;
import com.shoppingcart.promotions.CategoryPromotion;
import com.shoppingcart.promotions.PromotionService;
import com.shoppingcart.promotions.SameSellerPromotion;
import com.shoppingcart.promotions.TotalPricePromotion;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static FileOperations fileOperations = new JSONFileOperations();

    public static void main(String[] args) {
        processCommands(args);
    }

    static void setFileOperations(FileOperations fileOperations) {
        Main.fileOperations = fileOperations;
    }

    static void processCommands(String[] args) {
        if (args.length < 2) {
            logger.error("Usage: java com.shoppingcart.Main <input_file> <output_file>");
            return;
        }

        String inputFilePath = args[0];
        String outputFilePath = args[1];

        try {
            CommandProcessor processor = createCommandProcessor();

            // Read commands from input file
            List<JSONObject> commands = fileOperations.readCommandsFromFile(inputFilePath);
            logger.info("Commands read from input file: {}", commands);

            // Process commands and collect responses
            List<JSONObject> responses = commands.stream()
                    .map(processor::processCommand)
                    .collect(Collectors.toList());
            logger.info("Responses generated: {}", responses);

            // Write responses to output file
            fileOperations.writeResponsesToFile(responses, outputFilePath);
            logger.info("Responses written to output file: {}", outputFilePath);

            logger.info("Commands processed successfully. Check the output file for results.");
        } catch (NoSuchFileException e) {
            logger.error("Input file not found: {}", inputFilePath, e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("IO Exception occurred: ", e);
            throw new RuntimeException(e);
        }
    }

    private static CommandProcessor createCommandProcessor() {
        PromotionService promotionService = new PromotionService(Arrays.asList(
                new SameSellerPromotion(),
                new CategoryPromotion(),
                new TotalPricePromotion()
        ));
        Cart cart = new Cart(promotionService);

        // Create command handlers
        AddItemCommandHandler addItemHandler = new AddItemCommandHandler(cart);
        RemoveItemCommandHandler removeItemHandler = new RemoveItemCommandHandler(cart);
        ResetCartCommandHandler resetCartHandler = new ResetCartCommandHandler(cart);
        DisplayCartCommandHandler displayCartHandler = new DisplayCartCommandHandler(cart);
        AddVasItemToItemCommandHandler addVasItemHandler = new AddVasItemToItemCommandHandler(cart);

        // Create and configure command processor
        CommandProcessor processor = new CommandProcessor();
        processor.registerHandler("addItem", addItemHandler);
        processor.registerHandler("removeItem", removeItemHandler);
        processor.registerHandler("resetCart", resetCartHandler);
        processor.registerHandler("displayCart", displayCartHandler);
        processor.registerHandler("addVasItemToItem", addVasItemHandler);

        return processor;
    }
}
