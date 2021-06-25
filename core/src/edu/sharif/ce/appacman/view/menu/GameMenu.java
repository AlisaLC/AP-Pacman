package edu.sharif.ce.appacman.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.sharif.ce.appacman.Pacman;
import edu.sharif.ce.appacman.model.GameState;
import edu.sharif.ce.appacman.model.MapGraph;
import edu.sharif.ce.appacman.model.Point;
import edu.sharif.ce.appacman.view.GhostPlayer;
import edu.sharif.ce.appacman.view.MapMakerMap;
import edu.sharif.ce.appacman.view.MapMakerTile;
import edu.sharif.ce.appacman.view.MenuState;
import edu.sharif.ce.appacman.view.PacmanPlayer;

public class GameMenu extends Menu {

    MapMakerMap map;
    PacmanPlayer pacmanPlayer;
    GhostPlayer[] ghosts;
    MapGraph mapGraph;
    int livesCount;
    int coinCount;
    int score;
    Label scoreLabel, resumeLabel;
    Table livesTable;
    boolean isPause;
    int frightEatCounter = 0;
    Sound deathSound, eatCoinSound, eatEnergySound, eatGhostSound, lostSound;
    float SFX_VOLUME = 0.5f;
    Json json;
    TextureAtlas atlas;

    public GameMenu(Pacman game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        initializeSettings();
        List<Point> points = initializeMap();
        initializePacmanPlayer(points);
        initializeGhosts(points);
        Touchpad touchpad = getTouchpad();
        setScoreLabel();
        stage.addActor(map);
        stage.addActor(touchpad);
        for (int i = 0; i < 4; i++) {
            stage.addActor(ghosts[i]);
        }
        stage.addActor(pacmanPlayer);
        stage.addActor(livesTable);
        stage.addActor(scoreLabel);
        stage.addActor(resumeLabel);
        if (game.getUser() != null && game.getUser().getGameState() != null && !game.getUser().getGameState().equals("")) {
            setGameState(json.fromJson(GameState.class, game.getUser().getGameState()));
        }
    }

    private void initializeSettings() {
        deathSound = Gdx.audio.newSound(Gdx.files.internal("SFX/death.mp3"));
        eatCoinSound = Gdx.audio.newSound(Gdx.files.internal("SFX/eat_coin.mp3"));
        eatEnergySound = Gdx.audio.newSound(Gdx.files.internal("SFX/eat_energy.mp3"));
        eatGhostSound = Gdx.audio.newSound(Gdx.files.internal("SFX/eat_ghost.mp3"));
        lostSound = Gdx.audio.newSound(Gdx.files.internal("SFX/lost.mp3"));
        livesCount = game.getSettings().getMaxLives();
        SFX_VOLUME = game.getSettings().getSFXLevel();
        if (game.isHard()) {
            game.getBackgroundMusic().pause();
            game.getGameMusic().play();
        }
        json = new Json();
        atlas = new TextureAtlas(Gdx.files.internal("map/pacman.atlas"));
    }

    private void initializeGhosts(List<Point> points) {
        ghosts = new GhostPlayer[4];
        for (int i = 0; i < 4; i++) {
            Point point = points.get(points.size() - i - 1);
            ghosts[i] = new GhostPlayer(map, (int) map.getTile(point.getX(),
                    point.getY()).getRectangle().getX(), (int) map.getTile(point.getX(),
                    point.getY()).getRectangle().getY(), i, game.isHard(), mapGraph);
        }
    }

    private void initializePacmanPlayer(List<Point> points) {
        pacmanPlayer = new PacmanPlayer(map, (int) map.getTile(points.get(0).getX(),
                points.get(0).getY()).getRectangle().getX(), (int) map.getTile(points.get(0).getX(),
                points.get(0).getY()).getRectangle().getY(), mapGraph);
        map.setPacmanPlayer(pacmanPlayer);
        map.getTile(points.get(0).getX(), points.get(0).getY()).setEaten(true);
    }

