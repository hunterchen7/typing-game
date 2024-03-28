package com.cs2212group9.typinggame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
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
    private final Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
    private final Music music = Gdx.audio.newMusic(Gdx.files.internal("audio/TownTheme.mp3"));
    private boolean usernameReset = false;
    private Texture backgroundTexture;

    /**
     * Constructor for the LoginScreen, initializes camera & viewport, and sets up button skins
     *
     * @param gam - the game object
     */

    public LoginScreen(final TypingGame gam) {
        this.game = gam;

        camera = new OrthographicCamera();

        viewport = new FitViewport(1200, 800, camera);
        viewport.apply();

        camera.position.set(1200, 800, 0);
        camera.update();

        stage = new Stage(viewport, game.batch);

        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        // Draw the background texture. Adjust the positioning and sizing as needed.
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

        // Call stage.act() and stage.draw() after drawing the background
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
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
        // text to show the user that the username or password is incorrect or already exists
        Label errorLabel = new Label("", skin);
        errorLabel.setColor(1, 0, 0, 1);

        Image logo = new Image(new Texture(Gdx.files.internal("logo.png")));
        logo.setPosition(280, 550);
        stage.addActor(logo);
        // place logo at the top of the screen

        TextField usernameField = addTextFieldRow(table, "Username:", "user", 10);
        usernameField.setSize(250, 80);
        // this method of storing a password is actually quite unsecure, but I don't aim for this to be that secure
        // So I won't change it. the reason for it is when the JVM segfaults (possible with JNI), contents of memory
        // gets dumped into a file, and if the password is stored in memory as a string, it can be read from that file.
        // char arrays are usually used because they can be dumped, like in Swing.
        TextField passwordField = addTextFieldRow(table, "Password (optional):", "", 125);
        passwordField.setSize(250, 80);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        // clear default text on click
        usernameField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!usernameReset) {
                    usernameField.setText("");
                    usernameReset = true;
                }
                super.clicked(event, x, y);
            }
        });

        Button loginButton = new TextButton("login", skin);
        Button registerButton = new TextButton("register", skin);

        table.row();
        table.row().padTop(15);
        table.add(loginButton).width(200).colspan(2);
        table.row();
        table.row().padTop(10);
        table.add(registerButton).width(200).colspan(2);

        // login button onclick
        loginButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            // log in logic
            // check if username and password match
            // if so, go to main menu
            // else display "username or password did not match any records"
            String username = usernameField.getText();
            String password = passwordField.getText();

            UserAuthenticator user = new UserAuthenticator(username, password);
            if (user.authenticate()) {
                System.out.println("authenticated user: " + username);
                this.game.setUsername(username);
                game.setScreen(new MainMenuScreen(game));
                dispose();
            } else {
                System.out.println("failed to authenticate user: " + username);
                errorLabel.setText("username or password is incorrect");
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

            if (username.isBlank()) {
                errorLabel.setText("Username cannot be blank");
            } else if (username.length() > 20) {
                errorLabel.setText("Username cannot be longer than 20 characters");
            } else if (user.register()) {
                this.game.setUsername(username);
                // pop up to say registered successfully
                // go to main menu
                game.setScreen(new MainMenuScreen(game));
                dispose();
            } else {
                System.out.println("failed to register user: " + username);
                errorLabel.setText("Username already exists");
            }
        }));

        table.row().padTop(10);
        table.add(errorLabel).colspan(2);

        stage.addActor(table);
    }

    private TextField addTextFieldRow(final Table table, String labelText, String defaultValue, int labelWidth) {
        final Label label = new Label(labelText, skin);
        final TextField text = new TextField(defaultValue, skin);

        table.add(label).width(labelWidth);
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
        stage.dispose();
        music.dispose();
        backgroundTexture.dispose(); // Dispose resources owned by this screen
    }
}

