package edu.sharif.ce.appacman.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.HashSet;
import java.util.Set;

import edu.sharif.ce.appacman.model.Point;
import lombok.Getter;
import lombok.Setter;

public class MapMakerMap extends Actor {

    MapMakerTile[][] map;
    TextureAtlas atlas;
    TextureRegion dotTexture;
    TextureRegion powerTexture;
    @Setter
    @Getter
    PacmanPlayer pacmanPlayer;
    public final int MAP_SQUARE_WIDTH;
    public final int MAP_PIXEL_SIZE;
    private Set<Point> toggledTiles;

    public MapMakerMap(int MAP_SQUARE_WIDTH, int x, int y) {
        this.MAP_SQUARE_WIDTH = MAP_SQUARE_WIDTH;
        MAP_PIXEL_SIZE = 980 / MAP_SQUARE_WIDTH;
        setX(x);
        setY(y);
        map = new MapMakerTile[MAP_SQUARE_WIDTH][MAP_SQUARE_WIDTH];
        atlas = new TextureAtlas(Gdx.files.internal("map/blocks.atlas"));
        dotTexture = new TextureRegion(new Texture(Gdx.files.internal("map/dot.png")));
        powerTexture = new TextureRegion(new Texture(Gdx.files.internal("map/power.png")));
        toggledTiles = new HashSet<>();
        int idCounter = 0;
        for (int i = 0; i < MAP_SQUARE_WIDTH; i++) {
            for (int j = 0; j < MAP_SQUARE_WIDTH; j++) {
                map[i][j] = new MapMakerTile((int) getX() + j * MAP_PIXEL_SIZE,
                        (int) getY() + i * MAP_PIXEL_SIZE, MAP_PIXEL_SIZE, new Point(i, j));
                if (i > 0 && i < (MAP_SQUARE_WIDTH - 1) && j > 0 &&
                        j < (MAP_SQUARE_WIDTH - 1)) {
                    map[i][j].updateDot(dotTexture);
                    map[i][j].getPoint().setIndex(idCounter++);
                } else {
                    map[i][j].setBlock(true);
                }
            }
        }
        updateWholeMap();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (int i = 0; i < MAP_SQUARE_WIDTH; i++) {
            for (int j = 0; j < MAP_SQUARE_WIDTH; j++) {
                map[i][j].draw(batch);
            }
        }
    }

