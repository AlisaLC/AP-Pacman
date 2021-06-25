package edu.sharif.ce.appacman.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.List;
import java.util.Map;

import edu.sharif.ce.appacman.Pacman;
import edu.sharif.ce.appacman.view.MenuState;
import edu.sharif.ce.appacman.view.ScoreboardRow;

public class ScoreboardMenu extends Menu {

    private Texture row;

    public ScoreboardMenu(Pacman game) {
        super(game);
        row = new Texture(Gdx.files.internal("scoreboard_row.png"));
    }

    @Override
    public void show() {
        super.show();
        String username = game.getUser() != null ? game.getUser().getUsername() : "";
        List<Map.Entry<String, Integer>> results = game.getDb().getScoreboard();
        ImageButton backButton = getBackButton();
        Label title = new Label("Scoreboard", game.getFirstSkin(), "title");
        Table table = prepareTable(username, results, title);
        stage.addActor(table);
        stage.addActor(backButton);
    }

    private Table prepareTable(String username, List<Map.Entry<String, Integer>> results, Label title) {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(title).center().padTop(30);
        int lastScore = -1;
        int scoreCounter = 0;
        for (int i = 0; i < results.size(); i++) {
            table.row();
            if (results.get(i).getValue().equals(lastScore)) scoreCounter++;
            else {
                scoreCounter = 0;
                lastScore = results.get(i).getValue();
            }
            table.add(new ScoreboardRow(i + 1 - scoreCounter, results.get(i).getKey(),
                    results.get(i).getValue(), results.get(i).getKey().equals(username),
                    row, game.getFirstSkin())).pad(20).expand().fill().center();
        }
        table.padBottom(30);
        return table;
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
