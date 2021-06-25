package edu.sharif.ce.appacman.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import edu.sharif.ce.appacman.model.Point;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapMakerTile {

    private Rectangle rectangle;
    private boolean isBlock;
    private boolean isEnergyBomb;
    private boolean isEaten;
    private TextureRegion textureRegion;
    private float rotation;
    Point point;
    private final int SIZE;

    public MapMakerTile(int x, int y, int SIZE, Point point) {
        this.SIZE = SIZE;
        this.point = point;
        rectangle = new Rectangle();
        rectangle.x = x;
        rectangle.y = y;
        rectangle.width = SIZE;
        rectangle.height = SIZE;
    }

    public void draw(Batch batch) {
        if (isBlock) {
            batch.draw(textureRegion, rectangle.x, rectangle.y, SIZE,
                    SIZE);
        } else if (!isEaten) {
            batch.draw(textureRegion, rectangle.x, rectangle.y, SIZE / 2,
                    SIZE / 2, SIZE,
                    SIZE, 1, 1, rotation);
            if (!isEnergyBomb) {
                rotation += 300 * Gdx.graphics.getDeltaTime();
            } else {
                rotation -= 300 * Gdx.graphics.getDeltaTime();
            }
        }
    }

    public void updateBlock(TextureAtlas atlas, boolean up, boolean right, boolean down, boolean left) {
        String status = (up ? "T" : "F") + (right ? "T" : "F") + (down ? "T" : "F") + (left ? "T" : "F");
        textureRegion = atlas.findRegion(status);
        isBlock = true;
    }

    public void updateDot(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
        isBlock = false;
        isEnergyBomb = false;
    }

    public void updatePower(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
        isBlock = false;
        isEnergyBomb = true;
    }

}
