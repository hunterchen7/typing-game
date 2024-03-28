package com.cs2212group9.typinggame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cs2212group9.typinggame.db.DBHelper;
import com.cs2212group9.typinggame.utils.InputListenerFactory;
import com.cs2212group9.typinggame.utils.UserAuthenticator;
/**
 * This class is mainly responsible for the login interface of the game.
 * @author Group 9 members
 */
public class LoginScreen implements Screen {

    final TypingGame game;
    OrthographicCamera camera;
    private final Stage stage;
    private final Viewport viewport;
    private Skin skin;
    private final Music music = Gdx.audio.newMusic(Gdx.files.internal("audio/TownTheme.mp3"));

    /** Constructor for the LoginScreen, initializes camera & viewport, and sets up button skins
     * @param gam - the game object
     */
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

        if (Gdx.input.isKeyPressed(Input.Keys.F5) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            dispose();
            game.setScreen(new LoginScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    /**
     * Show the login screen, set up the stage and add actors.
     * add buttons and text fields for username and password
     */
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
		//======!!!======The password entered by the user needs to be displayed as * to prevent the password from being peeped.
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
          	//======!!!======When the account and password do not match, the information does not seem to be prompted correctly.
            String username = usernameField.getText();
            String password = passwordField.getText();

            UserAuthenticator user = new UserAuthenticator(username, password);
            if (user.authenticate()) {
                System.out.println("authenticated user: " + username);
                this.game.setUsername(username);
                game.setScreen(new MainMenuScreen(game));
                dispose();
            } else {
                // display "username or password did not match any records"
            }

        }));

        // register button onclick
        // TODO: make it go to a registration screen
      	//======!!!======The Enter key can also be equivalent to clicking the currently selected button
        registerButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            // register logic
            // check if in database,
            // if not, add to database
            String username = usernameField.getText();
            String password = passwordField.getText();
            UserAuthenticator user = new UserAuthenticator(username, password);

            if (user.register()) {
                // pop up to say registered successfully
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
        music.dispose();
        stage.dispose();
    }
}
//======!!!======When you enter the correct password and account to log in, it should not show that the user already exists

