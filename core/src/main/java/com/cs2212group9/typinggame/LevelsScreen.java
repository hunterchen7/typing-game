package com.cs2212group9.typinggame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cs2212group9.typinggame.db.DBScores;
import com.cs2212group9.typinggame.utils.InputListenerFactory;
import com.cs2212group9.typinggame.db.DBLevel;
/**
 * This class is mainly responsible for the level selection interface of the game.
 * @author Group 9 members
 * @version 1.0
 */
public class LevelsScreen implements Screen {
    /** Represents the instance of the TypingGame class that controls the game logic */
    final TypingGame game;
    /** Represents the camera used to manage the view of the game */
    OrthographicCamera camera;
    /** Represents the stage where UI elements are placed for rendering and handling input */
    private final Stage stage;
    /** Represents the viewport defining the area of the stage visible on the screen */
    private final Viewport viewport;
    /** Represents the skin defining the style of UI elements */
    private final Skin skin = new Skin(Gdx.files.internal("ui/neon/neon-ui.json"));
    // from https://opengameart.org/content/woodland-fantasy
    /** Represents the background music played during the game */
    private final Music music = Gdx.audio.newMusic(Gdx.files.internal("audio/space_echo.ogg"));
    /** Represents the texture used for the background of the levels screen */
    private final Texture backgroundTexture = new Texture(Gdx.files.internal("levels_background.png"));
    /**
     * Constructor for the LevelsScreen, initializes camera and viewport, and sets up button skins
     * @param gam - the game object
     */
    public LevelsScreen(final TypingGame gam) {
        game = gam;

        camera = new OrthographicCamera();

        viewport = new FitViewport(1200, 800, camera);
        viewport.apply();

        camera.position.set(1200, 800, 0);
        camera.update();

        music.play();
        music.setVolume(game.getMusicVolume());
        music.setLooping(true);

        stage = new Stage();
    }

    /**
     * Renders the screen
     * @param delta The time in seconds since the last render
     */
    @Override
    public void render(float delta) {
        // Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        // Draw the background texture to fill the screen
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

        // Existing rendering code for the stage...
        stage.act();
        stage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.F5) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            dispose();
            game.setScreen(new LevelsScreen(game));
        }
    }
    /**
     * resize window
     * @param width The width of window
     * @param height The height of window
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    /**
     * Show the levels screen, generates button levels among other things
     */
    @Override
    // TODO: don't hard code positions, make dynamic
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.top();

        table.add(new Label("Select a Level", skin)).colspan(3);
        table.padTop(100);
        table.row();

        int levelCount = DBLevel.getLevelCount();
        System.out.println("DBLevel count: " + levelCount);

        String username = this.game.getUsername();

        int highestUnlockedLevel = DBScores.highestUnlockedLevel(username);
        System.out.println("Highest unlocked level: " + highestUnlockedLevel);

        for (int i = 0; i < levelCount / 3; i++) { // placeholder levels
            for (int j = 1; j <= 3; j++) {
                int level = i * 3 + j;
                TextButton levelButton = new TextButton("Level " + level, skin);
                if (level > highestUnlockedLevel) {
                    // set color to red if not unlocked
                    levelButton.setColor(0, 0, 0, 0.75f);
                    levelButton.setTouchable(Touchable.disabled);
                } else {
                    levelButton.setColor(1, 1, 1, 1);
                }
                levelButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
                    if (level > highestUnlockedLevel) {
                        // TODO: add a popup or something
                        System.out.println("Level locked, must beat level " + (level - 1) + " first");
                        return;
                    }
                    game.setScreen(new GameScreen(game, level));
                    dispose();
                }));
                table.add(levelButton).width(200).pad(5);
            }
            table.row().padTop(-10);
        }

        stage.addActor(table);

        TextButton returnButton = new TextButton("Return to Main Menu", skin);
        returnButton.setPosition(5, 5);
        returnButton.setWidth(200);
        returnButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            dispose();
            game.setScreen(new MainMenuScreen(game));
        }));
        stage.addActor(returnButton);
    }

    /**
     * hide the window
     */
    @Override
    public void hide() {
    }

    /**
     * pause the window
     */
    @Override
    public void pause() {
    }

    /**
     * resume the window
     */
    @Override
    public void resume() {
    }

    /**
     * close the window
     */
    @Override
    public void dispose() {
        if (backgroundTexture != null) backgroundTexture.dispose();
        // Dispose other resources...
        stage.dispose();
        skin.dispose();
        music.dispose();
    }
}