    public void setMap(String mapCode) {
        char[][] points = new char[MAP_SQUARE_WIDTH][MAP_SQUARE_WIDTH];
        String[] lines = mapCode.split("\r?\n");
        if (lines.length != MAP_SQUARE_WIDTH) return;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() != MAP_SQUARE_WIDTH) return;
            points[i] = lines[i].toCharArray();
        }
        for (int i = 0; i < MAP_SQUARE_WIDTH; i++) {
            for (int j = 0; j < MAP_SQUARE_WIDTH; j++) {
                if (points[i][j] == ' ') {
                    map[i][j].updateDot(dotTexture);
                } else if (points[i][j] == '*') {
                    map[i][j].updateBlock(atlas, getUpStatus(points, i, j), getRightStatus(points, i, j),
                            getDownStatus(points, i, j), getLeftStatus(points, i, j));
                } else if (points[i][j] == '.') {
                    map[i][j].updatePower(powerTexture);
                } else if (points[i][j] == '0') {
                    map[i][j].updateDot(dotTexture);
                    map[i][j].setEaten(true);
                }
            }
        }
    }

    private boolean getUpStatus(char[][] points, int i, int j) {
        if (i == (MAP_SQUARE_WIDTH - 1)) return false;
        return points[i + 1][j] == '*';
    }

    private boolean getRightStatus(char[][] points, int i, int j) {
        if (j == (MAP_SQUARE_WIDTH - 1)) return false;
        return points[i][j + 1] == '*';
    }

    private boolean getDownStatus(char[][] points, int i, int j) {
        if (i == 0) return false;
        return points[i - 1][j] == '*';
    }

    private boolean getLeftStatus(char[][] points, int i, int j) {
        if (j == 0) return false;
        if (i == 0 || i == (MAP_SQUARE_WIDTH - 1)) {
            return true;
        }
        return points[i][j - 1] == '*';
    }

    private boolean getUpStatus(int i, int j) {
        if (i == (MAP_SQUARE_WIDTH - 1)) return false;
        if (j == 0 || j == (MAP_SQUARE_WIDTH - 1)) {
            return true;
        }
        return map[i + 1][j].isBlock();
    }

    private boolean getRightStatus(int i, int j) {
        if (j == (MAP_SQUARE_WIDTH - 1)) return false;
        if (i == 0 || i == (MAP_SQUARE_WIDTH - 1)) {
            return true;
        }
        return map[i][j + 1].isBlock();
    }

    private boolean getDownStatus(int i, int j) {
        if (i == 0) return false;
        if (j == 0 || j == (MAP_SQUARE_WIDTH - 1)) {
            return true;
        }
        return map[i - 1][j].isBlock();
    }

    private boolean getLeftStatus(int i, int j) {
        if (j == 0) return false;
        return map[i][j - 1].isBlock();
    }

    public void click(int x, int y) {
        int relativeX = (int) (x - getX());
        int relativeY = (int) (y - (1080 - MAP_SQUARE_WIDTH *
                MAP_PIXEL_SIZE - getY()));
        if (relativeX > MAP_PIXEL_SIZE &&
                relativeX < (MAP_SQUARE_WIDTH - 1) * MAP_PIXEL_SIZE &&
                relativeY > MAP_PIXEL_SIZE &&
                relativeY < (MAP_SQUARE_WIDTH - 1) * MAP_PIXEL_SIZE) {
            int j = relativeX / MAP_PIXEL_SIZE;
            int i = (MAP_SQUARE_WIDTH - 1) - relativeY / MAP_PIXEL_SIZE;
            if (!((i == 1 && j == 1) || (i == 1 && j == MAP_SQUARE_WIDTH - 2) ||
                    (i == MAP_SQUARE_WIDTH - 2 && j == 1) ||
                    (i == MAP_SQUARE_WIDTH - 2 && j == MAP_SQUARE_WIDTH - 2))) {
                if (toggledTiles.contains(map[i][j].point)) {
                    return;
                }
                toggledTiles.add(map[i][j].point);
                if (map[i][j].isBlock()) {
                    map[i][j].updateDot(dotTexture);
                } else {
                    map[i][j].setBlock(true);
                }
                updateWholeMap();
            }
        }
    }

    public MapMakerTile getTileByPosition(int x, int y) {
        int relativeX = (int) (x - getX());
        int relativeY = (int) (y - (1080 - MAP_SQUARE_WIDTH * MAP_PIXEL_SIZE - getY()));
        if (relativeX >= 0 &&
                relativeX < MAP_SQUARE_WIDTH * MAP_PIXEL_SIZE &&
                relativeY >= 0 &&
                relativeY < MAP_SQUARE_WIDTH * MAP_PIXEL_SIZE) {
            int j = relativeX / MAP_PIXEL_SIZE;
            int i = relativeY / MAP_PIXEL_SIZE;
            return map[i][j];
        }
        return null;
    }

    public void updateWholeMap() {
        for (int i = 0; i < MAP_SQUARE_WIDTH; i++) {
            for (int j = 0; j < MAP_SQUARE_WIDTH; j++) {
                if (map[i][j].isBlock()) {
                    map[i][j].updateBlock(atlas, getUpStatus(i, j), getRightStatus(i, j),
                            getDownStatus(i, j), getLeftStatus(i, j));
                } else if (!map[i][j].isEaten()) {
                    if (map[i][j].isEnergyBomb()) {
                        map[i][j].updatePower(powerTexture);
                    } else {
                        map[i][j].updateDot(dotTexture);
                    }
                }
            }
        }
    }

    public MapMakerTile getTile(int i, int j) {
        return map[i][j];
    }

    public void clearClickTiles() {
        toggledTiles.clear();
    }
}
