package com.cs2212group9.typinggame;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.cs2212group9.typinggame.utils.DBHelper;
import com.cs2212group9.typinggame.utils.InputListenerFactory;

public class GameScreen implements Screen {

    final TypingGame game;

    Texture wordImage;
    Texture bucketImage;
    Sound dropSound;
    Music rainMusic;
    OrthographicCamera camera;
    Rectangle bucket;
    Array<Rectangle> words;
    long lastDropTime;
    int dropsGathered;
    Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
    private Stage stage;
    private DBHelper D;

    public GameScreen(final TypingGame gam, DBHelper db) {
        this.game = gam;
        D = db;

        // load the images for the droplet and the bucket
        wordImage = new Texture(Gdx.files.internal("sprites/blue_smile.png"));
        bucketImage = new Texture(Gdx.files.internal("sprites/fire_in_the_hole.png"));

        // load the sound effects and music
        dropSound = Gdx.audio.newSound(Gdx.files.internal("audio/forceField_000.ogg"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/computerNoise_002.ogg"));
        rainMusic.setLooping(true);

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // create a Rectangle to logically represent the bucket
        bucket = new Rectangle();
        bucket.x = (float) 800 / 2 - (float) 64 / 2; // center the bucket horizontally
        bucket.y = 20; // bottom left corner of the bucket is 20 pixels above
        // the bottom screen edge
        bucket.width = 64;
        bucket.height = 64;

        // create the words array and spawn the first raindrop
        words = new Array<Rectangle>();
        spawnRaindrop();

        stage = new Stage();


    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - 64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        words.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    private boolean state = true;

    @Override
    public void render(float delta) {
        if (state) {
            // clear the screen with a dark blue color. The
            // arguments to clear are the red, green
            // blue and alpha component in the range [0,1]
            // of the color to be used to clear the screen.
            ScreenUtils.clear(0, 0, 0.2f, 1);

            // tell the camera to update its matrices.
            camera.update();

            // tell the SpriteBatch to render in the
            // coordinate system specified by the camera.
            game.batch.setProjectionMatrix(camera.combined);

            // begin a new batch and draw the bucket and
            // all drops
            game.batch.begin();
            game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);
            game.batch.draw(bucketImage, bucket.x, bucket.y);
            for (Rectangle raindrop : words) {
                game.batch.draw(wordImage, raindrop.x, raindrop.y);
            }
            game.batch.end();

            // process user input
            if (Gdx.input.isTouched()) {
                Vector3 touchPos = new Vector3();
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                bucket.x = touchPos.x - 64 / 2;
            }
            if (Gdx.input.isKeyPressed(Keys.LEFT))
                bucket.x -= 200 * Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyPressed(Keys.RIGHT))
                bucket.x += 200 * Gdx.graphics.getDeltaTime();

            if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
                pause();
            }

            // make sure the bucket stays within the screen bounds
            if (bucket.x < 0)
                bucket.x = 0;
            if (bucket.x > 800 - 64)
                bucket.x = 800 - 64;

            // check if we need to create a new raindrop
            if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
                spawnRaindrop();

            // move the words, remove any that are beneath the bottom edge of
            // the screen or that hit the bucket. In the later case we play back
            // a sound effect as well.
            Iterator<Rectangle> iter = words.iterator();
            while (iter.hasNext()) {
                Rectangle raindrop = iter.next();
                raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
                if (raindrop.y + 64 < 0)
                    iter.remove();
                if (raindrop.overlaps(bucket)) {
                    dropsGathered++;
                    dropSound.play();
                    iter.remove();
                }
            }
        } else {
            Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);
            stage.act();
            stage.draw();

            if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
                state = true;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        rainMusic.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
        rainMusic.pause();
        state = false;
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.top();
        table.padTop(100);

        TextButton resumeButton = new TextButton("Resume", skin);
        TextButton returnButton = new TextButton("Return to main menu", skin);

        resumeButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            resume();
        }));

        returnButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            game.setScreen(new MainMenuScreen(game, D));
            dispose();
        }));

        table.add(resumeButton).width(300);
        table.row().padTop(10);
        table.add(returnButton).width(300);
        table.row().padTop(10);

        stage.addActor(table);
    }

    @Override
    public void resume() {
        state = true;
        rainMusic.play();

    }

    @Override
    public void dispose() {
        wordImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }

}
