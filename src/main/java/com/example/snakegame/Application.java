package com.example.snakegame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Application extends javafx.application.Application {
    Group root;
    Scene scene;
    Controller controller;
    private boolean isRunning = false;
    public static List<Rectangle> snakeParts;

    public List<Rectangle> draw() {
       List<Rectangle> snakeParts = new ArrayList<>();
        for (Block block : controller.snake.getBody()) {
            Rectangle rec = new Rectangle();
            rec.setFill(block.color);
            rec.setX(block.getPositionX());
            rec.setY(block.getPositionY());
            rec.setHeight(controller.size);
            rec.setWidth(controller.size);
            snakeParts.add(rec);
        }
        return snakeParts;
    }


    @Override
    public void start(Stage stage) throws IOException {
        //Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        root = new Group();
        scene = new Scene(root, Color.BLACK);
        Pane pane = new Pane();

        Block snakeHead = new Block(controller.width/2-controller.size, controller.height/2- controller.size,Color.GREEN);
        controller = new Controller(new SnakeBody(snakeHead), new Block());
        stage.setWidth(controller.width);
        stage.setHeight(controller.height);
        stage.setResizable(true);
        stage.sizeToScene();
        for (int j = 0; j < controller.height/controller.size; j++) {
            Line lineV = new Line(j*controller.size,0,j*controller.size,controller.height);
            lineV.setStroke(Color.WHITE);
            Line lineH = new Line(0,j*controller.size,controller.width,j*controller.size);
            lineH.setStroke(Color.WHITE);
            root.getChildren().add(lineV);
            root.getChildren().add(lineH);
        }

        stage.setTitle("Snake Game");
        stage.setScene(scene);


        Timeline timeline = new Timeline();
        controller.newRandomApplePosition();
        isRunning = true;
        controller.keyBindings(scene);
        Rectangle apple = new Rectangle();
        apple.setWidth(controller.size);
        apple.setHeight(controller.size);
        apple.setX(controller.apple.getPositionX() * controller.size);
        apple.setY(controller.apple.getPositionY() * controller.size);
        apple.setFill(controller.apple.color);
        snakeParts = draw();
        KeyFrame frame = new KeyFrame(Duration.seconds(0.2), actionEvent -> {
            if(!isRunning) {
                System.out.println("zatrzymano");
                return;
            }
            controller.move();
            controller.snake.setMoved(true);
            int count = 0;
            //snake draw
            for (Rectangle part: snakeParts) {
                part.setY(controller.snake.getBody().get(count).getPositionY());
                part.setX(controller.snake.getBody().get(count).getPositionX());
                count++;
            }
            //apple draw
            apple.setX(controller.apple.getPositionX() * controller.size);
            apple.setY(controller.apple.getPositionY() * controller.size);

            isRunning = controller.checkIsRunning();
            boolean eaten = controller.checkIfAppleEaten();
            if (eaten) {
                Rectangle newPart = new Rectangle();
                newPart.setY(controller.snake.getBody().get(controller.snake.getBody().size()-1).getPositionY());
                newPart.setX(controller.snake.getBody().get(controller.snake.getBody().size()-1).getPositionX());
                newPart.setFill(controller.snake.getBody().get(controller.snake.getBody().size()-1).getColor());
                snakeParts.add(newPart);
                root.getChildren().add(newPart);
                System.out.println("dodano nowy element");
            }
        });

        timeline.getKeyFrames().addAll(frame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        root.getChildren().add(apple);
        root.getChildren().addAll(snakeParts);
        stage.show();
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}