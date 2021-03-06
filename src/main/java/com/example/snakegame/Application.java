package com.example.snakegame;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Application extends javafx.application.Application {
    private Stage window;
    private Stage additionalWindow;
    private Group root;
    private Scene scene;
    private Canvas canvas;
    private GraphicsContext gc;
    private Controller controller;
    public static boolean isRunning = false;
    private Timeline timeline;
    private Thread timer;
    private Button newGameBtn;
    private Button highScoresBtn;
    private Button exitBtn;
    private Button saveBtn;
    private Button mainMenuBtn;
    private Button startGameBtn;

    @Override
    public void start(Stage stage) throws IOException {
        newGameBtn = new Button("New Game");
        highScoresBtn= new Button("High Scores");
        exitBtn = new Button("Exit");
        saveBtn = new Button("Save");
        mainMenuBtn = new Button("Main menu");
        startGameBtn = new Button("Start Game");
        window = stage;
        additionalWindow = new Stage();
        additionalWindow.setResizable(false);
        additionalWindow.sizeToScene();
        window.setTitle("Snake");
        controller = new Controller(new SnakeBody(), new Block());
        initMenu(window);
        window.show();
    }
    private  void initSave(Stage stage) {
        StackPane pane = new StackPane();
        pane.setPrefSize(200,200);
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        TextField nameField = new TextField();
        Label label = new Label("Name:");
        label.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(label,nameField,saveBtn);
        pane.getChildren().addAll(vBox);
        pane.setAlignment(Pos.CENTER);
        Scene settingsScene = new Scene(pane);
        stage.setScene(settingsScene);
        stage.show();

        saveBtn.setOnMouseClicked(mouseEvent -> {
            controller.writeToCSV(nameField.getText());
            stage.close();
        });
    }
    private void initSettings(Stage stage, String errorMsg) {
        StackPane pane = new StackPane();
        pane.setPrefSize(100,100);
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        TextField rows = new TextField();
        Label label = new Label("x");
        label.setAlignment(Pos.CENTER);
        TextField columns = new TextField();
        rows.setText("24");
        columns.setText("24");
        Label errorLabel = new Label(errorMsg);
        errorLabel.setTextFill(Color.RED);
        vBox.getChildren().addAll(errorLabel,rows,label,columns,startGameBtn);
        pane.getChildren().addAll(vBox);
        pane.setAlignment(Pos.CENTER);
        Scene settingsScene = new Scene(pane);
        stage.setScene(settingsScene);
        stage.show();
        startGameBtn.setOnMouseClicked(e -> {
            try {
                int rowNumber = Integer.parseInt(rows.getText());
                int columnNumber = Integer.parseInt(columns.getText());
                stage.close();
                startGame(window, rowNumber, columnNumber);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                stage.close();
                initSettings(additionalWindow, "Wrong Input!");
            }
                });

    }
    private void initMenu(Stage stage) {
        stage.setTitle("Snake");
        controller.getSnake().setNumberOfApplesEaten(0);
        StackPane pane = new StackPane();
        pane.setPrefSize(Controller.DEFAULT_WIDTH,Controller.DEFAULT_HEIGHT);
        Rectangle rectangle = new Rectangle(0,0,Controller.DEFAULT_WIDTH,Controller.DEFAULT_HEIGHT);
        rectangle.setFill(Color.BLACK);
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(newGameBtn);
        vBox.getChildren().add(highScoresBtn);
        vBox.getChildren().add(exitBtn);
        pane.getChildren().addAll(rectangle,vBox);
        pane.setAlignment(Pos.CENTER);
        Scene menuScene = new Scene(pane);
        stage.setScene(menuScene);
        newGameBtn.setOnMouseClicked(e ->
            initSettings(additionalWindow, ""));
        highScoresBtn.setOnMouseClicked(e -> initHighScores(additionalWindow));
        exitBtn.setOnMouseClicked(e -> window.close());
    }
    private void initHighScores(Stage stage) {
        StackPane pane = new StackPane();
        pane.setPrefSize(200,200);
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        List<List<String>> scores = controller.readFromCSV();
        scores.sort((x, y) -> {
            for (int i = 0; i < Math.min(x.size(), y.size()); i++) {
                if (!x.get(i).equals(y.get(i))) {
                    if(i < 1)
                        return Integer.parseInt(y.get(i))  - Integer.parseInt(x.get(i));
                    else
                        return x.get(i).charAt(0)  - y.get(i).charAt(0);
                }
            }
            return 0;
        });
        scores = controller.removeNameDuplicatesInScoreList(scores);
        int count = 1;
        for (List<String> score :
                scores) {
            Label scoreLabel = new Label( count + "." +score.get(1) + " " + score.get(0));
            scoreLabel.setAlignment(Pos.CENTER);
            vBox.getChildren().add(scoreLabel);
            count++;
        }
        pane.getChildren().addAll(vBox);
        pane.setAlignment(Pos.CENTER);
        Scene settingsScene = new Scene(pane);
        stage.setScene(settingsScene);
        stage.show();
    }
    private void stopGame() {
        isRunning = false;
        timer.stop();
        timeline.stop();
        controller.getSnake().getBody().clear();
    }

    private void stoppingGame() {
        stopGame();
        initGameOverScreen(window);
    }

    private void initGameOverScreen(Stage stage) {
        StackPane pane = new StackPane();
        pane.setPrefSize(Controller.DEFAULT_WIDTH,Controller.DEFAULT_HEIGHT);
        Rectangle rectangle = new Rectangle(0,0,Controller.DEFAULT_WIDTH,Controller.DEFAULT_HEIGHT);
        rectangle.setFill(Color.BLACK);
        Text gameOverText = new Text("Game Over!");
        Text score = new Text("Your Score: " + controller.getSnake().getNumberOfApplesEaten());
        Text time = new Text("Your Time: " + controller.returnSnakeTimeInMinutesAndSeconds());
        gameOverText.setStroke(Color.RED);
        score.setStroke(Color.RED);
        time.setStroke(Color.RED);
        VBox vBox = new VBox();
        vBox.getChildren().addAll(gameOverText,score,time,mainMenuBtn,saveBtn, exitBtn);
        pane.getChildren().addAll(rectangle, vBox);
        Scene gameOverScene = new Scene(pane);
        pane.setAlignment(Pos.CENTER);
        stage.setScene(gameOverScene);
        exitBtn.setOnMouseClicked(e -> window.close());
        mainMenuBtn.setOnMouseClicked(e-> initMenu(window));
        saveBtn.setOnMouseClicked(e -> initSave(additionalWindow));
    }

    private void startGame(Stage stage, int rows, int cols) {
        controller.setRows(rows);
        controller.setColumns(cols);
        root = new Group();
        canvas = new Canvas(controller.getWidth(),controller.getHeight());
        root.getChildren().add(canvas);
        scene = new Scene(root);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setScene(scene);
       // stage.show();
        gc = canvas.getGraphicsContext2D();
        controller.getSnake().add(new Block(controller.getRows()/2 *controller.getSize() - controller.getSize(),
                controller.getColumns()/2 * controller.getSize() - controller.getSize(), Color.GREEN));
        controller.newRandomApplePosition();
        controller.getSnake().getBody().get(0).setColor(Color.GREEN);
        controller.keyBindings(scene);
        controller.getSnake().setTimeInSeconds(0);
        isRunning = true;
         timer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        controller.getSnake().setTimeInSeconds(controller.getSnake().getTimeInSeconds() + 1);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        timeline = new Timeline(new KeyFrame(Duration.millis(150),actionEvent -> {
            if(!isRunning) {
                stoppingGame();
                return;
            }
            stage.setTitle("Score - " + controller.getSnake().getNumberOfApplesEaten());
            controller.move();
            controller.getSnake().setMoved(true);
            isRunning = controller.checkIsRunning();
            controller.checkIfAppleEaten();
            draw();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        timer.start();
    }

    private void draw() {
        drawBackground();
        drawApple();
        drawSnake();
    }

    private void drawApple() {
        gc.setFill(controller.getApple().getColor());
        gc.fillOval(controller.getApple().getPositionX() * controller.getSize(), controller.getApple().getPositionY() * controller.getSize(), controller.getSize(), controller.getSize());
    }

    private void drawBackground() {
        for (int i = 0; i < controller.getWidth()/controller.getSize(); i++) {
            for (int j = 0; j < controller.getHeight()/controller.getSize(); j++) {
                if((i+j) % 2 == 0)
                    gc.setFill(Color.BLACK);
                else
                    gc.setFill(Color.rgb(30,30,30));
                gc.fillRect(i * controller.getSize(), j * controller.getSize(),controller.getSize(), controller.getSize());
            }
        }
    }

    private void drawSnake() {
        for (Block b :
                controller.getSnake().getBody()) {
            gc.setFill(b.getColor());
            gc.fillRect(b.getPositionX(), b.getPositionY(), controller.getSize(), controller.getSize());

        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}