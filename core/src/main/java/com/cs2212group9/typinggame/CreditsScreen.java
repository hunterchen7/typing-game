package com.cs2212group9.typinggame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cs2212group9.typinggame.utils.InputListenerFactory;

/**
 * This is the tutorial screen
 * @author Group 9 members
 * @version 1.0
 */
public class CreditsScreen implements Screen {
    /** The instance of the TypingGame class that controls the game logic */
    final TypingGame game;
    /** The camera used to manage the view of the game */
    OrthographicCamera camera;
    /** The stage where UI elements are placed for rendering and handling input */
    private final Stage stage;
    /** The viewport defining the area of the stage visible on the screen */
    private final Viewport viewport;
    /** The skin defining the style of UI elements */
    private final Skin skin = new Skin(Gdx.files.internal("ui/star-soldier/star-soldier-ui.json"));
    private final Skin rainbowSkin = new Skin(Gdx.files.internal("ui/rainbow-skin/rainbow-ui.json"));
    /** The texture used for the background of the game screen */
    private final Texture backgroundTexture;
    /** The table used to organize UI elements */
    private final Table table = new Table();

    /**
     * Constructor for the TutorialScreen, initializes camera and viewport, and sets up button skins
     *
     * @param gam - the game object
     */
    public CreditsScreen(final TypingGame gam) {
        game = gam;

        camera = new OrthographicCamera();

        viewport = new FitViewport(1200, 800, camera);
        viewport.apply();

        camera.position.set(1200, 800, 0);
        camera.update();

        stage = new Stage(viewport, game.batch);

        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
    }

    /**
     * Renders the game screen. Handles keyboard inputs for navigation and refresh.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        // Draw the background texture. Adjust the positioning and sizing as needed.
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        int width = 1000;
        int height = 260;
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.fillRectangle(0, 0, width, height);
        game.batch.draw(new Texture(pixmap), (1200 - width) / 2f, 360);
        pixmap.dispose();
        game.batch.end();

        // Call stage.act() and stage.draw() after drawing the background
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.F5) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            dispose();
            game.setScreen(new LoginScreen(game));
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
     * Show the tutorial screen, set up the stage and add actors.
     * add appropriate buttons and images for the tutorial
     */
    @Override
    // TODO: don't hard code positions, make dynamic
    public void show() {
        Gdx.input.setInputProcessor(stage);

        table.setFillParent(true);
        table.top();
        table.pad(30);
        table.padTop(200);
        table.padBottom(75);
        // text to show the user that the username or password is incorrect or already exists

        Label title = new Label("Credits", skin);
        title.setFontScale(3f);
        table.add(title);
        table.row().padTop(10);

        table.add(new Label("Created by Group 9 for CS2212", skin));
        table.row().padTop(10);

        table.add(new Label("Winter 2024 term @ Western University", skin));
        table.row().padTop(50);

        table.add(new Label("Group Members:", skin));
        table.row().padTop(10);

        table.add(new Label("Hunter Chen, Xiaowei Feng, Yang Liu, Junwu Chen and Mingze Li", skin));

        stage.addActor(table);

        TextButton returnButton = new TextButton("Return", skin);
        returnButton.setPosition(1050, 5);
        returnButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            dispose();
            game.setScreen(new LoginScreen(game));
        }));
        stage.addActor(returnButton);
    }

    /**
     * Hide the tutorial screen
     */
    @Override
    public void hide() {
    }

    /**
     * Pause the tutorial screen
     */
    @Override
    public void pause() {
    }

    /**
     * Resume the tutorial screen
     */
    @Override
    public void resume() {
    }

    /**
     * Dispose of the tutorial screen
     */
    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose(); // Dispose resources owned by this screen
    }
}

