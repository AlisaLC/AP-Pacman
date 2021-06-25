package edu.sharif.ce.appacman.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Settings {

    private Preferences preferences;

    private final String PREFERENCES_NAME = "settings";

    public Settings() {
        preferences = Gdx.app.getPreferences(PREFERENCES_NAME);
    }

    public int getMaxLives() {
        return preferences.getInteger("max_lives", 3);
    }

    public void setMaxLives(int maxLives) {
        preferences.putInteger("max_lives", maxLives);
        preferences.flush();
    }

    public float getMusicLevel() {
        return preferences.getFloat("music_level", 0.2f);
    }

    public void setMusicLevel(float musicLevel) {
        preferences.putFloat("music_level", musicLevel);
        preferences.flush();
    }

    public float getSFXLevel() {
        return preferences.getFloat("sfx_level", 0.2f);
    }

    public void setSFXLevel(float musicLevel) {
        preferences.putFloat("sfx_level", musicLevel);
        preferences.flush();
    }

    public int getMapWidth() {
        return preferences.getInteger("map_width", 19);
    }

    public void setMapWidth(int mapWidth) {
        preferences.putInteger("map_width", mapWidth);
        preferences.flush();
    }
}
