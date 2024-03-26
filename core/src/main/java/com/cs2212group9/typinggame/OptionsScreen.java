package com.cs2212group9.typinggame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
/**
 * This class is mainly used to set various parameters of the game according to user needs.
 * @author Group 9 members
 * @version 1.0
 */
public class OptionsScreen implements Screen {
    /** Represents the main game class of the typing game */
    final TypingGame game;
    /** Orthographic camera for controlling 2D graphics rendering */
    OrthographicCamera camera;
    /** Stage object for managing stages and actors */
    private final Stage stage;
    /** Viewport object used to specify the stage display area */
    private final Viewport viewport;
    /** Skin objects used to define the appearance and behavior of UI elements */
    private Skin skin;

    /** 
     * Constructor for the OptionsScreen, initializes camera & viewport, and sets up button skins
     *
     * @param gam - the game object
     */
    public OptionsScreen(final TypingGame gam) {
        game = gam;

        camera = new OrthographicCamera();

        viewport = new FitViewport(1200, 800, camera);
        viewport.apply();

        camera.position.set(1200, 800, 0);
        camera.update();

        stage = new Stage();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
    }

    /**
     *Render game screen
     *
     * @param delta rendering interval
     */
    @Override
    public void render(float delta) {
        // Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.F5) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            dispose();
            game.setScreen(new OptionsScreen(game));
        }
    }

    /**
     * Resize the viewport to match the new width and height.
     *
     * @param width new viewport width
     * @param height New viewport height
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
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
     * Method called when hiding the game interface
     */  
    @Override
    public void hide() {

    }
  
    /**
     * Methods to clean up resources and release memory
     */
    @Override
    public void dispose() {
        stage.clear();
        stage.dispose();
    }

    /**
     * Method called when displaying the game interface
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();

        table.setFillParent(true);
    }
}
