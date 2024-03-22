package com.cs2212group9.typinggame;

import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.cs2212group9.typinggame.db.DBLevel;
import com.cs2212group9.typinggame.utils.InputListenerFactory;

public class GameScreen implements Screen {
    final TypingGame game;
    Texture wordImage;
    Sound dropSound;
    Sound explodeSound;
    Sound otherExplodeSound;
    Music rainMusic;
    OrthographicCamera camera;
    Array<Rectangle> words;
    long lastDropTime;
    Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
    private Stage stage;
    private int waves;
    Array<String> wordsList;
    private String currentTypedWord = "";
    private int score = 0;
    private int indexOfWordToType = -1; // Index of the word that the player is currently typing
    private int levelId; // used for hot reloading, not yet implemented

    /**
     * Constructor for the GameScreen, initializes game related objects & factories, sets up camera and initial positions
     * @param gam - the game object
     * @param levelId - the level to be played
     */
    public GameScreen(final TypingGame gam, final int levelId) {
        this.levelId = levelId;
        this.game = gam;

        // load the sound effects and music
        dropSound = Gdx.audio.newSound(Gdx.files.internal("audio/forceField_000.ogg"));
        // vine boom sound
        explodeSound = Gdx.audio.newSound(Gdx.files.internal("audio/vine-boom.mp3"));
        // from https://opengameart.org/content/rpg-battle-theme-the-last-encounter-0
        otherExplodeSound = Gdx.audio.newSound(Gdx.files.internal("audio/explosionCrunch_000.ogg"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/rpg-loop.wav"));
        rainMusic.setLooping(true);

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);


        // create the words array and spawn the first raindrop
        words = new Array<Rectangle>();
        waves = DBLevel.getLevelWaves(levelId);
        wordsList = DBLevel.getLevelWords(levelId);

        spawnWord();
        stage = new Stage();
    }

    /**
     * spawns a word
     */
    private void spawnWord() {
        if (waves == 0) return;
        waves -= 1;
        // Evelyn this is where rectangles are created (might not need this)
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(128, 800 - 128);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        words.add(raindrop);
        lastDropTime = TimeUtils.millis();
    }

    private boolean state = true;

    private void handleInput() {

        if (state) {
            if (indexOfWordToType == -1) return; // If no word is being typed, don't process input

            // handle typed letters
            for (int i = Keys.A; i <= Keys.Z; i++) {
                if (Gdx.input.isKeyJustPressed(i)) {
                    char typedChar = (char) ('a' + i - Keys.A);
                    String wordToType = wordsList.get(indexOfWordToType);
                    String nextChar = wordToType.length() > currentTypedWord.length()
                        ? wordToType.substring(currentTypedWord.length(), currentTypedWord.length() + 1)
                        : "";

                    if (nextChar.equalsIgnoreCase(String.valueOf(typedChar))) {
                        currentTypedWord += typedChar;
                        if (currentTypedWord.equalsIgnoreCase(wordToType)) {
                            // Word completed
                            // explodeSound.play();
                            score++;
                            words.removeIndex(indexOfWordToType);
                            wordsList.removeIndex(indexOfWordToType);
                            currentTypedWord = ""; // Reset the typed word
                            indexOfWordToType = -1; // Reset index to find new bottom-most word
                        }
                    } // If the key pressed doesn't match the next character, do nothing

                    break; // Process only one key per frame
                }
            }
            // handle backspace
            if (Gdx.input.isKeyJustPressed(Keys.BACKSPACE)) {
                if (!currentTypedWord.isEmpty()) {
                    currentTypedWord = currentTypedWord.substring(0, currentTypedWord.length() - 1);
                }
            }
        }
        // handle esc for pause
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            pause();
        }
    }

    private void updateWordToTypeIndex() {
        // Reset index
        indexOfWordToType = -1;
        float lowestY = Float.MAX_VALUE;
        for (int i = 0; i < words.size; i++) {
            Rectangle wordRectangle = words.get(i);
            // Check if the word is the lowest on the screen
            if (wordRectangle.y < lowestY) {
                lowestY = wordRectangle.y;
                indexOfWordToType = i;
            }
        }
    }

