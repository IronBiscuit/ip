package duke;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The duke.Duke class that runs the duke.Duke task manager program
 */
public class Duke extends Application {

    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;
    private Image user = new Image(this.getClass().getResourceAsStream("/images/stark.png"));
    private Image duke = new Image(this.getClass().getResourceAsStream("/images/jarvis.png"));
    private Storage storage;
    private Parser parser;



    public Duke() {
        storage = new Storage("src/main/data/", "src/main/data/data.txt");
        try {
            storage.processData();
        } catch (java.io.IOException ignored) {
            /* Exceptions are ignored */
        }
        parser = new Parser(storage.getData());
    }

    public void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                System.err.println(e);
            }
        }).start();
    }

    /**
     * Iteration 2:
     * Creates two dialog boxes, one echoing user input and the other containing duke.Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    private void handleUserInput() {
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userInput.getText(), user),
                DialogBox.getDukeDialog(getResponse(userInput.getText()), duke)
        );
        userInput.clear();
    }

    public String getResponse(String input) {
        String outputMessage;
        try {
            outputMessage = parser.parse(input);
            if (!parser.shouldContinueDuke()) {
                ArrayList<String> finalLines = parser.finalizedLines();
                storage.saveData(finalLines);
                setTimeout(() -> Platform.exit(), 1500);
            }
        } catch (DukeException e) {
            outputMessage = Ui.handleDukeException(e);
        } catch (IOException ignored) {
            /* Exceptions are ignored as they will never be thrown */
            outputMessage = "";
        }
        return outputMessage;
    }

    public void setIntro() {
        dialogContainer = new VBox();
        dialogContainer.getChildren().add(DialogBox.getDukeDialog(Ui.introduction(), duke));
    }

    /**
     *
     * @param stage
     */
    public void start(Stage stage) {
        try {
            storage.processData();
        } catch (java.io.IOException ignored) {
            /* Exceptions are ignored */
        }

        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        dialogContainer.getChildren().add(DialogBox.getDukeDialog(Ui.introduction(), duke));
        System.out.println("reached here");
        scrollPane.setContent(dialogContainer);
        userInput = new TextField();
        sendButton = new Button("Send");
        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);
        scene = new Scene(mainLayout);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("duke.Duke");
        stage.setResizable(false);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);

        mainLayout.setPrefSize(400.0, 600.0);

        scrollPane.setPrefSize(385, 535);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);

        // You will need to import `javafx.scene.layout.Region` for this.
        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);

        userInput.setPrefWidth(325.0);

        sendButton.setPrefWidth(55.0);

        AnchorPane.setTopAnchor(scrollPane, 1.0);

        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);

        AnchorPane.setLeftAnchor(userInput , 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);
        sendButton.setOnMouseClicked((event) -> {
            handleUserInput();
        });

        userInput.setOnAction((event) -> {
            handleUserInput();
        });

        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));
    }
}
