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
import java.util.List;

public class Application extends javafx.application.Application {
    Stage window;
    Stage additionalWindow;
    private Group root;
    private Scene scene;
    private Canvas canvas;
    private GraphicsContext gc;
    private Controller controller;
    private boolean isRunning = false;
    Timeline timeline;


    @Override
    public void start(Stage stage) throws IOException {
        window = stage;
        additionalWindow = new Stage();
        stage.setTitle("Snake");
        controller = new Controller(new SnakeBody(), new Block());
        initMenu(window);

        window.show();
        System.out.println("Koniec funkcji start()");
    }
    private void initSettings(Stage stage, String errorMsg) {
        additionalWindow.setResizable(false);
        additionalWindow.sizeToScene();
        StackPane pane = new StackPane();
        pane.setPrefSize(100,100);
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        Button btn1 = new Button();
        btn1.setText("Start Game");
        TextField rows = new TextField();
        Label label = new Label("x");
        label.setAlignment(Pos.CENTER);
        TextField columns = new TextField();
        rows.setText("24");
        columns.setText("24");
        Label errorLabel = new Label(errorMsg);
        errorLabel.setTextFill(Color.RED);
        vBox.getChildren().addAll(errorLabel,rows,label,columns,btn1);
        pane.getChildren().addAll(vBox);
        pane.setAlignment(Pos.CENTER);
        Scene settingsScene = new Scene(pane);
        stage.setScene(settingsScene);
        stage.show();
        btn1.setOnMouseClicked(e -> {
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
        StackPane pane = new StackPane();
        pane.setPrefSize(Controller.DEFAULT_WIDTH,Controller.DEFAULT_HEIGHT);
        Rectangle rectangle = new Rectangle(0,0,Controller.DEFAULT_WIDTH,Controller.DEFAULT_HEIGHT);
        rectangle.setFill(Color.BLACK);
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        Button btn1 = new Button();
        btn1.setText("New Game");
        vBox.getChildren().add(btn1);
        Button btn2 = new Button();
        btn2.setText("High scores");
        vBox.getChildren().add(btn2);
        Button btn3 = new Button();
        btn3.setText("Exit");
        vBox.getChildren().add(btn3);
        pane.getChildren().addAll(rectangle,vBox);
        pane.setAlignment(Pos.CENTER);
        Scene menuScene = new Scene(pane);
        stage.setScene(menuScene);


        btn1.setOnMouseClicked(e ->
            initSettings(additionalWindow, ""));
        btn2.setOnMouseClicked(e -> initHighScores(window));
        btn3.setOnMouseClicked(e -> window.close());
    }
    private void initHighScores(Stage stage) {

    }
    private void stopGame() {
        isRunning = false;
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
        gameOverText.setStroke(Color.RED);
        Button exitBtn = new Button("Exit");
        Button menuBtn = new Button("Main Manu");
        VBox vBox = new VBox();
        vBox.getChildren().addAll(gameOverText, menuBtn, exitBtn);
        pane.getChildren().addAll(rectangle, vBox);
        Scene gameOverScene = new Scene(pane);
        pane.setAlignment(Pos.CENTER);
        stage.setScene(gameOverScene);

        exitBtn.setOnMouseClicked(e -> window.close());
        menuBtn.setOnMouseClicked(e-> initMenu(window));

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
        isRunning = true;
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
            System.out.println("klatka w timeline");
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        System.out.println("koniec funkcji startGame()");
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