package com.example.snakegame;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Controller {
    public static final int DEFAULT_WIDTH = 600;
    public static final int DEFAULT_HEIGHT = 600;
    private SnakeBody snake;
    private Block apple;
    private int size;
    private int rows;
    private int columns;
    private int width;
    private int height;

    public Controller() {
        this.size = 25;
        this.rows = 24;
        this.columns = 24;
        this.width = size * rows;
        this.height = size * columns;
    }

    public Controller(SnakeBody snake, Block apple) {
        this.size = 25;
        this.snake = snake;
        this.apple = apple;
        this.rows = 24;
        this.columns = 24;
        this.width = size * rows;
        this.height = size * columns;
    }

    public void writeToCSV(String string) {
        FileWriter writer = null;
        try {
            writer = new FileWriter("save.csv",true);

            writer.append(snake.getNumberOfApplesEaten() + "," + string + "\n");


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<List<String>> readFromCSV() {
        List<List<String>> list = new ArrayList<>();
        String line = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("save.csv"));
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                List<String> subList = Arrays.asList(row);
                list.add(subList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e){
                e.printStackTrace();
            }
            return list;
        }
    }

    public boolean checkIfAppleEaten() {
        Block head = snake.getBody().get(0);
        if(head.getPositionX() == apple.getPositionX() * size && head.getPositionY() == apple.getPositionY() * size){
            snake.setNumberOfApplesEaten(snake.getNumberOfApplesEaten() + 1);
            newRandomApplePosition();
            Block block = new Block();
            block.setColor(Color.LIMEGREEN);
            block.setPositionX(snake.getBody().get(snake.getBody().size()-1).getPositionX());
            block.setPositionY(snake.getBody().get(snake.getBody().size()-1).getPositionY());
            snake.add(block);
            return true;
        }
        return false;
    }

    public boolean checkIsRunning() {
        Block head = snake.getBody().get(0);
        if (head.getPositionX() >= width)
            return false;
        if (head.getPositionX() < 0)
            return false;
        if(head.getPositionY() >= height)
            return false;
        if(head.getPositionY() < 0)
            return false;
        if(snake.getBody().size() > 1) {
            for (int i = 1; i < snake.getBody().size() ; i++) {
                Block part = snake.getBody().get(i);
                if(part.getPositionX() == head.getPositionX() && part.getPositionY() == head.getPositionY())
                    return false;
            }
        }
        return true;
    }

    public enum Direction {
        UP,DOWN,RIGHT,LEFT
    }
    public Direction direction = Direction.RIGHT;

    public void keyBindings(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (!snake.isMoved())
                return;

            switch (event.getCode()) {
                case UP -> {
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

            snake.setMoved(false);
        });
    }

    public void move() {
        for (int i = snake.getBody().size()-1; i > 0; i--) {
            snake.getBody().get(i).setPositionX(snake.getBody().get(i-1).getPositionX());
            snake.getBody().get(i).setPositionY(snake.getBody().get(i-1).getPositionY());
        }
        switch (direction) {
            case UP -> snake.getBody().get(0).setPositionY(snake.getBody().get(0).getPositionY() - size);
            case DOWN -> snake.getBody().get(0).setPositionY(snake.getBody().get(0).getPositionY() + size);
            case LEFT -> snake.getBody().get(0).setPositionX(snake.getBody().get(0).getPositionX() - size);
            case RIGHT -> snake.getBody().get(0).setPositionX(snake.getBody().get(0).getPositionX() + size);
        }
    }


    public void newRandomApplePosition() {
        do {
            Random random = new Random();
            apple.setPositionX(random.nextInt(width / size));
            apple.setPositionY(random.nextInt(height / size));
            apple.setColor(Color.RED);
        } while (isAppleOnSnakeBody());
    }
    public boolean isAppleOnSnakeBody() {
        for (Block b :
                snake.getBody()) {
            if (b.getPositionX() == apple.getPositionX() * size && b.getPositionY() == apple.getPositionY() * size) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public SnakeBody getSnake() {
        return snake;
    }

    public void setSnake(SnakeBody snake) {
        this.snake = snake;
    }

    public Block getApple() {
        return apple;
    }

    public void setApple(Block apple) {
        this.apple = apple;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
        this.width = rows * this.size;
    }

    public int getColumns() {
        return columns;

    }

    public void setColumns(int columns) {
        this.columns = columns;
        this.height = columns * this.size;
    }
}