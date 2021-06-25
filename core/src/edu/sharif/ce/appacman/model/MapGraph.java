package edu.sharif.ce.appacman.model;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Comparator;
import java.util.HashMap;

import edu.sharif.ce.appacman.view.MapMakerMap;

public class MapGraph implements IndexedGraph<Point> {

    MapMakerMap map;
    int width;
    public Array<Point> points;
    ObjectMap<Point, Array<Connection<Point>>> tileMap;
    private MapDistance distance;
    private int indexCounter;
    private HashMap<Integer, Integer> indexMap;

    public MapGraph(MapMakerMap map) {
        this.map = map;
        this.width = map.MAP_SQUARE_WIDTH;
        distance = new MapDistance();
        points = new Array<>();
        tileMap = new ObjectMap<>();
        indexMap = new HashMap<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                if (!map.getTile(i, j).isBlock()) {
                    points.add(new Point(i, j, indexCounter++));
                    indexMap.put(i * width + j, indexCounter - 1);
                }
            }
        }
        for (int i = 0; i < points.size; i++) {
            for (int j = 0; j < 4; j++) {
                if (isConnected(points.get(i), j)) {
                    connectPoints(points.get(i), j);
                }
            }
        }
    }

    public GraphPath<Point> findPath(Point from, Point to) {
        GraphPath<Point> path = new DefaultGraphPath<>();
        try {
            new IndexedAStarPathFinder<>(this).searchNodePath(from, to, distance, path);
        } catch (NullPointerException e) {

        }
        return path;
    }

    private void connectPoints(Point from, int direction) {
        switch (direction) {
            case 0:
                connectPoints(from, points.get(getPointIndex(from.getX() + 1, from.getY())));
                break;
            case 1:
                connectPoints(from, points.get(getPointIndex(from.getX(), from.getY() + 1)));
                break;
            case 2:
                connectPoints(from, points.get(getPointIndex(from.getX() - 1, from.getY())));
                break;
            case 3:
                connectPoints(from, points.get(getPointIndex(from.getX(), from.getY() - 1)));
                break;
        }
    }

    private boolean isConnected(Point from, int direction) {
        switch (direction) {
            case 0:
                return !map.getTile(from.getX() + 1, from.getY()).isBlock();
            case 1:
                return !map.getTile(from.getX(), from.getY() + 1).isBlock();
            case 2:
                return !map.getTile(from.getX() - 1, from.getY()).isBlock();
            case 3:
                return !map.getTile(from.getX(), from.getY() - 1).isBlock();
        }
        return false;
    }

    private void connectPoints(Point from, Point to) {
        MapPath path = new MapPath(from, to);
        if (!tileMap.containsKey(from)) {
            tileMap.put(from, new Array<>());
        }
        tileMap.get(from).add(path);
    }

    public int getPointIndex(int i, int j) {
        if (indexMap.get(i * width + j) != null) {
            return indexMap.get(i * width + j);
        } else {
            return indexMap.entrySet().stream().min(Comparator.comparingInt(e -> Math.abs(e.getKey() - (i * width + j)))).get().getValue();
        }
    }

    @Override
    public int getIndex(Point node) {
        return node.getIndex();
    }

    @Override
    public int getNodeCount() {
        return indexCounter;
    }

    @Override
    public Array<Connection<Point>> getConnections(Point fromNode) {
        return tileMap.get(fromNode);
    }
}
