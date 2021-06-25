package edu.sharif.ce.appacman.model;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Vector2;

public class MapPath implements Connection<Point> {

    private Point from;
    private Point to;
    private float cost;

    public MapPath(Point from, Point to) {
        this.from = from;
        this.to = to;
        cost = Vector2.dst(from.getX(), from.getY(), to.getX(), to.getY());
    }

    public int getDirection() {
        if (to.getX() > from.getX()) {
            return 0;
        }
        if (to.getY() > from.getY()) {
            return 1;
        }
        if (to.getX() < from.getX()) {
            return 2;
        }
        if (to.getY() < from.getY()) {
            return 3;
        }
        return -1;
    }

    @Override
    public float getCost() {
        return 0;
    }

    @Override
    public Point getFromNode() {
        return from;
    }

    @Override
    public Point getToNode() {
        return to;
    }
}
