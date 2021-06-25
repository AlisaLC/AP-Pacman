package edu.sharif.ce.appacman.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.TimeUtils;

import java.security.Signature;
import java.util.HashMap;

import edu.sharif.ce.appacman.model.MapGraph;
import edu.sharif.ce.appacman.model.Point;
import lombok.Getter;

public class PacmanPlayer extends Actor {

    public Rectangle rectangle;
    final int SIZE;
    HashMap<String, TextureRegion> eatingTextures;
    String[] steps = {"0", "30", "45", "60", "45", "30"};
    int step;
    long lastStepTime;
    MapMakerMap map;
    @Getter
    int direction = -1;
    int tempDirection = -1;
    MapGraph mapGraph;
    Point currentPoint;
    public Point currentPosition;
    Point expectedPoint;
    public Point expectedPosition;
    boolean isPause;

    public PacmanPlayer(MapMakerMap map, int x, int y, MapGraph mapGraph) {
        this.map = map;
        this.mapGraph = mapGraph;
        rectangle = new Rectangle();
        setX(x);
        setY(y);
        SIZE = map.MAP_PIXEL_SIZE;
        rectangle.width = SIZE;
        rectangle.height = SIZE;
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("map/pacman.atlas"));
        eatingTextures = new HashMap<>();
        eatingTextures.put("0", atlas.findRegion("0"));
        eatingTextures.put("30", atlas.findRegion("30"));
        eatingTextures.put("45", atlas.findRegion("45"));
        eatingTextures.put("60", atlas.findRegion("60"));
        currentPoint = new Point((int) getX(), (int) getY());
        MapMakerTile currentTile = map.getTileByPosition((int) getX() + 1, (int) getY() + 1);
        currentPosition = new Point(currentTile.getPoint().getX(), currentTile.getPoint().getY());
        expectedPoint = new Point((int) getX(), (int) getY());
        expectedPosition = new Point(currentTile.getPoint().getX(), currentTile.getPoint().getY());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updateTexture();
        currentPoint.setX((int) getX());
        currentPoint.setY((int) getY());
        if (!isPause) {
            updateNextMove();
            updateDirectionByKeyPress();
            updateLocation();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(eatingTextures.get(steps[step]), getX(), getY(), SIZE / 2, SIZE / 2, SIZE, SIZE, 1, 1, (direction - 1) * -90);
    }

    private void updateTexture() {
        if (TimeUtils.millis() - lastStepTime > 100) {
            nextStep();
            lastStepTime = TimeUtils.millis();
        }
    }

    private void updateNextMove() {
        if (almostEqual(currentPoint, expectedPoint)) {
            direction = tempDirection;
            setX(expectedPoint.getX());
            setY(expectedPoint.getY());
            currentPosition.setX(expectedPosition.getX());
            currentPosition.setY(expectedPosition.getY());
            updateExpected();
        }
    }

    private void updateDirectionByKeyPress() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            tempDirection = 0;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            tempDirection = 1;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            tempDirection = 2;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            tempDirection = 3;
        }
    }

    private void updateLocation() {
        if (expectedPoint.getX() > getX()) {
            setX(getX() + 3 * SIZE * Gdx.graphics.getDeltaTime());
        } else if (expectedPoint.getX() < getX()) {
            setX(getX() - 3 * SIZE * Gdx.graphics.getDeltaTime());
        }
        if (expectedPoint.getY() > getY()) {
            setY(getY() + 3 * SIZE * Gdx.graphics.getDeltaTime());
        } else if (expectedPoint.getY() < getY()) {
            setY(getY() - 3 * SIZE * Gdx.graphics.getDeltaTime());
        }
    }

    private void updateExpected() {
        if (direction > -1) {
            switch (direction) {
                case 0:
                    expectedPosition.setX(currentPosition.getX() + 1);
                    break;
                case 1:
                    expectedPosition.setY(currentPosition.getY() + 1);
                    break;
                case 2:
                    expectedPosition.setX(currentPosition.getX() - 1);
                    break;
                case 3:
                    expectedPosition.setY(currentPosition.getY() - 1);
                    break;
            }
            if (!map.getTile(expectedPosition.getX(), expectedPosition.getY()).isBlock()) {
                expectedPoint.setX((int) map.getTile(expectedPosition.getX(), expectedPosition.getY()).getRectangle().getX());
                expectedPoint.setY((int) map.getTile(expectedPosition.getX(), expectedPosition.getY()).getRectangle().getY());
            } else {
                expectedPosition.setX(currentPosition.getX());
                expectedPosition.setY(currentPosition.getY());
            }
        }
    }

    private boolean almostEqual(Point a, Point b) {
        return Vector2.dst(a.getX(), a.getY(), b.getX(), b.getY()) < 0.15 * SIZE;
    }

    private void nextStep() {
        step++;
        if (step == steps.length) step = 0;
    }

    public void setDirection(int direction) {
        tempDirection = direction;
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

    public void pause() {
        tempDirection = direction;
        direction = -1;
        isPause = true;
    }

    public void resume() {
        direction = tempDirection;
        isPause = false;
    }

    public void updatePointByPosition() {
        currentPoint.setX((int) map.getTile(currentPosition.getX(), currentPosition.getY()).getRectangle().getX());
        currentPoint.setY((int) map.getTile(currentPosition.getX(), currentPosition.getY()).getRectangle().getY());
        setX(currentPoint.getX());
        setY(currentPoint.getY());
        expectedPosition.setX(currentPosition.getX());
        expectedPosition.setY(currentPosition.getY());
        expectedPoint.setX(currentPoint.getX());
        expectedPoint.setY(currentPoint.getY());
        updateExpected();
    }
}
