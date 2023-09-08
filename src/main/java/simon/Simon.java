package simon;

import java.util.Scanner;

import simon.command.Parser;
import simon.task.Task;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * The main class for the {@code Simon} application.
 * It handles the primary loop of the application and interacts with the user.
 */
public class Simon extends Application {

    /** List of tasks maintained by the {@code Simon} application. */
    private TaskList tasks;

    /** Storage handler for saving and loading tasks from a file. */
    private final Storage storage;

    /** UI handler for displaying messages and prompts to the user. */
    private final Ui ui;

    /** Horizontal line for UI formatting. */
    protected static final String SPACE = "____________________________________________________________";

    /** Horizontal line with newline prefix for UI formatting. */
    protected static final String NSPACE = "\n____________________________________________________________";

    /** Horizontal line with newline suffix for UI formatting. */
    protected static final String SPACEN = "____________________________________________________________\n";
    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;
    private Image user = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image duke = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    public Simon() {
        this("data/simon.txt");  // use default filePath
    }

    /**
     * Constructs a new {@code Simon} application instance.
     *
     * @param filePath Path to the file where tasks will be saved and loaded from.
     */
    public Simon(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (SimonException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /**
     * Starts and runs the main loop of the {@code Simon} application.
     * Continues to prompt the user for commands until the 'bye' command is given.
     */
    public void run() {
        Scanner scan = new Scanner(System.in);
        ui.showWelcome();

        while (true) {
            String inData = scan.nextLine();
            Parser.Command command = Parser.parseCommand(inData.split(" ")[0]);

            try {
                switch (command) {
                    case LIST:
                        ui.listTasks(tasks);
                        break;
                    case TODO:
                    case DEADLINE:
                    case EVENT:
                        Task newTask = Parser.parseAddTask(inData, command);
                        tasks.addTask(newTask);
                        storage.save(tasks.getAllTasks());
                        ui.showAddedTask(newTask, tasks.getTaskCount());
                        break;
                    case UNMARK:
                        Task unmarkedTask = tasks.markTask(inData, false);
                        storage.save(tasks.getAllTasks());
                        ui.showMarkedTask(false, unmarkedTask);
                        break;
                    case MARK:
                        Task markedTask = tasks.markTask(inData, true);
                        storage.save(tasks.getAllTasks());
                        ui.showMarkedTask(true, markedTask);
                        break;
                    case DELETE:
                        Task deletedTask = tasks.deleteTask(inData);
                        storage.save(tasks.getAllTasks());
                        ui.showDeletedTask(deletedTask, tasks.getTaskCount());
                        break;
                    case FIND:
                        TaskList matchedTasks = tasks.findTasks(inData);
                        ui.showMatchingTasks(matchedTasks);
                        break;
                    case BYE:
                        ui.showGoodbye();
                        return;
                    case UNKNOWN:
                    default:
                        ui.showUnknownCommand();
                }
            } catch (SimonException se) {
                ui.showError(se.getMessage());
            }
        }
    }

    /**
     * The main entry point for the {@code Simon} application.
     *
     * @param args Command line arguments. Not used.
     */
    public static void main(String[] args) {
        new Simon("data/simon.txt").run();
    }

    @Override
    public void start(Stage stage) {
        //Step 1. Setting up required components

        //The container for the content of the chat to scroll.
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);

        userInput = new TextField();
        sendButton = new Button("Send");

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);

        scene = new Scene(mainLayout);

        stage.setScene(scene);
        stage.show();

        //Step 2. Formatting the window to look as expected
        stage.setTitle("Simon");
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

        //Part 3. Add functionality to handle user input.
        sendButton.setOnMouseClicked((event) -> {
            handleUserInput();
        });

        userInput.setOnAction((event) -> {
            handleUserInput();
        });

        //Scroll down to the end every time dialogContainer's height changes.
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

        // more code to be added here later
    }

    private void handleUserInput() {
        Label userText = new Label(userInput.getText());
        Label dukeText = new Label(getResponse(userInput.getText()));

        DialogBox userDialog = DialogBox.getUserDialog(userText, new ImageView(user));
        DialogBox dukeDialog = DialogBox.getDukeDialog(dukeText, new ImageView(duke));

        // Add padding between DialogBoxes
        VBox.setMargin(userDialog, new Insets(0, 0, 10, 0));  // 10 units of padding at the bottom
        VBox.setMargin(dukeDialog, new Insets(0, 0, 10, 0));  // 10 units of padding at the bottom

        dialogContainer.getChildren().addAll(userDialog, dukeDialog);
        userInput.clear();
    }

    /**
     * You should have your own function to generate a response to user input.
     * Replace this stub with your completed method.
     */
    String getResponse(String input) {
        return "Duke heard: " + input;
    }
}