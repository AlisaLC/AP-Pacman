package edu.sharif.ce.appacman.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.HashMap;

import edu.sharif.ce.appacman.model.MapGraph;
import edu.sharif.ce.appacman.model.Point;
import lombok.Getter;
import lombok.Setter;

public class GhostPlayer extends Actor {

    public Rectangle rectangle;
    int SIZE;
    float GHOST_SPEED;
    HashMap<String, TextureRegion> movingTextures;
    int color;
    String[] colors = {"RED", "BLUE", "PINK", "ORANGE"};
    MapMakerMap map;
    boolean isHard;
    int direction = -1;
    MapGraph mapGraph;
    Point currentPoint;
    public Point currentPosition;
    Point expectedPoint;
    Point expectedPosition;
    GraphPath<Point> path;
    int pathIndex = 0;
    @Getter
    @Setter
    boolean isPause, isFright, isInvisible;
    public float frightTime, invisibleTime;

    public GhostPlayer(MapMakerMap map, int x, int y, int color, boolean isHard, MapGraph mapGraph) {
        this.SIZE = map.MAP_PIXEL_SIZE;
        this.map = map;
        this.isHard = isHard;
        this.color = color;
        this.mapGraph = mapGraph;
        rectangle = new Rectangle();
        setX(x);
        setY(y);
        rectangle.width = SIZE;
        rectangle.height = SIZE;
        movingTextures = new HashMap<>();
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("map/ghosts.atlas"));
        for (int i = 0; i < 4; i++) {
            String name = colors[i] + "-";
            movingTextures.put(name + "UP", atlas.findRegion(name + "UP"));
            movingTextures.put(name + "RIGHT", atlas.findRegion(name + "RIGHT"));
            movingTextures.put(name + "DOWN", atlas.findRegion(name + "DOWN"));
            movingTextures.put(name + "LEFT", atlas.findRegion(name + "LEFT"));
        }
        movingTextures.put("FRIGHT-UP", atlas.findRegion("FRIGHT-UP"));
        movingTextures.put("FRIGHT-RIGHT", atlas.findRegion("FRIGHT-RIGHT"));
        movingTextures.put("FRIGHT-DOWN", atlas.findRegion("FRIGHT-DOWN"));
        movingTextures.put("FRIGHT-LEFT", atlas.findRegion("FRIGHT-LEFT"));
        currentPoint = new Point((int) getX(), (int) getY());
        MapMakerTile currentTile = map.getTileByPosition((int) getX() + 1, (int) getY() + 1);
        currentPosition = new Point(currentTile.getPoint().getX(), currentTile.getPoint().getY());
        expectedPoint = new Point((int) getX(), (int) getY());
        expectedPosition = new Point(currentTile.getPoint().getX(), currentTile.getPoint().getY());
        updatePath();
        updateExpected();
        GHOST_SPEED = 2.5f;
        invisible(2);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updateTimeValues();
        currentPoint.setX((int) getX());
        currentPoint.setY((int) getY());
        if (!isPause && !isInvisible) {
            updateNextMove();
            updateLocation();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        double invisibleFraction = isInvisible ? Double.parseDouble("0." + (invisibleTime + "").split("\\.")[1]) : 0;
        double frightFraction = isFright ? Double.parseDouble("0." + (frightTime + "").split("\\.")[1]) : 0;
        if (!(((invisibleFraction > 0.25 && invisibleFraction < 0.5) || (invisibleFraction > 0.75)) ||
                (frightTime > 8 && ((frightFraction > 0.25 && frightFraction < 0.5) || (frightFraction > 0.75))))) {
            batch.draw(movingTextures.get(getTextureString()), getX(), getY(), SIZE, SIZE);
        }
    }

    private void updateNextMove() {
        if (almostEqual(currentPoint, expectedPoint)) {
            setX(expectedPoint.getX());
            setY(expectedPoint.getY());
            currentPosition.setX(expectedPosition.getX());
            currentPosition.setY(expectedPosition.getY());
            if (pathIndex == path.getCount() - 1) {
                updatePath();
            }
            updateExpected();
        }
    }

    private void updateLocation() {
        if (map.getTile(expectedPosition.getX(), expectedPosition.getY()).isBlock()) {
            return;
        }
        if (expectedPoint.getX() > getX()) {
            setX(getX() + GHOST_SPEED * SIZE * Gdx.graphics.getDeltaTime());
            direction = 1;
        } else if (expectedPoint.getX() < getX()) {
            setX(getX() - GHOST_SPEED * SIZE * Gdx.graphics.getDeltaTime());
            direction = 3;
        }
        if (expectedPoint.getY() > getY()) {
            setY(getY() + GHOST_SPEED * SIZE * Gdx.graphics.getDeltaTime());
            direction = 0;
        } else if (expectedPoint.getY() < getY()) {
            setY(getY() - GHOST_SPEED * SIZE * Gdx.graphics.getDeltaTime());
            direction = 2;
        }
    }

