package com.cs2212group9.typinggame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cs2212group9.typinggame.db.DBScores;
import com.cs2212group9.typinggame.utils.InputListenerFactory;
/**
 * This class is mainly responsible for the main menu interface of the game.
 * @author Group 9 members
 * @version 1.0
 */
public class MainMenuScreen implements Screen {
    /** Represents the main game class for the typing game */
    final TypingGame game;
    /** Orthographic camera for 2D graphics rendering control */
    OrthographicCamera camera;
    /** Stage object for managing the stage and actors */
    private final Stage stage;
    /** Viewport object for specifying the stage display area */
    private final Viewport viewport;
    /** Background music for the game menu */
    private final Music music = Gdx.audio.newMusic(Gdx.files.internal("audio/space.ogg"));
    /** Skin object for defining the appearance and behavior of UI elements */
    private final Skin skin = new Skin(Gdx.files.internal("ui/star-soldier/star-soldier-ui.json"));
    /** Represents the index of the next level in the game progression */
    private int nextLevel;
    private final Texture backgroundTexture;

    /**
     * Constructor for the MainMenuScreen, initializes camera & viewport, and sets up button skins
     *
     * @param gam - the game object
     */
    public MainMenuScreen(final TypingGame gam) {
        game = gam;

        camera = new OrthographicCamera();

        viewport = new FitViewport(1200, 800, camera);
        viewport.apply();

        camera.position.set(1200, 800, 0);
        camera.update();

        stage = new Stage();


        this.nextLevel = DBScores.highestUnlockedLevel(game.getUsername());
        backgroundTexture = new Texture(Gdx.files.internal("background.png")); // Ensure the file path is correct
    }

    /**
     * Renders the game screen.
     * 
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        // Draw the background texture. Adjust the positioning and sizing as needed.
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

        // Existing rendering code for drawing stage and other UI components...
        stage.act();
        stage.draw();

        /*if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
         */
        if (Gdx.input.isKeyPressed(Input.Keys.F5) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            dispose();
            game.setScreen(new MainMenuScreen(game));
        }
    }

    /**
     * Called when the game window is resized.
     * 
     * @param width  The new width of the game window
     * @param height The new height of the game window
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    /**
     * Show the main menu screen, set up the stage and add actors.
     * add buttons for play, levels, high scores, options, logout, and quit
     */
    @Override
    public void show() {
        music.play();
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.top();
        table.padTop(100);

        Label welcome = new Label("Welcome, " + game.getUsername(), skin);
        welcome.setColor(0, 1, 1, 1);
        welcome.getStyle().background = skin.newDrawable("white", 0, 0, 0, 0.5f);

        table.add(welcome);
        table.row().padTop(10);
        welcome.setFontScale(1.5f);

        Image logo = new Image(new Texture(Gdx.files.internal("logo.png")));
        table.row().padTop(30);
        table.add(logo);
        table.row().padTop(25);

        Button playButton = new TextButton("Play", skin);
        table.add(playButton).width(300);
        table.row().padTop(-15);

        Button levelsButton = new TextButton("Levels", skin);
        table.add(levelsButton).width(300);
        table.row().padTop(-15);

        Button highScoresButton = new TextButton("High Scores", skin);
        table.add(highScoresButton).width(300);
        table.row().padTop(-15);

        Button optionsButton = new TextButton("Options", skin);
        // table.add(optionsButton).width(300);
        // table.row().padTop(-15);

        Button logoutButton = new TextButton("Logout", skin);
        table.add(logoutButton).width(300);
        table.row().padTop(-15);

        Button quitButton = new TextButton("Quit", skin);
        table.add(quitButton).width(300);
        table.row().padTop(-15);

        playButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            // should be last level
            game.setScreen(new GameScreen(game, this.nextLevel));
            dispose();
        }));

        levelsButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            game.setScreen(new LevelsScreen(game));
            dispose();
        }));

        optionsButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            game.setScreen(new OptionsScreen(game));
            dispose();
        }));

        highScoresButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            game.setScreen(new ScoresScreen(game));
            dispose();
        }));

        logoutButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            game.setScreen(new LoginScreen(game));
            dispose();
        }));

        quitButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            // TODO: Add confirmation dialog
            Gdx.app.exit();
        }));

        stage.addActor(table);
    }

    /**
     * Method called when hiding the game interface
     */  
    @Override
    public void hide() {
    }

    /**
     * Method called when the game is paused.
     */
    @Override
    public void pause() {
    }

    /**
     * Method called when resuming the game
     */ 
    @Override
    public void resume() {
    }
  
    /**
     * Methods to clean up resources and release memory
     */
    @Override
    public void dispose() {
        // Dispose of the background texture when no longer needed
        if (backgroundTexture != null) backgroundTexture.dispose();

        // Dispose other resources...
        stage.clear();
        music.dispose();
        stage.dispose();
    }
}
