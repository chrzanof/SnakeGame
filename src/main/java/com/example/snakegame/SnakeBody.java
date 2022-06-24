package com.example.snakegame;

import java.util.ArrayList;
import java.util.List;

public class SnakeBody {
    private int numberOfApplesEaten;
    private List<Block> body;
    boolean moved;
    private int timeInSeconds;

    public SnakeBody() {
        this.numberOfApplesEaten = 0;
        this.body = new ArrayList<>();
        this.moved = false;
    }

    public SnakeBody(Block head) {
        this.numberOfApplesEaten = 0;
        this.body = new ArrayList<>();
        this.body.add(head);
        this.moved = false;
    }

    public int getTimeInSeconds() {
        return timeInSeconds;
    }

    public void setTimeInSeconds(int timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }

    public void add(Block block) {
        this.body.add(block);
    }

    public int getNumberOfApplesEaten() {
        return numberOfApplesEaten;
    }

    public void setNumberOfApplesEaten(int numberOfApplesEaten) {
        this.numberOfApplesEaten = numberOfApplesEaten;
    }

    public List<Block> getBody() {
        return body;
    }

    public void setBody(List<Block> body) {
        this.body = body;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }
}