    private void updateTimeValues() {
        if (!isPause) {
            if (isFright) {
                frightTime += Gdx.graphics.getDeltaTime();
                if (frightTime >= 10) {
                    brave();
                }
            }
            if (isInvisible) {
                invisibleTime -= Gdx.graphics.getDeltaTime();
                if (invisibleTime <= 0) {
                    isInvisible = false;
                }
            }
        } else {
            if (isInvisible) {
                invisibleTime -= Gdx.graphics.getDeltaTime();
                double invisibleFraction = Double.parseDouble("0." + (invisibleTime + "").split("\\.")[1]);
                if (invisibleFraction < 0.1) {
                    invisibleTime += 0.88;
                }
            }
        }
    }

    private String getTextureString() {
        if (!isFright) {
            return colors[color] + "-" + getDirectionString(direction);
        } else {
            return "FRIGHT-" + getDirectionString(direction);
        }
    }

    private String getDirectionString(int direction) {
        switch (direction) {
            case 0:
                return "UP";
            case 1:
                return "RIGHT";
            case 2:
                return "DOWN";
            case 3:
                return "LEFT";
        }
        return getDirectionString(color);
    }

    private void updateExpected() {
        if (isHard && !isFright && pathIndex == 6) {
            updatePath();
        }
        if (path.getCount() > 1) {
            expectedPosition.setX(path.get(++pathIndex).getX());
            expectedPosition.setY(path.get(pathIndex).getY());
            expectedPoint.setX((int) map.getTile(expectedPosition.getX(), expectedPosition.getY()).getRectangle().getX());
            expectedPoint.setY((int) map.getTile(expectedPosition.getX(), expectedPosition.getY()).getRectangle().getY());
        }
    }

    private void updatePath() {
        if (!isHard || isFright) {
            path = mapGraph.findPath(mapGraph.points.get(mapGraph.getPointIndex(currentPosition.getX(),
                    currentPosition.getY())), mapGraph.points.random());
        } else {
            Point frontPoint = map.pacmanPlayer.currentPosition;
            Point targetPoint;
            switch (color) {
                case 1:
                    frontPoint = getFrontPoint(map.getPacmanPlayer().currentPosition, map.getPacmanPlayer().direction, 3);
                    break;
                case 2:
                    frontPoint = getFrontPoint(map.getPacmanPlayer().currentPosition, map.getPacmanPlayer().direction, -3 + MathUtils.random(6));
                    break;
                case 3:
                    frontPoint = getFrontPoint(map.getPacmanPlayer().currentPosition, MathUtils.random(0, 3), -3 + MathUtils.random(6));
            }
            targetPoint = mapGraph.points.get(mapGraph.
                    getPointIndex(frontPoint.getX(), frontPoint.getY()));
            path = mapGraph.findPath(mapGraph.points.get(mapGraph.getPointIndex(currentPosition.getX(),
                    currentPosition.getY())), targetPoint);
        }
        pathIndex = 0;
    }

    private Point getFrontPoint(Point pacman, int direction, int steps) {
        switch (direction) {
            case 0:
                return new Point(pacman.getX() + steps, pacman.getY());
            case 1:
                return new Point(pacman.getX(), pacman.getY() + steps);
            case 2:
                return new Point(pacman.getX() - steps, pacman.getY());
            case 3:
                return new Point(pacman.getX(), pacman.getY() - steps);
        }
        return pacman;
    }

    private boolean almostEqual(Point a, Point b) {
        return Vector2.dst(a.getX(), a.getY(), b.getX(), b.getY()) < 0.15 * SIZE;
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        rectangle.x = x;
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        rectangle.y = y;
    }

    public void reset() {
        int i = ((color % 2) * (map.MAP_SQUARE_WIDTH - 3)) + 1;
        int j = ((color > 1 ? 1 : 0) * (map.MAP_SQUARE_WIDTH - 3)) + 1;
        MapMakerTile currentTile = map.getTile(i, j);
        setX(currentTile.getRectangle().getX());
        setY(currentTile.getRectangle().getY());
        currentPoint = new Point((int) getX(), (int) getY());
        currentPosition = new Point(i, j);
        expectedPoint = new Point((int) getX(), (int) getY());
        expectedPosition = new Point(i, j);
        updatePath();
        updateExpected();
        invisible(5f);
    }

    public void pause() {
        isPause = true;
    }

    public void resume() {
        direction = 0;
        isPause = false;
    }

    public void fright() {
        isFright = true;
        updatePath();
        updateExpected();
        frightTime = 0;
        GHOST_SPEED = 1.75f;
    }

    public void brave() {
        isFright = false;
        updatePath();
        updateExpected();
        frightTime = 0;
        GHOST_SPEED = 2.5f;
    }

    public void invisible(float invisibleTime) {
        isInvisible = true;
        this.invisibleTime = invisibleTime;
    }

    public void visible() {
        isInvisible = false;
        invisibleTime = 0;
    }

    public void updatePointByPosition() {
        currentPoint.setX((int) map.getTile(currentPosition.getX(), currentPosition.getY()).getRectangle().getX());
        currentPoint.setY((int) map.getTile(currentPosition.getX(), currentPosition.getY()).getRectangle().getY());
        setX(currentPoint.getX());
        setY(currentPoint.getY());
        updatePath();
        updateExpected();
    }
}
