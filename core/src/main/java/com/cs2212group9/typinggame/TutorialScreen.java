package com.cs2212group9.typinggame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cs2212group9.typinggame.utils.InputListenerFactory;
import com.cs2212group9.typinggame.utils.UserAuthenticator;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * This is the tutorial screen
 * @author Group 9 members
 */
public class TutorialScreen implements Screen {
    final TypingGame game;
    OrthographicCamera camera;
    private final Stage stage;
    private final Viewport viewport;
    private final Skin skin = new Skin(Gdx.files.internal("ui/neon/neon-ui.json"));
    private final Texture backgroundTexture;
    private final Table table = new Table();
    private final String prevScreen;
    private final String[] slides = {"tutorial/slide1.png", "tutorial/slide2.png", "tutorial/slide3.png", "tutorial/slide4.png", "tutorial/slide5.png"};
    private int slide = 0;

    /**
     * Constructor for the TutorialScreen, initializes camera and viewport, and sets up button skins
     *
     * @param gam - the game object
     * @param prevScreen - a string representing the previous screen
     */

    public TutorialScreen(final TypingGame gam, String prevScreen) {
        game = gam;

        camera = new OrthographicCamera();

        viewport = new FitViewport(1200, 800, camera);
        viewport.apply();

        camera.position.set(1200, 800, 0);
        camera.update();

        stage = new Stage(viewport, game.batch);

        backgroundTexture = new Texture(Gdx.files.internal("background.png"));

        this.prevScreen = prevScreen;
    }

    /**
     * Constructor for the TutorialScreen with a specific slide
     *
     * @param gam - the game object
     * @param prevScreen - a string representing the previous screen
     * @param slide - an integer representing the current slide
     */
    public TutorialScreen(final TypingGame gam, String prevScreen, int slide) {
        this(gam, prevScreen);
        this.slide = slide;
    }

    /**
     * Move to the previous slide
     */
    private void prevSlide() {
        if (slide > 0) {
            dispose();
            game.setScreen(new TutorialScreen(game, prevScreen, slide - 1));
        }
    }

    /**
     * Move to the next slide
     */
    private void nextSlide() {
        if (slide < slides.length - 1) {
            dispose();
            game.setScreen(new TutorialScreen(game, prevScreen, slide + 1));
        }
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
        game.batch.end();

        // Call stage.act() and stage.draw() after drawing the background
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.F5) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            dispose();
            game.setScreen(new LoginScreen(game));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) prevSlide();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) nextSlide();
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
     * Create a drawable object from a file path, used for image button(s)
     *
     * @param path - the path to the file
     * @return a TextureRegionDrawable object
     */
    private TextureRegionDrawable createDrawable(String path) {
        return new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(path))));
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
        table.padBottom(75);
        // text to show the user that the username or password is incorrect or already exists

        stage.addActor(table);

        TextButton returnButton = new TextButton("Return", skin);
        returnButton.setPosition(1110, 5);
        returnButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            dispose();
            if (Objects.equals(this.prevScreen, "MainMenu")) {
                game.setScreen(new MainMenuScreen(game));
            } else if (Objects.equals(this.prevScreen, "Login")) {
                game.setScreen(new LoginScreen(game));
            }
        }));
        stage.addActor(returnButton);

        Image title = new Image(new Texture(Gdx.files.internal("tutorial/title.png")));
        table.add(title).colspan(3);
        table.row();

        Button prevButton = new ImageButton(createDrawable("tutorial/prev.png"));

        Image slideImage = new Image(new Texture(Gdx.files.internal(slides[slide])));
        slideImage.setPosition(0, 0);
        slideImage.setWidth(1000);
        slideImage.setHeight(667);

        Button nextButton = new ImageButton(createDrawable("tutorial/next.png"));

        table.add(prevButton);
        table.add(slideImage);
        table.add(nextButton);
        table.row();

        prevButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            prevSlide();
        }));

        nextButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            nextSlide();
        }));

        Label slideLabel = new Label("Slide: " + (this.slide + 1) + " / " + (slides.length), skin);
        slideLabel.setFontScale(1.5f);
        table.add(slideLabel).colspan(3).padTop(20);
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

