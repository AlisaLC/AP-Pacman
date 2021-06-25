package edu.sharif.ce.appacman.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.List;

import edu.sharif.ce.appacman.Pacman;
import edu.sharif.ce.appacman.view.MenuState;

public class MainMenu extends Menu {

    public MainMenu(Pacman game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        TextButton playButton = new TextButton("Play", game.getFirstSkin(), "big");
        TextButton scoreboardButton = new TextButton("Scoreboard", game.getFirstSkin(), "big");
        TextButton accountButton = new TextButton("Account", game.getFirstSkin(), "big");
        TextButton settingsButton = new TextButton("Settings", game.getFirstSkin(), "big");
        TextButton logoutButton = new TextButton("Logout", game.getFirstSkin(), "big");
        if (game.getUser() == null) {
            logoutButton.setText("Back to Login");
        }
        setPlayClick(playButton);
        setMenuChangeClick(scoreboardButton, MenuState.SCOREBOARD);
        setMenuChangeClick(accountButton, MenuState.ACCOUNT);
        setMenuChangeClick(settingsButton, MenuState.SETTINGS);
        logoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setUser(null);
                game.changeState(MenuState.LOGIN);
                dispose();
            }
        });
        addTableToStage(playButton, scoreboardButton, accountButton, settingsButton, logoutButton);
    }

    private void setPlayClick(TextButton playButton) {
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Label difficultyLevelLabel = new Label("Difficulty: ", game.getFirstSkin());
                SelectBox<String> difficultyLevelSelect = new SelectBox<>(game.getFirstSkin());
                Label mapLabel = new Label("Map: ", game.getFirstSkin());
                SelectBox<String> mapSelect = new SelectBox<>(game.getFirstSkin());
                Dialog dialog = getPlayDialog(difficultyLevelSelect, mapSelect);
                showDialog(difficultyLevelLabel, difficultyLevelSelect, mapLabel, mapSelect, dialog);
            }
        });
    }

    private Dialog getPlayDialog(SelectBox<String> difficultyLevelSelect, SelectBox<String> mapSelect) {
        Dialog dialog = new Dialog("", game.getFirstSkin()) {
            @Override
            protected void result(Object object) {
                if (!(object instanceof Boolean)) {
                    return;
                }
                Boolean result = (Boolean) object;
                if (result) {
                    game.setHard(difficultyLevelSelect.getSelected().equals("Hard"));
                    if (!mapSelect.getSelected().equals("No Saved Maps")) {
                        game.setMap(mapSelect.getSelected());
                        game.changeState(MenuState.GAME);
                        dispose();
                    } else {
                        hide();
                    }
                } else {
                    hide();
                }
            }
        };
        return dialog;
    }

    private void showDialog(Label difficultyLevelLabel, SelectBox<String> difficultyLevelSelect, Label mapLabel, SelectBox<String> mapSelect, Dialog dialog) {
        difficultyLevelSelect.setItems("Easy", "Hard");
        List<String> maps = game.getDb().getMaps();
        if (maps.isEmpty()) {
            mapSelect.setItems("No Saved Maps");
        } else {
            mapSelect.setItems(maps.stream().toArray(String[]::new));
        }
        dialog.button("Start", true);
        dialog.button("Cancel", false);
        dialog.getContentTable().add(difficultyLevelLabel).expand().left();
        dialog.getContentTable().add(difficultyLevelSelect).width(220).height(40).expand().right();
        dialog.getContentTable().row();
        dialog.getContentTable().add(mapLabel).expand().left();
        dialog.getContentTable().add(mapSelect).width(220).height(40).expand().right();
        dialog.getTitleTable().pad(5);
        dialog.getContentTable().pad(5);
        dialog.getButtonTable().pad(5);
        dialog.show(stage);
        dialog.setWidth(400);
        dialog.setX((1920 - dialog.getWidth()) / 2);
    }

    private void addTableToStage(TextButton playButton, TextButton scoreboardButton, TextButton accountButton, TextButton settingsButton, TextButton logoutButton) {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(playButton).pad(20).padTop(50).width(1100).expand().fill();
        table.row();
        table.add(scoreboardButton).pad(20).width(1100).expand().fill();
        table.row();
        if (game.getUser() != null) {
            table.add(accountButton).pad(20).width(1100).expand().fill();
            table.row();
        }
        table.add(settingsButton).pad(20).width(1100).expand().fill();
        table.row();
        table.add(logoutButton).pad(20).padBottom(50).width(1100).expand().fill();
        stage.addActor(table);
    }

    private void setMenuChangeClick(TextButton scoreboardButton, MenuState scoreboard) {
        scoreboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.changeState(scoreboard);
                dispose();
            }
        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setUser(null);
            game.changeState(MenuState.LOGIN);
            dispose();
        }
    }
}