    private List<Point> initializeMap() {
        String mapCode = game.getDb().getMap(Integer.parseInt(game.getMap())).getValue();
        int mapWidth = mapCode.split("\r?\n")[0].length();
        int pixelSize = 980 / mapWidth;
        map = new MapMakerMap(mapWidth, (1920 - mapWidth * pixelSize) / 2, (1080 - mapWidth * pixelSize) / 2);
        map.setMap(mapCode);
        List<Point> points = new ArrayList<>();
        for (int i = 1; i < mapWidth - 1; i++) {
            for (int j = 1; j < mapWidth - 1; j++) {
                if (!map.getTile(i, j).isBlock()) {
                    points.add(new Point(i, j));
                }
            }
        }
        coinCount = points.size() - 1;
        Collections.shuffle(points);
        for (int i = 0; i < points.size() / (game.isHard() ? 20 : 50); i++) {
            map.getTile(points.get(i).getX(), points.get(i).getY()).setEnergyBomb(true);
            coinCount--;
        }
        map.updateWholeMap();
        mapGraph = new MapGraph(map);
        int center = (mapWidth - 1) / 2;
        points = points.stream().sorted(Comparator.comparingInt(e -> (int) (Math.pow(e.getX() - center, 2)
                + Math.pow(e.getY() - center, 2)))).collect(Collectors.toList());
        return points;
    }

    private void setScoreLabel() {
        scoreLabel = new Label("Score: 0", game.getFirstSkin(), "title");
        scoreLabel.setX(960 + map.MAP_SQUARE_WIDTH * map.MAP_PIXEL_SIZE / 2 + 50);
        scoreLabel.setY(600);
        resumeLabel = new Label("Move to resume", game.getFirstSkin(), "title");
        resumeLabel.setColor(Color.RED);
        resumeLabel.setX(scoreLabel.getX());
        resumeLabel.setY(scoreLabel.getY() - 80);
        resumeLabel.setVisible(false);
        livesTable = new Table();
        livesTable.left();
        for (int i = 0; i < livesCount; i++) {
            TextureRegion pacmanTexture = atlas.findRegion("45");
            Image pacman = new Image(pacmanTexture);
            livesTable.add(pacman).pad(5).width(48).height(48).center();
        }
        livesTable.setX(scoreLabel.getX());
        livesTable.setY(scoreLabel.getY() + 80);
    }

