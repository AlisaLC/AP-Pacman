package edu.sharif.ce.appacman.model;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;

public class MapDistance implements Heuristic<Point> {

    @Override
    public float estimate(Point node, Point endNode) {
        return Vector2.dst(node.getX(), node.getY(), endNode.getX(), endNode.getY());
    }

}
