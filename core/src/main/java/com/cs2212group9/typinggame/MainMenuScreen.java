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
 */
public class MainMenuScreen implements Screen {

    final TypingGame game;
    OrthographicCamera camera;
    private final Stage stage;
    private final Viewport viewport;
    // from https://opengameart.org/content/menu-music
    private final Music music = Gdx.audio.newMusic(Gdx.files.internal("audio/awesomeness.wav"));
    private final Skin skin;
    private int nextLevel;

    /**
     * Constructor for the MainMenuScreen, initializes camera & viewport, and sets up button skins
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
        // all skins from https://github.com/czyzby/gdx-skins
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        this.nextLevel = DBScores.highestUnlockedLevel(game.getUsername());
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

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
        // music.play();
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.top();
        table.padTop(100);

        Label welcome = new Label("Welcome, " + game.getUsername(), skin);

        table.add(welcome);
        table.row().padTop(10);
        welcome.setFontScale(3f);

        Image logo = new Image(new Texture(Gdx.files.internal("logo.png")));
        table.row().padTop(-25);
        table.add(logo);
        table.row();

        Button playButton = new TextButton("Play", skin);
        table.add(playButton).width(300);
        table.row().padTop(10);

        Button levelsButton = new TextButton("Levels", skin);
        table.add(levelsButton).width(300);
        table.row().padTop(10);

        Button highScoresButton = new TextButton("High Scores", skin);
        table.add(highScoresButton).width(300);
        table.row().padTop(10);

        Button optionsButton = new TextButton("Options", skin);
        table.add(optionsButton).width(300);
        table.row().padTop(10);

        Button logoutButton = new TextButton("Logout", skin);
        table.add(logoutButton).width(300);
        table.row().padTop(10);

        Button quitButton = new TextButton("Quit", skin);
        table.add(quitButton).width(300);
        table.row().padTop(10);

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

        /*highScoresButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            game.setScreen(new HighScoresScreen(game));
            dispose();
        }));*/

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
        stage.clear();
    }
}