    @Override
    public void render(float delta) {
        if (state) {
            ScreenUtils.clear(0, 0, 0, 1);
            camera.update();
            game.batch.setProjectionMatrix(camera.combined);

            // Update the bottom-most word index
            updateWordToTypeIndex();

            game.batch.begin();

            game.font.draw(game.batch, "Words Typed: " + score, 0, 480);
            game.font.draw(game.batch, "Words Remaining: " + this.waves, 0, 460);

            for (int i = 0; i < words.size; i++) {
                Rectangle wordRectangle = words.get(i);
                if (wordsList.isEmpty()) {
                    // TODO: handle game over (win or loss)
                    System.out.println("Game over.");
                    break;
                }
                String wordText = wordsList.get(i);

                // Determine the marked and unmarked parts of the current word to type
                String markedLetters = "";
                String unmarkedLetters = wordText;
                if (i == indexOfWordToType) {
                    int correctChars = Math.min(currentTypedWord.length(), wordText.length());
                    markedLetters = wordText.substring(0, correctChars);
                    unmarkedLetters = wordText.substring(correctChars);
                }

                GlyphLayout markedLayout = new GlyphLayout(game.font, markedLetters);
                GlyphLayout unmarkedLayout = new GlyphLayout(game.font, unmarkedLetters);

                // Draw the marked part of the word
                game.font.setColor(0, 1, 0, 1); // green for marked letters
                game.font.draw(game.batch, markedLetters,
                    wordRectangle.x + (wordRectangle.width - (markedLayout.width + unmarkedLayout.width)) / 2,
                    wordRectangle.y + wordRectangle.height / 2);

                // Draw the unmarked part of the word
                game.font.setColor(1, 1, 1, 1); // White for unmarked letters
                game.font.draw(game.batch, unmarkedLetters,
                    wordRectangle.x + (wordRectangle.width - (markedLayout.width + unmarkedLayout.width)) / 2 + markedLayout.width,
                    wordRectangle.y + wordRectangle.height / 2);

                // TODO: hey Evelyn this is where words get rendered, put image somewhere here
            }
            game.batch.end();

            handleInput();

            // Process falling words
            Iterator<Rectangle> iter = words.iterator();
            while (iter.hasNext()) {
                Rectangle wordRectangle = iter.next();
                int index = words.indexOf(wordRectangle, true);
                wordRectangle.y -= 75 * Gdx.graphics.getDeltaTime();
                if (wordRectangle.y < 64) {
                    otherExplodeSound.play();
                    iter.remove();
                    wordsList.removeIndex(index); // Remove the corresponding word text
                    if (indexOfWordToType == index) {
                        // Reset the typing if the bottom word was dropped
                        currentTypedWord = "";
                        indexOfWordToType = -1;
                    }
                }
            }

            // Check if a new word needs to be spawned (time between drops in milliseconds)
            if (TimeUtils.timeSinceMillis(lastDropTime) > 2000 || words.size < 1) spawnWord();

        } else {
            stage.act();
            stage.draw();
        }
    }


    @Override
    public void resize(int width, int height) {
    }


    /**
     * Resumes the game
     */
    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        // rainMusic.play();
    }

    /**
     * Resumes the game
     */
    @Override
    public void hide() {
    }

    /**
     * Pauses the game
     * pauses music, adds buttons to allow to exit to main menu or to resume
     */
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
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }));

        table.add(resumeButton).width(300);
        table.row().padTop(10);
        table.add(returnButton).width(300);
        table.row().padTop(10);

        stage.addActor(table);
    }

    /**
     * Resumes the game
     */
    @Override
    public void resume() {
        state = true;
        // rainMusic.play();

    }

    /**
     * Disposes of the game screen objects that we don't need elsewhere
     */
    @Override
    public void dispose() {
        stage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }

}
