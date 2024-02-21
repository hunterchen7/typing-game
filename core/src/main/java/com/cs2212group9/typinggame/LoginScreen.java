package com.cs2212group9.typinggame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cs2212group9.typinggame.utils.InputListenerFactory;

public class LoginScreen implements Screen {

    final TypingGame game;
    OrthographicCamera camera;
    private final Stage stage;
    private Table table;
    private final Viewport viewport;
    private Skin skin;


    public LoginScreen(final TypingGame gam) {
        game = gam;

        camera = new OrthographicCamera();

        viewport = new FitViewport(1200, 800, camera);
        viewport.apply();

        camera.position.set(1200, 800, 0);
        camera.update();

        stage = new Stage();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
    }

    @Override
    public void render(float delta) {
        // Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();

        Image logo = new Image(new Texture(Gdx.files.internal("logo.png")));
        mainTable.add(logo).colspan(2);
        mainTable.row();

        TextField usernameField = addTextFieldRow(mainTable, "Username:", "user");
        mainTable.row();

        mainTable.padTop(100);

        TextField passwordField = addTextFieldRow(mainTable, "Password:", "asd123");

        Button loginButton = new TextButton("login", skin);
        mainTable.row().padTop(10);

        Button registerButton = new TextButton("register", skin);

        mainTable.row();
        mainTable.row().padTop(10);
        mainTable.add(loginButton).width(200).colspan(2);
        mainTable.row();
        mainTable.row().padTop(5);
        mainTable.add(registerButton).width(200).colspan(2);

        loginButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            // log in logic
            // check if username and password match
            // if so, go to main menu
            // else display "username or password did not match any records"
            String username = usernameField.getText();
            String password = passwordField.getText();
            // TODO: check if username and password match
            if (!username.isEmpty() || !password.isEmpty()) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            } else {
                // display "username or password did not match any records"
            }

        }));

        registerButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            // register logic
            // check if in database,
            // if not, add to database
            String username = usernameField.getText();
            String password = passwordField.getText();
            // TODO: check if username/pw already exists
            if (!username.isEmpty() || !password.isEmpty()) {
                // add to database
            } else {
                // display "username or password already exists"
            }
        }));

        stage.addActor(mainTable);
    }

    private TextField addTextFieldRow(final Table table, String labelText, String defaultValue) {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        final Label label = new Label(labelText, skin);
        final TextField text = new TextField(defaultValue, skin);

        table.add(label).width(70);
        table.add(text).width(250);
        table.row().padTop(5);

        return text;
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
