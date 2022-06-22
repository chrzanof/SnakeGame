package com.example.snakegame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Application extends javafx.application.Application {
    Group root;
    Scene scene;
    Controller controller;
    static int size = 25;
    static int width = size * 24;
    static int height = size * 24;

    public enum Direction {
        UP,DOWN,RIGHT,LEFT
    }
    private Direction direction = Direction.RIGHT;
    private boolean isRunning = false;
    private boolean moved = false;

    public void keyBindings(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (!moved)
                return;

            switch (event.getCode()) {
                case UP ->{
                    if (direction != Direction.DOWN)
                        direction = Direction.UP;
                }
                case DOWN -> {
                    if (direction != Direction.UP)
                        direction = Direction.DOWN;
                }
                case RIGHT -> {
                    if (direction != Direction.LEFT)
                        direction = Direction.RIGHT;
                }
                case LEFT -> {
                    if (direction != Direction.RIGHT)
                        direction = Direction.LEFT;
                }
            }

            moved = false;
        });
    }

    @Override
    public void start(Stage stage) throws IOException {
        //Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        root = new Group();
        scene = new Scene(root, Color.BLACK);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setResizable(false);
        stage.sizeToScene();
        for (int j = 0; j < height/size; j++) {
            Line lineV = new Line(j*size,0,j*size,height);
            lineV.setStroke(Color.WHITE);
            lineV.setStrokeWidth(0.1);
            Line lineH = new Line(0,j*size,width,j*size);
            lineH.setStroke(Color.WHITE);
            lineH.setStrokeWidth(0.1);
            root.getChildren().add(lineV);
            root.getChildren().add(lineH);
        }

        stage.setTitle("Snake Game");
        stage.setScene(scene);
        Timeline timeline = new Timeline();

        Rectangle rectangle = new Rectangle(width/2 - size,height/2 - size,size,size);
        rectangle.setFill(Color.LIMEGREEN);
        isRunning = true;
        keyBindings(scene);
        KeyFrame frame = new KeyFrame(Duration.seconds(0.2), actionEvent -> {
            if(!isRunning)
                return;
            switch (direction){
                case RIGHT -> rectangle.setTranslateX(rectangle.getTranslateX() + size);
                case LEFT -> rectangle.setTranslateX(rectangle.getTranslateX() - size);
                case DOWN -> rectangle.setTranslateY(rectangle.getTranslateY() + size );
                case UP -> rectangle.setTranslateY(rectangle.getTranslateY() - size);
            }
            moved = true;
        });

        timeline.getKeyFrames().addAll(frame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        root.getChildren().addAll(rectangle);
        stage.show();
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}