package edu.sharif.ce.appacman.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import edu.sharif.ce.appacman.Pacman;
import edu.sharif.ce.appacman.view.MenuState;

public class SettingsMenu extends Menu {

    public SettingsMenu(Pacman game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        Label maxLivesLabel = new Label("Max Lives: ", game.getFirstSkin(), "title");
        Slider maxLivesSlider = new Slider(2, 5, 1, false, game.getSecondSkin());
        Label musicLevelLabel = new Label("Music Level: ", game.getFirstSkin(), "title");
        Slider musicLevelSlider = new Slider(0, 100, 5, false, game.getSecondSkin());
        Label SFXLevelLabel = new Label("SFX Level: ", game.getFirstSkin(), "title");
        Slider SFXLevelSlider = new Slider(0, 100, 5, false, game.getSecondSkin());
        Label mapWidthLabel = new Label("Map Width: ", game.getFirstSkin(), "title");
        Slider mapWidthSlider = new Slider(2, 17, 1, false, game.getSecondSkin());
        TextButton saveButton = new TextButton("Save", game.getFirstSkin(), "big");
        TextButton newMap = new TextButton("Create a new map", game.getFirstSkin(), "big");
        newMap.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.changeState(MenuState.MAP_MAKER);
                dispose();
            }
        });
        ImageButton backButton = getBackButton();
        prepareSliders(maxLivesLabel, maxLivesSlider, musicLevelLabel, musicLevelSlider, mapWidthLabel, mapWidthSlider, SFXLevelLabel, SFXLevelSlider);
        setSaveClick(maxLivesSlider, musicLevelSlider, mapWidthSlider, SFXLevelSlider, saveButton);
        addTableToStage(maxLivesLabel, maxLivesSlider, musicLevelLabel, musicLevelSlider, mapWidthLabel, mapWidthSlider, SFXLevelLabel, SFXLevelSlider, saveButton, newMap);
        stage.addActor(backButton);
    }

    private void prepareSliders(Label maxLivesLabel, Slider maxLivesSlider, Label musicLevelLabel, Slider musicLevelSlider, Label mapWidthLabel, Slider mapWidthSlider, Label SFXLevelLabel, Slider SFXLevelSlider) {
        maxLivesLabel.setColor(Color.WHITE);
        musicLevelLabel.setColor(Color.WHITE);
        mapWidthLabel.setColor(Color.WHITE);
        SFXLevelLabel.setColor(Color.WHITE);
        maxLivesSlider.setValue(game.getSettings().getMaxLives());
        musicLevelSlider.setValue(game.getSettings().getMusicLevel() * 100);
        SFXLevelSlider.setValue(game.getSettings().getSFXLevel() * 100);
        mapWidthSlider.setValue((game.getSettings().getMapWidth() - 1) / 2);
        maxLivesSlider.setWidth(860);
        maxLivesSlider.setHeight(50);
        musicLevelSlider.setWidth(830);
        musicLevelSlider.setHeight(50);
        SFXLevelSlider.setWidth(830);
        SFXLevelSlider.setHeight(50);
        mapWidthSlider.setWidth(845);
        mapWidthSlider.setHeight(50);
    }

    private void setSaveClick(Slider maxLivesSlider, Slider musicLevelSlider, Slider mapWidthSlider, Slider SFXLevelSlider, TextButton saveButton) {
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getSettings().setMaxLives(MathUtils.round(maxLivesSlider.getValue()));
                game.getSettings().setMusicLevel(musicLevelSlider.getValue() / 100.0f);
                game.getSettings().setSFXLevel(SFXLevelSlider.getValue() / 100.0f);
                game.getSettings().setMapWidth(2 * MathUtils.round(mapWidthSlider.getValue()) + 1);
                game.getBackgroundMusic().setVolume(game.getSettings().getMusicLevel());
                game.getGameMusic().setVolume(game.getSettings().getMusicLevel());
                Dialog dialog = new Dialog("", game.getFirstSkin());
                dialog.text("Settings Saved Successfully!");
                dialog.button("OK");
                dialog.getTitleTable().pad(5);
                dialog.getContentTable().pad(5);
                dialog.getButtonTable().pad(5);
                dialog.show(stage);
            }
        });
    }

    private void addTableToStage(Label maxLivesLabel, Slider maxLivesSlider, Label musicLevelLabel, Slider musicLevelSlider, Label mapWidthLabel, Slider mapWidthSlider, Label SFXLevelLabel, Slider SFXLevelSlider, TextButton saveButton, TextButton newMap) {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(maxLivesLabel).width(160).height(150).padRight(40).left();
        table.add(maxLivesSlider).width(860).height(150).padLeft(40).right();
        table.row();
        table.add(musicLevelLabel).width(130).height(150).padRight(70).left();
        table.add(musicLevelSlider).width(830).height(150).padLeft(70).right();
        table.row();
        table.add(SFXLevelLabel).width(130).height(150).padRight(70).left();
        table.add(SFXLevelSlider).width(830).height(150).padLeft(70).right();
        table.row();
        table.add(mapWidthLabel).width(145).height(150).padRight(70).left();
        table.add(mapWidthSlider).width(845).height(150).padLeft(70).right();
        table.row();
        table.add(saveButton).pad(15).colspan(2).width(1100).height(200).bottom();
        table.row();
        table.add(newMap).pad(15).colspan(2).width(1100).height(200).bottom();
        stage.addActor(table);
    }

    private ImageButton getBackButton() {
        ImageButton backButton = new ImageButton(game.getFirstSkin());
        backButton.add(new Image(new TextureRegionDrawable(new Texture(Gdx.files.internal("back.png"))))).width(150).height(150).center();
        backButton.setWidth(170);
        backButton.setHeight(170);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.changeState(MenuState.MAIN);
                dispose();
            }
        });
        backButton.setX(20);
        backButton.setY(20);
        return backButton;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.changeState(MenuState.MAIN);
            dispose();
        }
    }
}
