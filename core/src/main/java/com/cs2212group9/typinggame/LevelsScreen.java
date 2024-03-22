package com.cs2212group9.typinggame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cs2212group9.typinggame.utils.InputListenerFactory;
import com.cs2212group9.typinggame.db.DBLevel;
/**
 * This class is mainly responsible for the levelselection interface of the game.
 * @author Group 9 members
 */
public class LevelsScreen implements Screen {

    final TypingGame game;
    OrthographicCamera camera;
    private final Stage stage;
    private final Viewport viewport;
    private Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
    private DBLevel levelDb;
    private int selectedLevel = 1;
    // from https://opengameart.org/content/woodland-fantasy
    private final Music music = Gdx.audio.newMusic(Gdx.files.internal("audio/WoodlandFantasy.mp3"));

    /**
     * Constructor for the LevelsScreen, initializes camera & viewport, and sets up button skins
     * @param gam - the game object
     */
    public LevelsScreen(final TypingGame gam) {
        game = gam;
        levelDb = new DBLevel();

        camera = new OrthographicCamera();

        viewport = new FitViewport(1200, 800, camera);
        viewport.apply();

        camera.position.set(1200, 800, 0);
        camera.update();

        stage = new Stage();
    }

    /**
     * Constructor for the LevelsScreen, initializes camera & viewport, and sets up button skins
     * @param gam - the game object
     * @param selectedLevel - the default level to be played
     */
    public LevelsScreen(final TypingGame gam, int selectedLevel) {
        game = gam;
        levelDb = new DBLevel();

        camera = new OrthographicCamera();

        viewport = new FitViewport(1200, 800, camera);
        viewport.apply();

        camera.position.set(1200, 800, 0);
        camera.update();

        stage = new Stage();
        this.selectedLevel = selectedLevel;
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
            game.setScreen(new LevelsScreen(game));
        }
    }

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
        table.padTop(200);

        int levelCount = levelDb.levelCount();
        System.out.println("DBLevel count: " + levelCount);

        for (int i = 0; i < levelCount / 3; i++) { // placeholder levels
            for (int j = 1; j <= 3; j++) {
                int level = i * 3 + j;
                TextButton levelButton = new TextButton("Level " + level, skin);
                levelButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
                    game.setScreen(new GameScreen(game, level));
                    dispose();
                }));
                table.add(levelButton).width(200).pad(5);
            }
            table.row();
        }

        stage.addActor(table);
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
        skin.dispose();
        music.dispose();
    }
}
