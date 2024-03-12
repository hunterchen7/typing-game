package com.cs2212group9.typinggame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cs2212group9.typinggame.db.DBHelper;
import com.cs2212group9.typinggame.utils.InputListenerFactory;
import com.cs2212group9.typinggame.utils.UserAuthenticator;

public class LoginScreen implements Screen {

    final TypingGame game;
    OrthographicCamera camera;
    private final Stage stage;
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
    // TODO: don't hard code positions, make dynamic
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.top();
        table.padTop(350);

        Image logo = new Image(new Texture(Gdx.files.internal("logo.png")));
        logo.setPosition(280, 500);
        stage.addActor(logo);
        // place logo at the top of the screen

        TextField usernameField = addTextFieldRow(table, "Username:", "user");
        TextField passwordField = addTextFieldRow(table, "Password:", "asd123");

        Button loginButton = new TextButton("login", skin);
        Button registerButton = new TextButton("register", skin);

        table.row();
        table.row().padTop(10);
        table.add(loginButton).width(200).colspan(2);
        table.row();
        table.row().padTop(5);
        table.add(registerButton).width(200).colspan(2);

        // login button onclick
        loginButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            // log in logic
            // check if username and password match
            // if so, go to main menu
            // else display "username or password did not match any records"
            String username = usernameField.getText();
            String password = passwordField.getText();
            // TODO: check if username and password match

            UserAuthenticator user = new UserAuthenticator(username, password);
            if (user.authenticate()) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            } else {
                // display "username or password did not match any records"
            }

        }));

        // register button onclick
        // TODO: make it go to a registration screen
        registerButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            // register logic
            // check if in database,
            // if not, add to database
            String username = usernameField.getText();
            String password = passwordField.getText();
            UserAuthenticator user = new UserAuthenticator(username, password);

            if (user.register()) {
                // pop up to say registerd successfully
                // go to main menu
                game.setScreen(new MainMenuScreen(game));
                dispose();
            } else {
                // display "username or password already exists"
            }
        }));

        stage.addActor(table);
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
