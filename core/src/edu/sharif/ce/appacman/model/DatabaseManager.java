package edu.sharif.ce.appacman.model;

import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseFactory;
import com.badlogic.gdx.sql.SQLiteGdxException;
import com.sun.javafx.collections.MappingChange;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatabaseManager {

    Database db;

    public static final String DATABASE_NAME = "pacman.db";
    public static final String TABLE_CREATE_USERS = "CREATE TABLE IF NOT EXISTS users (\n" +
            "  username text PRIMARY KEY NOT NULL," +
            "  password text NOT NULL," +
            "  score integer NOT NULL," +
            "  game_state text NOT NULL)";
    public static final String TABLE_CREATE_MAPS = "CREATE TABLE IF NOT EXISTS maps (\n" +
            "  id integer PRIMARY KEY," +
            "  game_state text NOT NULL)";

    public DatabaseManager() {
        db = DatabaseFactory.getNewDatabase(DATABASE_NAME, 1, TABLE_CREATE_USERS, null);
        db.setupDatabase();
        try {
            db.openOrCreateDatabase();
            db.execSQL(TABLE_CREATE_USERS);
            db.execSQL(TABLE_CREATE_MAPS);
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
    }

    public boolean signupUser(User user) {
        try {
            String QUERY_USER_SIGNUP = "INSERT INTO users('username', 'password', 'score', 'game_state')" +
                    " VALUES ('%s','%s',0,'')";
            db.execSQL(String.format(QUERY_USER_SIGNUP, user.username, user.password));
            return true;
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User loginUser(User user) {
        try {
            String QUERY_USER_LOGIN = "SELECT * FROM users WHERE username='%s'";
            DatabaseCursor cursor = db.rawQuery(String.format(QUERY_USER_LOGIN, user.username, user.password));
            if (cursor.next()) {
                if (user.getPassword().equals(cursor.getString(1))) {
                    user.setScore(cursor.getInt(2));
                    user.setGameState(cursor.getString(3));
                } else {
                    user.setScore(-1);
                }
                return user;
            }
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map.Entry<String, Integer>> getScoreboard() {
        try {
            String QUERY_USER_TOP = "SELECT * FROM users ORDER BY score DESC LIMIT 10";
            DatabaseCursor cursor = db.rawQuery(QUERY_USER_TOP);
            HashMap<String, Integer> results = new HashMap<>();
            while (cursor.next()) {
                results.put(cursor.getString(0), cursor.getInt(2));
            }
            return results.entrySet().stream().sorted(Comparator.comparingInt(e -> -e.getValue())).collect(Collectors.toList());
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(new HashMap<String, Integer>().entrySet());
    }

    public boolean changePassword(User user) {
        try {
            String QUERY_USER_SIGNUP = "UPDATE users SET password='%s' WHERE username='%s'";
            db.execSQL(String.format(QUERY_USER_SIGNUP, user.password, user.username));
            return true;
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changeScore(User user) {
        try {
            String QUERY_USER_SIGNUP = "UPDATE users SET score='%d', game_state='' WHERE username='%s'";
            db.execSQL(String.format(QUERY_USER_SIGNUP, user.score, user.username));
            return true;
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveGame(User user) {
        try {
            String QUERY_USER_SIGNUP = "UPDATE users SET game_state='%s' WHERE username='%s'";
            db.execSQL(String.format(QUERY_USER_SIGNUP, user.gameState, user.username));
            return true;
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAccount(String username) {
        try {
            String QUERY_USER_SIGNUP = "DELETE FROM users WHERE username='%s'";
            db.execSQL(String.format(QUERY_USER_SIGNUP, username));
            return true;
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int saveMap(String map) {
        try {
            String QUERY_MAP_SAVE = "INSERT INTO maps('game_state')" +
                    " VALUES ('%s')";
            db.execSQL(String.format(QUERY_MAP_SAVE, map));
            DatabaseCursor cursor = db.rawQuery("select last_insert_rowid()");
            if (cursor.next()) {
                return cursor.getInt(0);
            }
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<String> getMaps() {
        List<String> results = new ArrayList<>();
        try {
            String QUERY_USER_TOP = "SELECT * FROM maps";
            DatabaseCursor cursor = db.rawQuery(QUERY_USER_TOP);
            while (cursor.next()) {
                results.add(cursor.getInt(0) + "");
            }
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
        return results;
    }

    public Map.Entry<Integer, String> getMap(int id) {
        try {
            String QUERY_USER_TOP = "SELECT * FROM maps WHERE id=%d";
            DatabaseCursor cursor = db.rawQuery(String.format(QUERY_USER_TOP, id));
            HashMap<Integer, String> results = new HashMap<>();
            if (cursor.next()) {
                results.put(cursor.getInt(0), cursor.getString(1));
                return (Map.Entry<Integer, String>) results.entrySet().toArray()[0];
            }
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeGameState(User user) {
        try {
            String QUERY_USER_SIGNUP = "UPDATE users SET game_state='' WHERE username='%s'";
            db.execSQL(String.format(QUERY_USER_SIGNUP, user.username));
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
    }
}
