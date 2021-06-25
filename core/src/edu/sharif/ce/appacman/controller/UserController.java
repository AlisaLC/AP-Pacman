package edu.sharif.ce.appacman.controller;

import edu.sharif.ce.appacman.Pacman;
import edu.sharif.ce.appacman.model.User;
import lombok.Getter;

public class UserController {

    @Getter
    private static UserController instance;

    static {
        instance = new UserController();
    }

    public String signup(Pacman game, String username, String password) {
        if (!username.matches("\\w{5,30}")) {
            return "Username invalid!";
        }
        if (!password.matches("\\w{5,30}")) {
            return "Password invalid or weak!";
        }
        User user = new User();
        user.setUsername(username.toLowerCase());
        user.setPassword(password);
        if (game.getDb().signupUser(user)) {
            return null;
        }
        return "Username already exists!";
    }

    public String login(Pacman game, String username, String password) {
        User user = new User();
        user.setUsername(username.toLowerCase());
        user.setPassword(password);
        if ((user = game.getDb().loginUser(user)) != null) {
            if (user.getScore() == -1) {
                return "Wrong password!";
            } else {
                game.setUser(user);
                return null;
            }
        } else {
            return "Username doesn't exist!";
        }
    }

    public String changePassword(Pacman game, String password) {
        if (!password.matches("\\w{5,30}")) {
            return "Password invalid or weak!";
        }
        User user = new User();
        user.setUsername(game.getUser().getUsername().toLowerCase());
        user.setPassword(password);
        if (game.getDb().changePassword(user)) {
            return null;
        }
        return "Can't change password!";
    }

    public String deleteAccount(Pacman game, String username) {
        if (game.getDb().deleteAccount(username)) {
            return null;
        }
        return "Can't change password!";
    }
}
