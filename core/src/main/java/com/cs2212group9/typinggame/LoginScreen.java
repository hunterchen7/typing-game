package com.cs2212group9.typinggame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cs2212group9.typinggame.utils.InputListenerFactory;
import com.cs2212group9.typinggame.utils.UserAuthenticator;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * This class is mainly responsible for the login interface of the game.
 * @author Group 9 members
 * @version 1.0
 */
public class LoginScreen implements Screen {
    /** The instance of the TypingGame class that controls the game logic */
    final TypingGame game;
    /** The camera used to manage the view of the game */
    OrthographicCamera camera;
    /** The stage where UI elements are placed for rendering and handling input */
    private final Stage stage;
    /** The viewport defining the area of the stage visible on the screen */
    private final Viewport viewport;
    /** The skin defining the style of UI elements */
    private final Skin skin = new Skin(Gdx.files.internal("ui/neon/neon-ui.json"));
    /** The background music played during the game */
    private final Music music = Gdx.audio.newMusic(Gdx.files.internal("audio/space.ogg"));
    /** Indicates whether the username has been reset */
    private boolean usernameReset = false;
    /** The texture used for the background of the game screen */
    private final Texture backgroundTexture;
    /** The label used to display error messages */
    private final Label errorLabel = new Label("", skin, "over");
    /** The table used to organize UI elements */
    private final Table table = new Table();
    /** The text field for entering the username */
    private final TextField usernameField = addTextFieldRow(table, "Username:", "user", 10);
    /** The text field for entering the password */
    private final TextField passwordField = addTextFieldRow(table, "Password (optional):", "", 155);
    
    /**
     * Constructor for the LoginScreen, initializes camera and viewport, and sets up button skins
     *
     * @param gam - the game object
     */
    public LoginScreen(final TypingGame gam) {
        game = gam;

        camera = new OrthographicCamera();

        music.play();
        music.setVolume(game.getMusicVolume());
        music.setLooping(true);

        viewport = new FitViewport(1200, 800, camera);
        viewport.apply();

        camera.position.set(1200, 800, 0);
        camera.update();

        stage = new Stage(viewport, game.batch);

        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        errorLabel.setColor(1, 0, 0, 1);

        // errorLabel.getStyle().background = skin.newDrawable("white", 0, 0, 0, 0.5f);
        // errorLabel.setColor(1, 0, 0, 1);
    }
   
    /**
     *Renders the login screen
     * @param delta The time in seconds since the last frame
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        // Draw the background texture. Adjust the positioning and sizing as needed.
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        GlyphLayout errLayout = new GlyphLayout(game.font, errorLabel.getText());
        int errWidth = (int) (errLayout.width * 1.62f);
        Pixmap pixmap = new Pixmap(errWidth, 20, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.7f);
        pixmap.fillRectangle(0, 0, errWidth, 20);
        Texture errBgTexture = new Texture(pixmap);
        pixmap.dispose();

        game.batch.draw(errBgTexture, 600 - (float) errWidth / 2, 255);
        game.batch.end();

        // Call stage.act() and stage.draw() after drawing the background
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.F5) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            dispose();
            game.setScreen(new LoginScreen(game));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            try {
                tryLogin(usernameField.getText(), passwordField.getText());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     *Try to log in
     * 
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @throws NoSuchAlgorithmException If the specified cryptographic algorithm is not available in the environment.
     */
    private void tryLogin(String username, String password) throws NoSuchAlgorithmException {
        // log in logic
        // check if username and password match
        // if so, go to main menu
        // else display "username or password did not match any records"

        UserAuthenticator user = new UserAuthenticator(username, password);
        if (user.authenticate()) {
            System.out.println("authenticated user: " + username);
            game.setUsername(username);
            game.setScreen(new MainMenuScreen(game));
            dispose();
        } else {
            System.out.println("failed to authenticate user: " + username);
            errorLabel.setText("username or password is incorrect");
        }
    }

    /**
     * resize window
     * @param width  The new width of the window.
     * @param height The new height of the window.
     */
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

        table.setFillParent(true);
        table.top();
        table.padTop(350);
        // text to show the user that the username or password is incorrect or already exists

        Image logo = new Image(new Texture(Gdx.files.internal("logo.png")));
        logo.setPosition(280, 550);
        stage.addActor(logo);
        // place logo at the top of the screen

        usernameField.setSize(250, 80);
        // this method of storing a password is actually quite unsecure, but I don't aim for this to be that secure
        // So I won't change it. the reason for it is when the JVM segfaults (possible with JNI), contents of memory
        // gets dumped into a file, and if the password is stored in memory as a string, it can be read from that file.
        // char arrays are usually used because they can be dumped, like in Swing.

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
        table.row().padTop(-10);
        table.add(registerButton).width(200).colspan(2);

        // login button onclick
        loginButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            tryLogin(usernameField.getText(), passwordField.getText());
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
                errorLabel.setText("username cannot be blank");
            } else if (username.length() > 20) {
                errorLabel.setText("username cannot be longer than 20 characters");
            } else if (user.register()) {
                game.setUsername(username);
                // pop up to say registered successfully
                // go to main menu
                game.setScreen(new MainMenuScreen(game));
                dispose();
            } else {
                System.out.println("failed to register user: " + username);
                errorLabel.setText("username already exists");
            }
        }));

        table.row().padTop(10);

        // errorLabel.getStyle().background = skin.newDrawable("white", 0, 0, 0, 0.5f);
        table.add(errorLabel).colspan(2);

        stage.addActor(table);

        TextButton tutorialButton = new TextButton("Tutorial", new Skin(Gdx.files.internal("ui/star-soldier/star-soldier-ui.json")));
        tutorialButton.setPosition(1015, -3);
        tutorialButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            dispose();
            game.setScreen(new TutorialScreen(game, "Login"));
        }));
        stage.addActor(tutorialButton);

        TextButton exitButton = new TextButton("Quit", skin);
        exitButton.setPosition(5, 5);
        exitButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            dispose();
            Gdx.app.exit();
        }));
        stage.addActor(exitButton);
    }

    /**
     * Add a row to the table with a label and a text field
     * @param table - the table to add the row to
     * @param labelText - the text of the label
     * @param defaultValue - the default value of the text field
     * @param labelWidth - the width of the label
     * @return the text field that was added
     */
    private TextField addTextFieldRow(final Table table, String labelText, String defaultValue, int labelWidth) {
        final Label label = new Label(labelText, skin);
        final TextField text = new TextField(defaultValue, skin, Objects.equals(labelText, "Username:") ? "login" : "password");
        // text.getStyle().background = skin.newDrawable("white", 0, 0, 0, 0.5f);

        table.add(label).width(labelWidth);
        table.add(text).width(250);
        table.row().padTop(5);

        return text;
    }

    /**
     *  hide window
     */
    @Override
    public void hide() {
    }

    /**
     *  pause window
     */
    @Override
    public void pause() {
    }

    /**
     *  resume window
     */
    @Override
    public void resume() {
    }

    /**
     * for close window
     */
    @Override
    public void dispose() {
        stage.dispose();
        music.dispose();
        backgroundTexture.dispose(); // Dispose resources owned by this screen
    }
}

