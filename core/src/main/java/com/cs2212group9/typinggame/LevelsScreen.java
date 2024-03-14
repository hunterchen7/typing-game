package com.cs2212group9.typinggame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cs2212group9.typinggame.utils.InputListenerFactory;
import com.cs2212group9.typinggame.db.Level;

public class LevelsScreen implements Screen {

    final TypingGame game;
    OrthographicCamera camera;
    private final Stage stage;
    private final Viewport viewport;
    private Skin skin;
    private Level levelDb;
    private int selectedLevel = 1;

    /**
     * Constructor for the LevelsScreen, initializes camera & viewport, and sets up button skins
     * @param gam - the game object
     */
    public LevelsScreen(final TypingGame gam) {
        game = gam;
        levelDb = new Level();

        camera = new OrthographicCamera();

        viewport = new FitViewport(1200, 800, camera);
        viewport.apply();

        camera.position.set(1200, 800, 0);
        camera.update();

        stage = new Stage();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
    }

    /**
     * Constructor for the LevelsScreen, initializes camera & viewport, and sets up button skins
     * @param gam - the game object
     * @param selectedLevel - the default level to be played
     */
    public LevelsScreen(final TypingGame gam, int selectedLevel) {
        game = gam;
        levelDb = new Level();

        camera = new OrthographicCamera();

        viewport = new FitViewport(1200, 800, camera);
        viewport.apply();

        camera.position.set(1200, 800, 0);
        camera.update();

        stage = new Stage();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        this.selectedLevel = selectedLevel;
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
        System.out.println("Level count: " + levelCount);

        for (int i = 1; i <= levelCount / 3; i++) { // placeholder levels
            for (int j = 1; j <= 3; j++) {
                int level = i * j;
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
    }
}
