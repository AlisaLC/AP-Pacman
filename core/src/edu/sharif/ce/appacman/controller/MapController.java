package edu.sharif.ce.appacman.controller;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.sharif.ce.appacman.model.DatabaseManager;
import edu.sharif.ce.appacman.model.Point;
import edu.sharif.ce.appacman.view.MapMakerMap;
import edu.sharif.ce.appacman.view.menu.MapMakerMenu;
import lombok.Getter;
import lombok.Setter;

public class MapController {

    @Getter
    private static MapController instance;

    static {
        instance = new MapController();
    }

    @Setter
    private int mapWidth;

    public String getRandomMap() {
        String maze = new RandomMazeGenerator(mapWidth).getOutput();
        char[][] points = new char[mapWidth][mapWidth];
        String[] lines = maze.split("\r?\n");
        for (int i = 0; i < lines.length; i++) {
            points[i] = lines[i].toCharArray();
        }
        for (int i = 1; i < (mapWidth - 1); i++) {
            for (int j = 1; j < (mapWidth - 1); j++) {
                if (isDeadEnd(points, i, j)) {
                    removeDeadEnd(points, i, j);
                }
            }
        }
        removeRandom(points);
        StringBuilder map = new StringBuilder();
        for (int i = 0; i < mapWidth; i++) {
            map.append(points[i]).append(System.lineSeparator());
        }
        return map.toString();
    }

    private void removeRandom(char[][] points) {
        List<Point> removablePoints = new ArrayList<>();
        for (int i = 1; i < (mapWidth - 1); i++) {
            for (int j = 1; j < (mapWidth - 1); j++) {
                if (points[i][j] == '*') {
                    removablePoints.add(new Point(i, j));
                }
            }
        }
        Collections.shuffle(removablePoints);
        for (int i = 0; i < removablePoints.size() * 0.15; i++) {
            points[removablePoints.get(i).getX()][removablePoints.get(i).getY()] = ' ';
        }
    }

    private void removeDeadEnd(char[][] points, int i, int j) {
        List<Point> removablePoints = new ArrayList<>();
        if (points[i][j + 1] == '*' && j != (mapWidth - 2))
            removablePoints.add(new Point(i, j + 1));
        if (points[i][j - 1] == '*' && j != 1) removablePoints.add(new Point(i, j - 1));
        if (points[i + 1][j] == '*' && i != (mapWidth - 2))
            removablePoints.add(new Point(i + 1, j));
        if (points[i - 1][j] == '*' && i != 1) removablePoints.add(new Point(i - 1, j));
        Collections.shuffle(removablePoints);
        points[removablePoints.get(0).getX()][removablePoints.get(0).getY()] = ' ';
        if (removablePoints.size() > 1 && MathUtils.randomBoolean()) {
            points[removablePoints.get(1).getX()][removablePoints.get(1).getY()] = ' ';
        }
    }

    private boolean isDeadEnd(char[][] points, int i, int j) {
        int count = 0;
        if (points[i][j + 1] == '*') count++;
        if (points[i][j - 1] == '*') count++;
        if (points[i + 1][j] == '*') count++;
        if (points[i - 1][j] == '*') count++;
        return count > 2;
    }

    public int saveMap(DatabaseManager db, MapMakerMap map) {
        String mapCode = mapToString(map);
        return db.saveMap(mapCode);
    }

    public String mapToString(MapMakerMap map) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < map.MAP_SQUARE_WIDTH; i++) {
            for (int j = 0; j < map.MAP_SQUARE_WIDTH; j++) {
                if (map.getTile(i, j).isBlock()) {
                    result.append('*');
                } else if (!map.getTile(i, j).isEaten()) {
                    if (map.getTile(i, j).isEnergyBomb()) {
                        result.append('.');
                    } else {
                        result.append(' ');
                    }
                } else {
                    result.append('0');
                }
            }
            result.append(System.lineSeparator());
        }
        return result.toString();
    }
}
