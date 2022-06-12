package com.example.snakegame;

import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        Group root = new Group();
        Scene scene = new Scene(root, Color.BLACK);
//        Image icon = new Image("src/img.png");
//        stage.getIcons().add(icon);
        stage.setWidth(600);
        stage.setHeight(600);
        stage.setTitle("Snake Game");
        stage.setResizable(false);
        

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}