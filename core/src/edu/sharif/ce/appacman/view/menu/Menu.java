package edu.sharif.ce.appacman.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import edu.sharif.ce.appacman.Pacman;

public class Menu implements Screen {

    protected final Pacman game;
    protected SpriteBatch batch;
    protected Stage stage;
    protected Viewport viewport;
    protected OrthographicCamera camera;

    public Menu(Pacman game) {
        this.game = game;
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(Pacman.WORLD_WIDTH, Pacman.WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(0, -3, 10);
        camera.update();
        stage = new Stage(viewport, batch);
        stage.addActor(game.getBackground());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    @Override
    public void pause() {
        game.getBackgroundMusic().pause();
    }

    @Override
    public void resume() {
        game.getBackgroundMusic().play();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.clear();
    }


}
