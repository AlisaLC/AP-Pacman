package edu.sharif.ce.appacman.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.sharif.ce.appacman.model.Point;
import edu.sharif.ce.appacman.view.menu.MapMakerMenu;

public class RandomMazeGenerator {
    private final char ENTRY_POINT_UP = 'U';
    private final char ENTRY_POINT_DOWN = 'D';
    private final char ENTRY_POINT_RIGHT = 'R';
    private final char ENTRY_POINT_LEFT = 'L';
    private final int STARTING_X = 0;
    private final int STARTING_Y = 0;
    private List<Point>[][] neighboringPoints;
    private char[][] entryPoint, mazeOutput;
    private boolean[][] isVisited;
    private final int mazeHeight;
    private final int mazeWidth;

    public RandomMazeGenerator(int mapWidth) {
        mazeWidth = (mapWidth - 1) / 2;
        mazeHeight = mazeWidth;
        initializeMaze();
        randomizeMaze();
        makePath(STARTING_X, STARTING_Y);
        fillMazeOutput();
    }

    private void initializeMaze() {
        neighboringPoints = new List[mazeHeight][mazeWidth];
        isVisited = new boolean[mazeHeight][mazeWidth];
        entryPoint = new char[mazeHeight][mazeWidth];
        mazeOutput = new char[2 * mazeHeight + 1][2 * mazeWidth + 1];
        isVisited[STARTING_X][STARTING_Y] = true;
    }

    private void randomizeMaze() {
        for (int i = 0; i < mazeHeight; i++) {
            for (int j = 0; j < mazeWidth; j++) {
                neighboringPoints[i][j] = new ArrayList<>();
                if (i > 0)
                    neighboringPoints[i][j].add(new Point(i - 1, j));
                if (i < mazeHeight - 1)
                    neighboringPoints[i][j].add(new Point(i + 1, j));
                if (j > 0)
                    neighboringPoints[i][j].add(new Point(i, j - 1));
                if (j < mazeWidth - 1)
                    neighboringPoints[i][j].add(new Point(i, j + 1));
                Collections.shuffle(neighboringPoints[i][j]);
            }
        }
    }

    private void makePath(int x, int y) {
        for (Point p : neighboringPoints[x][y]) {
            if (!isVisited[p.getX()][p.getY()]) {
                isVisited[p.getX()][p.getY()] = true;
                if (p.getX() > x)
                    entryPoint[p.getX()][p.getY()] = ENTRY_POINT_UP;
                else if (p.getX() < x)
                    entryPoint[p.getX()][p.getY()] = ENTRY_POINT_DOWN;
                else if (p.getY() > y)
                    entryPoint[p.getX()][p.getY()] = ENTRY_POINT_LEFT;
                else
                    entryPoint[p.getX()][p.getY()] = ENTRY_POINT_RIGHT;
                makePath(p.getX(), p.getY());
            }
        }
    }

    private void fillMazeOutput() {
        prepareMazeOuput();
        fillMazeEntryPoints();
    }

    private void prepareMazeOuput() {
        char MAZE_OUTPUT_BLOCKED = '*';
        for (int i = 0; i < 2 * mazeHeight + 1; i++)
            for (int j = 0; j < 2 * mazeWidth + 1; j++)
                mazeOutput[i][j] = MAZE_OUTPUT_BLOCKED;
    }

    private void fillMazeEntryPoints() {
        for (int i = 1; i < 2 * mazeHeight + 1; i += 2) {
            for (int j = 1; j < 2 * mazeWidth + 1; j += 2) {
                char MAZE_OUTPUT_POINT = ' ';
                mazeOutput[i][j] = MAZE_OUTPUT_POINT;
                char MAZE_OUTPUT_FREE = ' ';
                switch (entryPoint[(i - 1) / 2][(j - 1) / 2]) {
                    case ENTRY_POINT_UP:
                        mazeOutput[i - 1][j] = MAZE_OUTPUT_FREE;
                        break;
                    case ENTRY_POINT_DOWN:
                        mazeOutput[i + 1][j] = MAZE_OUTPUT_FREE;
                        break;
                    case ENTRY_POINT_RIGHT:
                        mazeOutput[i][j + 1] = MAZE_OUTPUT_FREE;
                        break;
                    case ENTRY_POINT_LEFT:
                        mazeOutput[i][j - 1] = MAZE_OUTPUT_FREE;
                }
            }
        }
    }

    public String getOutput() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < 2 * mazeHeight + 1; i++)
            output.append(mazeOutput[i]).append(System.lineSeparator());
        return output.toString();
    }
}