    private Touchpad getTouchpad() {
        Touchpad touchpad = new Touchpad(10, game.getThirdSkin(), "red");
        touchpad.setWidth(300);
        touchpad.setHeight(300);
        touchpad.setX(100);
        touchpad.setY(350);
        Drawable touchpadNone = game.getThirdSkin().getDrawable("joystick-red");
        Drawable touchpadUp = game.getThirdSkin().getDrawable("joystick-u-red");
        Drawable touchpadRight = game.getThirdSkin().getDrawable("joystick-r-red");
        Drawable touchpadDown = game.getThirdSkin().getDrawable("joystick-d-red");
        Drawable touchpadLeft = game.getThirdSkin().getDrawable("joystick-l-red");
        Touchpad.TouchpadStyle style = new Touchpad.TouchpadStyle();
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.UP:
                        style.background = touchpadUp;
                        break;
                    case Input.Keys.RIGHT:
                        style.background = touchpadRight;
                        break;
                    case Input.Keys.DOWN:
                        style.background = touchpadDown;
                        break;
                    case Input.Keys.LEFT:
                        style.background = touchpadLeft;
                        break;
                }
                return super.keyDown(event, keycode);
            }
        });
        style.background = touchpadNone;
        touchpad.setStyle(style);
        setTouchpadEvent(touchpad, touchpadNone, touchpadUp, touchpadRight, touchpadDown, touchpadLeft, style);
        return touchpad;
    }

    private void setTouchpadEvent(Touchpad touchpad, Drawable touchpadNone, Drawable touchpadUp, Drawable touchpadRight, Drawable touchpadDown, Drawable touchpadLeft, Touchpad.TouchpadStyle style) {
        touchpad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float deltaX = touchpad.getKnobPercentX();
                float deltaY = touchpad.getKnobPercentY();
                int direction = -1;
                if (Math.abs(deltaX) < 0.4 && Math.abs(deltaY) < 0.4) {
                    style.background = touchpadNone;
                } else {
                    if (isPause) resumeGame();
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        if (deltaX > 0) {
                            style.background = touchpadRight;
                            direction = 1;
                        } else {
                            style.background = touchpadLeft;
                            direction = 3;
                        }
                    } else {
                        if (deltaY > 0) {
                            style.background = touchpadUp;
                            direction = 0;
                        } else {
                            style.background = touchpadDown;
                            direction = 2;
                        }
                    }
                }
                if (direction > -1) {
                    pacmanPlayer.setDirection(direction);
                }
            }
        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (!isPause) {
            String scoreAddition = "";
            scoreAddition = applyCoinEffect(scoreAddition);
            scoreAddition = checkForCollision(scoreAddition);
            isGameFinished();
            handleBackEvent();
            scoreLabel.setText("Score: " + score + " " + scoreAddition);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
                Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) ||
                Gdx.input.isKeyJustPressed(Input.Keys.DOWN) ||
                Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            resumeGame();
        }
    }

    private void resumeGame() {
        isPause = false;
        pacmanPlayer.resume();
        for (int i = 0; i < 4; i++) {
            ghosts[i].resume();
        }
        resumeLabel.setVisible(false);
    }

    private void handleBackEvent() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            pauseGameDialog();
        }
    }

    private void pauseGameDialog() {
        pauseGame();
        Dialog dialog = new Dialog("Pause", game.getFirstSkin(), "dialog") {
            @Override
            protected void result(Object object) {
                hide();
                boolean result = (Boolean) object;
                if (result) {
                    resumeGame();
                } else {
                    if (game.getUser() != null) {
                        game.getUser().setGameState(json.toJson(getGameState()));
                        game.getDb().saveGame(game.getUser());
                    }
                    game.changeState(MenuState.MAIN);
                    dispose();
                }
            }
        };
        dialog.text("Game Paused");
        dialog.button("Resume", true);
        dialog.button("Exit", false);
        dialog.show(stage);
    }

    private void pauseGame() {
        pacmanPlayer.pause();
        for (int i = 0; i < 4; i++) {
            ghosts[i].pause();
        }
        isPause = true;
        resumeLabel.setVisible(true);
    }

    private void isGameFinished() {
        if (livesCount == 0) {
            pacmanPlayer.pause();
            for (int i = 0; i < 4; i++) {
                ghosts[i].pause();
            }
            if (game.getUser() != null && score > game.getUser().getScore()) {
                game.getUser().setScore(score);
                game.getDb().changeScore(game.getUser());
            } else if (game.getUser() != null) {
                game.getUser().setGameState("");
                game.getDb().removeGameState(game.getUser());
            }
            pauseGame();
            Dialog dialog = new Dialog("", game.getFirstSkin(), "dialog") {
                @Override
                protected void result(Object object) {
                    hide();
                    boolean result = (Boolean) object;
                    if (result) {
                        dispose();
                        game.changeState(MenuState.MAIN);
                        game.disposeCurrent();
                        game.changeState(MenuState.GAME);
                    } else {
                        dispose();
                        game.changeState(MenuState.MAIN);
                    }
                }
            };
            dialog.text("U Lost!\nScore: " + score);
            dialog.button("Try Again", true);
            dialog.button("Exit", false);
            lostSound.play(SFX_VOLUME);
            dialog.show(stage);
        }
    }

    private String checkForCollision(String scoreAddition) {
        for (int i = 0; i < 4; i++) {
            if (pacmanPlayer.rectangle.overlaps(ghosts[i].rectangle) && !ghosts[i].isInvisible()) {
                if (ghosts[i].isFright()) {
                    ghosts[i].brave();
                    ghosts[i].reset();
                    ghosts[i].invisible(5f);
                    score += (game.isHard() ? 5 : 1) * 200 * (++frightEatCounter);
                    scoreAddition = "+" + ((game.isHard() ? 5 : 1) * (200 * frightEatCounter));
                    eatGhostSound.play(SFX_VOLUME);
                } else {
                    livesTable.getCells().get(livesCount - 1).getActor().setVisible(false);
                    livesCount--;
                    for (int j = 0; j < 4; j++) {
                        ghosts[j].reset();
                    }
                    pacmanPlayer.updatePointByPosition();
                    deathSound.play(SFX_VOLUME);
                    pauseGame();
                    break;
                }
            }
        }
        return scoreAddition;
    }

    private String applyCoinEffect(String scoreAddition) {
        MapMakerTile currentTile = map.getTile(pacmanPlayer.currentPosition.getX(), pacmanPlayer.currentPosition.getY());
        if (!currentTile.isBlock() && !currentTile.isEaten()) {
            currentTile.setEaten(true);
            if (!currentTile.isEnergyBomb()) {
                score += (game.isHard() ? 5 : 1) * 5;
                scoreAddition = "+" + ((game.isHard() ? 5 : 1) * 5);
                coinCount--;
                if (coinCount == 0) {
                    resetCoins();
                }
                eatCoinSound.play(SFX_VOLUME);
            }
            if (currentTile.isEnergyBomb()) {
                boolean canReset = true;
                for (int i = 0; i < 4; i++) {
                    if (ghosts[i].isFright()) {
                        canReset = false;
                    }
                }
                if (canReset) frightEatCounter = 0;
                for (int i = 0; i < 4; i++) {
                    ghosts[i].fright();
                }
                eatEnergySound.play(SFX_VOLUME);
            }
        }
        return scoreAddition;
    }

    private void resetCoins() {
        for (int i = 0; i < map.MAP_SQUARE_WIDTH; i++) {
            for (int j = 0; j < map.MAP_SQUARE_WIDTH; j++) {
                if (!map.getTile(i, j).isBlock()) {
                    map.getTile(i, j).setEaten(false);
                    coinCount++;
                }
            }
        }
        map.getTile(pacmanPlayer.currentPosition.getX(), pacmanPlayer.currentPosition.getY()).setEaten(true);
        livesCount++;
        if (livesCount > livesTable.getCells().size) {
            TextureRegion pacmanTexture = atlas.findRegion("45");
            Image pacman = new Image(pacmanTexture);
            livesTable.add(pacman).pad(5).width(48).height(48).center();
        } else {
            livesTable.getCells().get(livesCount - 1).getActor().setVisible(true);
        }
        isPause = true;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (game.isHard()) {
            game.getGameMusic().stop();
            game.getBackgroundMusic().play();
        }
        frightEatCounter = 0;
        isPause = false;
        score = 0;
        coinCount = 0;
        livesCount = 0;
    }

    private GameState getGameState() {
        return new GameState(map, pacmanPlayer, ghosts, score, livesCount, game.getMap(), game.isHard());
    }

    private void setGameState(GameState state) {
        if (state.getMapName().equals(game.getMap()) && game.isHard() == state.isHard()) {
            map.setMap(state.getMap());
            pacmanPlayer.currentPosition.setX(state.getPacmanLocation().getX());
            pacmanPlayer.currentPosition.setY(state.getPacmanLocation().getY());
            pacmanPlayer.updatePointByPosition();
            for (int i = 0; i < 4; i++) {
                if (state.getInvisibleTimes()[i] > 0) {
                    ghosts[i].invisible(state.getInvisibleTimes()[i]);
                } else {
                    ghosts[i].visible();
                }
                if (state.getFrightTimes()[i] > 0) {
                    ghosts[i].fright();
                    ghosts[i].frightTime = state.getFrightTimes()[i];
                }
                ghosts[i].currentPosition.setX(state.getGhostLocation()[i].getX());
                ghosts[i].currentPosition.setY(state.getGhostLocation()[i].getY());
                ghosts[i].updatePointByPosition();
            }
            score = state.getScore();
            livesCount = state.getLivesCount();
            pauseGameDialog();
        }
    }

    @Override
    public void pause() {
        super.pause();
        game.getGameMusic().pause();
        pauseGameDialog();
    }

    @Override
    public void resume() {
        if (game.isHard()) {
            game.getGameMusic().play();
        } else {
            game.getBackgroundMusic().play();
        }
    }


}
