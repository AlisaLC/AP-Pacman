package edu.sharif.ce.appacman.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;

import edu.sharif.ce.appacman.Pacman;
import edu.sharif.ce.appacman.controller.UserController;
import edu.sharif.ce.appacman.view.MenuState;

public class AccountMenu extends Menu {

    public AccountMenu(Pacman game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        Label passwordLabel = new Label("Password: ", game.getFirstSkin(), "title");
        TextField passwordInput = new TextField("", game.getFirstSkin());
        TextButton changePasswordButton = new TextButton("Change Password", game.getFirstSkin(), "big");
        TextButton deleteAccountButton = new TextButton("Delete Account", game.getFirstSkin(), "big");
        ImageButton backButton = getBackButton();
        passwordLabel.setColor(Color.WHITE);
        passwordInput.setPasswordCharacter('•');
        passwordInput.setPasswordMode(true);
        setChangePasswordClick(passwordInput, changePasswordButton);
        setDeleteAccountClick(deleteAccountButton);
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(passwordLabel).width(200).height(150).center();
        table.add(passwordInput).width(900).height(150).center();
        table.row();
        table.add(changePasswordButton).padTop(30).width(1100).colspan(2).height(200).bottom();
        table.row();
        table.add(deleteAccountButton).pad(30).width(1100).colspan(2).height(200).bottom();
        stage.addActor(table);
        stage.addActor(backButton);
    }

    private void setDeleteAccountClick(TextButton deleteAccountButton) {
        deleteAccountButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog confirmDialog = new Dialog("Do you want to delete this account?", game.getFirstSkin()) {
                    @Override
                    protected void result(Object object) {
                        if (!(object instanceof Boolean)) {
                            return;
                        }
                        Boolean result = (Boolean) object;
                        if (result) {
                            showDeleteAccountErrorDialog();
                        } else {
                            hide();
                        }
                    }
                };
                confirmDialog.button("Yes", true);
                confirmDialog.button("No", false);
                confirmDialog.show(stage);
            }
        });
    }

    private void showDeleteAccountErrorDialog() {
        Dialog dialog;
        String errorMessage = UserController.getInstance().deleteAccount(game, game.getUser().getUsername());
        if (errorMessage == null) {
            dialog = new Dialog("", game.getFirstSkin());
            dialog.text("Account Deleted Successfully!");
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
                    game.setUser(null);
                    game.changeState(MenuState.LOGIN);
                    dispose();
                }
            }, 0.5f);
        } else {
            dialog = prepareErrorDialog(errorMessage);
        }
        showDialog(dialog);
    }

    private void setChangePasswordClick(TextField passwordInput, TextButton changePasswordButton) {
        changePasswordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String errorMessage = UserController.getInstance().changePassword(game, passwordInput.getText());
                Dialog dialog;
                if (errorMessage == null) {
                    dialog = new Dialog("", game.getFirstSkin());
                    dialog.text("Password Changed Successfully!");
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
        dialog.setWidth(400);
        dialog.setX((1920 - dialog.getWidth()) / 2);
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

    private ImageButton getBackButton() {
        ImageButton backButton = new ImageButton(game.getFirstSkin());
        backButton.add(new Image(new TextureRegionDrawable(new Texture(Gdx.files.internal("back.png"))))).width(150).height(150).center();
        backButton.setWidth(170);
        backButton.setHeight(170);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.getUser() != null) {
                    game.changeState(MenuState.MAIN);
                } else {
                    game.changeState(MenuState.LOGIN);
                }
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
            if (game.getUser() != null) {
                game.changeState(MenuState.MAIN);
            } else {
                game.changeState(MenuState.LOGIN);
            }
            dispose();
        }
    }
}
