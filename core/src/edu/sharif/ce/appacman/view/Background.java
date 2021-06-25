package edu.sharif.ce.appacman.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.ScreenUtils;

public class Background extends Actor {

    Texture starBackground;
    int y;

    public Background() {
        starBackground = new Texture(Gdx.files.internal("background.jpg"));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        y += 800 * Gdx.graphics.getDeltaTime();
        if (y >= 1080) y = 0;
        batch.draw(starBackground, 0, -1080 + y, 1920, 1080);
        batch.draw(starBackground, 0, y, 1920, 1080);
    }
}
