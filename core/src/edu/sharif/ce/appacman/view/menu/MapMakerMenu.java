package edu.sharif.ce.appacman.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.TimeUtils;

import edu.sharif.ce.appacman.Pacman;
import edu.sharif.ce.appacman.controller.MapController;
import edu.sharif.ce.appacman.view.MapMakerMap;
import edu.sharif.ce.appacman.view.MenuState;

public class MapMakerMenu extends Menu {

    MapMakerMap map;
    int lastTouchedX = -1000;
    int lastTouchedY = -1000;

    public MapMakerMenu(Pacman game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        int mapWidth = game.getSettings().getMapWidth();
        int pixelSize = 980 / mapWidth;
        MapController.getInstance().setMapWidth(mapWidth);
        map = new MapMakerMap(mapWidth, (1920 - mapWidth * pixelSize) / 2, (1080 - mapWidth * pixelSize) / 2);
        ImageButton backButton = getBackButton();
        TextButton generateRandomButton = new TextButton("Generate Random Map", game.getFirstSkin(), "big");
        generateRandomButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                map.setMap(MapController.getInstance().getRandomMap());
            }
        });
        TextButton saveMap = new TextButton("Save Map", game.getFirstSkin(), "big");
        saveMap.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MapController.getInstance().saveMap(game.getDb(), map);
            }
        });
        Table table = new Table();
        table.setX(mapWidth * pixelSize / 2 + 1000);
        table.setY(350);
        table.setWidth(400);
        table.setHeight(400);
        table.add(generateRandomButton).pad(20).expand().fill();
        table.row();
        table.add(saveMap).pad(20).expand().fill();
        stage.addActor(table);
        stage.addActor(map);
        stage.addActor(backButton);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (Gdx.input.justTouched()) {
            map.click(Gdx.input.getX(), Gdx.input.getY());
            lastTouchedX = Gdx.input.getX();
            lastTouchedY = Gdx.input.getY();
            map.clearClickTiles();
        } else if (Gdx.input.isTouched()) {
            if (!(Math.abs(lastTouchedX - Gdx.input.getX()) < 16 && Math.abs(lastTouchedY - Gdx.input.getY()) < 16)) {
                map.click(Gdx.input.getX(), Gdx.input.getY());
                lastTouchedX = Gdx.input.getX();
                lastTouchedY = Gdx.input.getY();
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.changeState(MenuState.SETTINGS);
            dispose();
        }
    }

    private ImageButton getBackButton() {
        ImageButton backButton = new ImageButton(game.getFirstSkin());
        backButton.add(new Image(new TextureRegionDrawable(new Texture(Gdx.files.internal("back.png"))))).width(150).height(150).center();
        backButton.setWidth(170);
        backButton.setHeight(170);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.changeState(MenuState.SETTINGS);
                dispose();
            }
        });
        backButton.setX(20);
        backButton.setY(20);
        return backButton;
    }
}
