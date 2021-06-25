package edu.sharif.ce.appacman.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;

import edu.sharif.ce.appacman.Pacman;
import edu.sharif.ce.appacman.controller.UserController;
import edu.sharif.ce.appacman.view.MenuState;

public class LoginMenu extends Menu {

    public LoginMenu(Pacman game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        Label usernameLabel = new Label("Username: ", game.getFirstSkin(), "title");
        TextField usernameInput = new TextField("", game.getFirstSkin(), "big");
        Label passwordLabel = new Label("Password: ", game.getFirstSkin(), "title");
        TextField passwordInput = new TextField("", game.getFirstSkin());
        TextButton loginButton = new TextButton("Login", game.getFirstSkin(), "big");
        TextButton signUpButton = new TextButton("Sign Up", game.getFirstSkin(), "big");
        TextButton loginDefaultButton = new TextButton("Enter without a user", game.getFirstSkin(), "big");
        TextButton exitButton = new TextButton("Exit", game.getFirstSkin(), "big");
        prepareTextFields(usernameLabel, usernameInput, passwordLabel, passwordInput);
        setLoginClick(usernameInput, passwordInput, loginButton);
        setSignupClick(usernameInput, passwordInput, signUpButton);
        setDefaultLoginClick(loginDefaultButton);
        setExitClick(exitButton);
        addTableToStage(usernameLabel, usernameInput, passwordLabel, passwordInput, loginButton, signUpButton, loginDefaultButton, exitButton);
    }

    private void setDefaultLoginClick(TextButton loginDefaultButton) {
        loginDefaultButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.changeState(MenuState.MAIN);
                dispose();
            }
        });
    }

    private void addTableToStage(Label usernameLabel, TextField usernameInput, Label passwordLabel, TextField passwordInput, TextButton loginButton, TextButton signUpButton, TextButton loginDefaultButton, TextButton exitButton) {
        Table mainTable = new Table();
        Table fieldsTable = new Table();
        Table buttonsTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        fieldsTable.add(usernameLabel);
        fieldsTable.add(usernameInput).expand().fill();
        fieldsTable.row();
        fieldsTable.add(passwordLabel).padTop(15);
        fieldsTable.add(passwordInput).padTop(15).expand().fill();
        buttonsTable.add(loginButton).expand().pad(15).fill();
        buttonsTable.row();
        buttonsTable.add(signUpButton).expand().pad(15).fill();
        buttonsTable.row();
        buttonsTable.add(loginDefaultButton).expand().pad(15).fill();
        buttonsTable.row();
        buttonsTable.add(exitButton).expand().pad(15).fill();
        mainTable.add(fieldsTable).width(1100).padTop(50).height(300).padBottom(50).expand().fill();
        mainTable.row();
        mainTable.add(buttonsTable).width(1100).padBottom(30).height(550).expand().fill();
        stage.addActor(mainTable);
    }

    private void prepareTextFields(Label usernameLabel, TextField usernameInput, Label passwordLabel, TextField passwordInput) {
        usernameLabel.setColor(Color.WHITE);
        usernameInput.setTextFieldListener((textField, c) -> {
            textField.setText(textField.getText().toLowerCase());
            textField.setCursorPosition(textField.getText().length());
        });
        passwordLabel.setColor(Color.WHITE);
        passwordInput.setPasswordCharacter('•');
        passwordInput.setPasswordMode(true);
    }

    private void setExitClick(TextButton exitButton) {
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    private void setSignupClick(TextField usernameInput, TextField passwordInput, TextButton signUpButton) {
        signUpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String errorMessage = UserController.getInstance().signup(game, usernameInput.getText(), passwordInput.getText());
                Dialog dialog;
                if (errorMessage == null) {
                    dialog = new Dialog("", game.getFirstSkin());
                    dialog.text("Signed Up Successfully!");
                } else {
                    dialog = prepareErrorDialog(errorMessage);
                }
                showDialog(dialog);
            }
        });
    }

    private void setLoginClick(TextField usernameInput, TextField passwordInput, TextButton loginButton) {
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String errorMessage = UserController.getInstance().login(game, usernameInput.getText(), passwordInput.getText());
                Dialog dialog;
                if (errorMessage == null) {
                    dialog = new Dialog("", game.getFirstSkin());
                    dialog.text("Logged In Successfully!");
                    Timer timer = new Timer();
                    timer.scheduleTask((new Timer.Task() {
                        @Override
                        public void run() {
                            dialog.hide();
                        }
                    }), 0.15f);
                    timer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            game.changeState(MenuState.MAIN);
                            dispose();
                        }
                    }, 0.5f);
                } else {
                    dialog = prepareErrorDialog(errorMessage);
                }
                showDialog(dialog);
            }
        });
    }

    private void showDialog(Dialog dialog) {
        dialog.button("OK");
        dialog.getTitleTable().pad(5);
        dialog.getContentTable().pad(5);
        dialog.getButtonTable().pad(5);
        dialog.show(stage);
    }

    private Dialog prepareErrorDialog(String errorMessage) {
        Dialog dialog = new Dialog("ERROR", game.getFirstSkin(), "dialog");
        dialog.getTitleTable().clear();
        Label title = new Label("ERROR!", game.getFirstSkin());
        title.setColor(Color.BLACK);
        dialog.getTitleTable().add(title).center();
        dialog.text(errorMessage);
        return dialog;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            Dialog dialog = new Dialog("Exit", game.getFirstSkin(), "dialog") {
                @Override
                protected void result(Object object) {
                    boolean result = (Boolean) object;
                    if (result) {
                        Gdx.app.exit();
                    } else {
                        hide();
                    }
                }
            };
            dialog.text("Are you sure you want to exit?");
            dialog.button("Confirm", true);
            dialog.button("Cancel", false);
            dialog.show(stage);
        }
    }
}
