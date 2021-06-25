package edu.sharif.ce.appacman;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import edu.sharif.ce.appacman.model.DatabaseManager;
import edu.sharif.ce.appacman.model.Settings;
import edu.sharif.ce.appacman.model.User;
import edu.sharif.ce.appacman.view.Background;
import edu.sharif.ce.appacman.view.MapMakerMap;
import edu.sharif.ce.appacman.view.MenuState;
import edu.sharif.ce.appacman.view.menu.AccountMenu;
import edu.sharif.ce.appacman.view.menu.GameMenu;
import edu.sharif.ce.appacman.view.menu.LoginMenu;
import edu.sharif.ce.appacman.view.menu.MainMenu;
import edu.sharif.ce.appacman.view.menu.MapMakerMenu;
import edu.sharif.ce.appacman.view.menu.ScoreboardMenu;
import edu.sharif.ce.appacman.view.menu.SettingsMenu;
import lombok.Getter;
import lombok.Setter;

public class Pacman extends Game {

    public static final int WORLD_WIDTH = 1920;
    public static final int WORLD_HEIGHT = 1080;
    @Getter
    private DatabaseManager db;
    @Getter
    private Settings settings;
    @Getter
    @Setter
    private User user;
    @Getter
    MenuState state;
    @Getter
    @Setter
    private boolean isHard;
    @Getter
    @Setter
    private String map;
    @Getter
    private Music backgroundMusic;
    @Getter
    private Music gameMusic;
    @Getter
    private Skin firstSkin;
    @Getter
    private Skin secondSkin;
    @Getter
    private Skin thirdSkin;
    @Getter
    private Background background;
    private LoginMenu loginMenu;
    private MainMenu mainMenu;
    private GameMenu gameMenu;
    private ScoreboardMenu scoreboardMenu;
    private AccountMenu accountMenu;
    private SettingsMenu settingsMenu;
    private MapMakerMenu mapMakerMenu;

    public void changeState(MenuState state) {
        this.state = state;
        switch (state) {
            case LOGIN:
                if (loginMenu == null) loginMenu = new LoginMenu(this);
                setScreen(loginMenu);
                break;
            case MAIN:
                if (mainMenu == null) mainMenu = new MainMenu(this);
                setScreen(mainMenu);
                break;
            case GAME:
                if (gameMenu == null) gameMenu = new GameMenu(this);
                setScreen(gameMenu);
                break;
            case SCOREBOARD:
                if (scoreboardMenu == null) scoreboardMenu = new ScoreboardMenu(this);
                setScreen(scoreboardMenu);
                break;
            case ACCOUNT:
                if (accountMenu == null) accountMenu = new AccountMenu(this);
                setScreen(accountMenu);
                break;
            case SETTINGS:
                if (settingsMenu == null) settingsMenu = new SettingsMenu(this);
                setScreen(settingsMenu);
                break;
            case MAP_MAKER:
                if (mapMakerMenu == null) mapMakerMenu = new MapMakerMenu(this);
                setScreen(mapMakerMenu);
                break;
        }
    }

    public void disposeCurrent() {
        switch (state) {
            case GAME:
                gameMenu.dispose();
                break;
            case MAIN:
                mainMenu.dispose();
        }
    }

    @Override
    public void create() {
        db = new DatabaseManager();
        firstSkin = new Skin(Gdx.files.internal("sgx/skin/sgx-ui.json"));
        secondSkin = new Skin(Gdx.files.internal("star-soldier/skin/star-soldier-ui.json"));
        thirdSkin = new Skin(Gdx.files.internal("arcade/skin/arcade-ui.json"));
        background = new Background();
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("menu_music.mp3"));
        backgroundMusic.play();
        backgroundMusic.setLooping(true);
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("hard_music.mp3"));
        gameMusic.setLooping(true);
        firstSkin.getFont("medium").getData().setScale(1.5f);
        firstSkin.getFont("font").getData().setScale(1.5f);
        firstSkin.getFont("small").getData().setScale(1.f);
        settings = new Settings();
        backgroundMusic.setVolume(settings.getMusicLevel());
        gameMusic.setVolume(settings.getMusicLevel());
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        changeState(MenuState.LOGIN);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        firstSkin.dispose();
        backgroundMusic.dispose();
    }
}
