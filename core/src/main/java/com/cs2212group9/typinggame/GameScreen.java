package com.cs2212group9.typinggame;

// Import necessary LibGDX and Java utilities
import java.util.ArrayList;
import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.cs2212group9.typinggame.db.DBLevel;
import com.cs2212group9.typinggame.db.DBScores;
import com.cs2212group9.typinggame.effects.Explosion;
import com.cs2212group9.typinggame.effects.WordBackground;
import com.cs2212group9.typinggame.utils.InputListenerFactory;

/**
 * This class is responsible for the main game screen, where the player types words to destroy them.
 * It handles game logic, rendering, and player input, and displays the game over screen when needed.
 * It also manages the spawning of words, scoring, and game state transitions.
 * @author Group 9 members
 * @version 1.0
 */
public class GameScreen implements Screen {
    // Declaration of all class member variables
    /** The instance of the TypingGame class that controls the game logic */
    final TypingGame game;
    Sound dropSound, explodeSound, otherExplodeSound;
    Music music;
    OrthographicCamera camera;
    Array<Rectangle> words;
    long lastDropTime;
    Skin skin = new Skin(Gdx.files.internal("ui/star-soldier/star-soldier-ui.json"));
    /** The number of waves in the game */
    private Stage stage;
    /** An array containing the list of words */
    private int waves;
    Array<String> wordsList;
    /** store currentTypedWord */
    private String currentTypedWord = "";
    /** store score */
    private int score = 0;
    /** store wordstyped */
    private int wordsTyped = 0;
    /** store index of word typed*/
    private int indexOfWordToType = -1;
    /** store game time */
    private long gameStartTime, gameEndTime;
    /** store pause start time */
    private long pauseStartTime;
    /** store whether game over */
    private boolean gameOver = false;
    /** store level id */
    private final int levelId;
    /** store score set */
    private boolean scoreSet = false;
    /** the variable of background */
    private final Texture backgroundTexture;
    ArrayList<Explosion> explosions;
    /** The total number of levels */
    private final int levelCount = DBLevel.getLevelCount();
    
    /**
     * Constructs the game screen with necessary settings and initializes game objects.
     *
     * @param gam The main game object, providing access to shared resources.
     * @param levelId The level ID to load specific level settings and words.
     */

    public GameScreen(final TypingGame gam, final int levelId) {
        game = gam;

        // Load sound effects and music
        dropSound = Gdx.audio.newSound(Gdx.files.internal("audio/forceField_000.ogg"));
        explodeSound = Gdx.audio.newSound(Gdx.files.internal("audio/explosionCrunch_000.ogg"));
        otherExplodeSound = Gdx.audio.newSound(Gdx.files.internal("audio/explosionCrunch_000.ogg"));
        String[] musicFiles = {
            "audio/mammoth.ogg",
            "audio/notathing.mp3",
            "audio/tempus.ogg",
            "audio/cyberdeath.mp3",
            "audio/magic-space.mp3"
        };
        music = Gdx.audio.newMusic(Gdx.files.internal(musicFiles[levelId % musicFiles.length]));
        music.setLooping(true);
        music.setVolume(0.2f); // music is kinda loud

        // Set up the camera for 2D rendering
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        long startTime = System.nanoTime();

        // Initialize words array and load words for the current level
        words = new Array<Rectangle>();
        waves = DBLevel.getLevelWaves(levelId);
        Array<String> wordsPool = DBLevel.getLevelWords(levelId);
        wordsList = new Array<>();
        for (int i = 0; i < waves; i++) {
            String random = wordsPool.random();
            // try not to allow duplicates
            // based on benchmarks not removed -> removed, 1st load: ~1.5 ms -> ~3 ms, 2nd+ loads ~0.3 ms -> ~0.5 ms
            // totally acceptable performance hit, with improved user experience
            if (wordsPool.size > waves) {
                wordsPool.removeValue(random, false);
            }
            wordsList.add(random);
        }

        System.out.println("Time to load words: " + (System.nanoTime() - startTime) / 1_000_000f + " ms");
        spawnWord();

        // Initialize the stage for UI elements
        stage = new Stage();
        gameStartTime = TimeUtils.millis(); // Record the start time of the game
        this.levelId = levelId;
        backgroundTexture = new Texture(Gdx.files.internal("background_game_screen.png"));

        explosions = new ArrayList<Explosion>();
    }


    /**
     * Spawns a new word at a random position on the screen.
     * This is triggered periodically and after a word is typed correctly.
     * If there are no more waves of words left, no new words are spawned.
     */
    private void spawnWord() {
        if (waves == 0) return; // Exit if no more waves are left
        waves -= 1;
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(128, 800 - 128); // Random X within bounds
        raindrop.y = 460; // Start at the top of the screen
        raindrop.width = 64;
        raindrop.height = 64;
        words.add(raindrop);
        lastDropTime = TimeUtils.millis(); // Record the time of the last spawn
    }

    private boolean state = true; // When state is true, the game is active, otherwise the game is paused

    /**
     * Handles player input for typing letters and controlling the game (e.g., pausing).
     * It checks for typed letters against the current target word and processes backspace inputs.
     * It also toggles the game's pause state when the ESC key is pressed.
     */
    private void handleInput() {
        // Toggle pause and resume with ESC key
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            System.out.println("escape pressed");
            if (state) {
                pause();
            } else {
                resume(); // Call a method that specifically handles resuming the game
            }
        }

        if (state && indexOfWordToType >= 0 && indexOfWordToType < wordsList.size) {
            // skip word if space pressed
            if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
                dropSound.play();
                words.removeIndex(indexOfWordToType);
                wordsList.removeIndex(indexOfWordToType);
                currentTypedWord = ""; // Reset the typed word
                indexOfWordToType = -1; // Reset index to find new bottom-most word
                // Check if the game is over
                if (wordsList.size == 0) {
                    gameOver = true;
                    gameEndTime = TimeUtils.millis();
                }
            }

            // handle typed letters and backspace only if the game is not paused
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
                            score += currentTypedWord.length();
                            wordsTyped++;
                            Rectangle word = words.get(indexOfWordToType);
                            float x = word.x;
                            float y = word.y;
                            words.removeIndex(indexOfWordToType);
                            wordsList.removeIndex(indexOfWordToType);
                            currentTypedWord = ""; // Reset the typed word
                            indexOfWordToType = -1; // Reset index to find new bottom-most word
                            // Check if the game is over
                            if (wordsList.size == 0) {
                                gameOver = true;
                                gameEndTime = TimeUtils.millis();
                            }
                            // add explosion animation
                            explosions.add(new Explosion(x, y));
                            // play explode sound
                            explodeSound.play();
                        }
                    } // If the key pressed doesn't match the next character, do nothing

                    break; // Process only one key per frame
                }
            }

            // handle backspace
            if (Gdx.input.isKeyJustPressed(Keys.BACKSPACE) && !currentTypedWord.isEmpty()) {
                currentTypedWord = currentTypedWord.substring(0, currentTypedWord.length() - 1);
            }
        }
    }

    /**
     * Updates the index of the target word to type. It selects the word closest to the bottom
     * of the screen as the next target for typing.
     */
    private void updateWordToTypeIndex() {
        indexOfWordToType = -1; // Reset index
        float lowestY = Float.MAX_VALUE;
        for (int i = 0; i < words.size; i++) {
            Rectangle wordRectangle = words.get(i);
            if (wordRectangle.y < lowestY) {
                lowestY = wordRectangle.y;
                indexOfWordToType = i; // Update index to the lowest word
            }
        }
    }

    /**
     * Returns the sum of the ASCII values of the characters in a given word.
     * used for pseudo random selection of asteroid background for words
     *
     * @param word The word to calculate the ASCII sum for.
     * @return The sum of the ASCII values of the characters in the word.
     */
    private int asciiSum(String word) {
        int sum = 0;
        for (int i = 0; i < word.length(); i++) {
            sum += word.charAt(i);
        }
        return sum;
    }

    /**
     * Creates a texture with a black background for the word to improve visibility.
     *
     * @param width The width of the texture to create.
     * @return The texture with a black background.
     */
    private Texture wordBgTexture(float width) {
        Pixmap pixmap = new Pixmap((int) width, 18, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.75f);
        pixmap.fillRectangle(0, 0, (int) width, 18);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    /**
     * Displays the game over screen with the player's final score and the total time taken.
     * It allows the player to restart or exit to the main menu.
     */
    private void displayGameOverScreen() {
        if (gameEndTime == 0) gameEndTime = TimeUtils.millis(); // Set end time if not already set
        long totalTimeInSeconds = (gameEndTime - gameStartTime) / 1000;

        // ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        // Display "Game Over" and statistics
        String gameOverText;
        String nextLevel = "";
        int levelMinScore = DBLevel.getMinScores().get(levelId);
        boolean passed = score >= levelMinScore;
        if (passed && levelId < levelCount) {
            gameOverText = "Congratulations, You completed the level!";
            nextLevel = "You have unlocked level " + (levelId + 1) + "!";
        } else if (passed) {
            gameOverText = "Congratulations, You beat the game!";
            nextLevel = "You have unlocked all levels!";
        } else {
            gameOverText = "You lose! You need at least " + levelMinScore + " points to pass this level.";
        }
        game.font.draw(game.batch, gameOverText, 280, 300);
        game.font.draw(game.batch, nextLevel, 320, 275);
        game.font.draw(game.batch, "Words Typed: " + wordsTyped, 320, 250);
        game.font.draw(game.batch, "Final Score: " + score, 320, 225);
        game.font.draw(game.batch, "Time Consumed: " + totalTimeInSeconds + " seconds", 320, 200);
        game.font.draw(game.batch, "Click anywhere to return to the main menu", 280, 175);
        game.font.draw(game.batch, "Press enter to " +
            (passed && !(levelId >= levelCount) ? " go to the next level" : "retry level"), 280, 150);
        game.batch.end();

        // Add the score to the database, make sure it's only added once
        if (!scoreSet) {
            // current username stored in preferences
            String username = this.game.getUsername();
            DBScores.addScore(username, this.levelId, score);
            scoreSet = true;
        }

        if (Gdx.input.isTouched()) {
            dispose();
            game.setScreen(new MainMenuScreen(game)); // Return to main menu
        }

        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            dispose();
            int nextLevelId = Math.min(this.levelId + (passed ? 1 : 0), levelCount);
            game.setScreen(new GameScreen(game, nextLevelId));
        }
    }

    /**
     * The main game loop method called by the LibGDX framework. It clears the screen,
     * updates the camera, handles input, updates game objects, and draws the current
     * game state to the screen. It also checks for game over conditions.
     * @param time for rerender
     */
    @Override
    public void render(float delta) {
        // ScreenUtils.clear(1, 1, 1, 1); // Clear the screen
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        // Draw the background texture first
        game.batch.draw(backgroundTexture, 0, 0, 800, 480);
        game.batch.end();

        // check if the game is over to set game over condition and end game timer
        if (wordsList.size <= 0 && !gameOver) {
            gameOver = true;
            gameEndTime = TimeUtils.millis();
        }

        // display game over screen and skip rest of rendering if game is over
        if (gameOver) {
            displayGameOverScreen();
            return; // Skip the rest of the render method
        }

        // call method to handle user inputs (keyboard & mouse)
        handleInput();

        // state == true -> game not paused
        if (state) {
            camera.update();
            game.batch.setProjectionMatrix(camera.combined);

            // Update the bottom-most word index
            updateWordToTypeIndex();

            game.batch.begin();
            game.font.setColor(1, 1, 1, 1); // Set color to white for score display
            game.font.draw(game.batch, "Level " + levelId, 0, 475);
            game.font.draw(game.batch, "Score: " + score, 0, 455);
            game.font.draw(game.batch, "Words remaining: " + wordsList.size, 0, 435);

            // check which part of the word is typed and which isn't, and render accordingly
            for (Rectangle wordRectangle : words) {
                int index = words.indexOf(wordRectangle, true);
                String wordText = wordsList.get(index);
                String markedLetters = "";
                String unmarkedLetters = wordText;

                if (indexOfWordToType == index) {
                    int correctChars = Math.min(currentTypedWord.length(), wordText.length());
                    markedLetters = wordText.substring(0, correctChars);
                    unmarkedLetters = wordText.substring(correctChars);
                }

                GlyphLayout markedLayout = new GlyphLayout(game.font, markedLetters);
                GlyphLayout unmarkedLayout = new GlyphLayout(game.font, unmarkedLetters);

                float totalWidth = markedLayout.width + unmarkedLayout.width;
                float startX = wordRectangle.x + (wordRectangle.width / 2) - (totalWidth / 2);

                /*
                // draw a asteroid as a background for the word
                String astPath = "sprites/asteroids/asteroidR" + (asciiSum(wordText) % 13 + 1) + ".png";
                Texture asteroid = new Texture(Gdx.files.internal(astPath));
                // using this ugly method because it allows for rotation
                game.batch.draw(asteroid, wordRectangle.x, wordRectangle.y - 10,
                    32, 32, // Set originX and originY to 32 to rotate around the center
                    wordRectangle.width, wordRectangle.height, // width and height of the drawing area
                    1f, 1f, // scaleX and scaleY
                    asciiSum(wordText) % 180 * 2, // pseudorandom rotation
                    0, 0, // srcX and srcY
                    asteroid.getWidth(), asteroid.getHeight(), // srcWidth and srcHeight
                    false, false); // flipX and flipY
                 */

                // draw a black background for the word for visibility
                game.batch.draw(wordBgTexture(totalWidth + 12), startX - 6, wordRectangle.y + 17);

                game.font.setColor(0, 1, 0, 1); // Green for marked letters
                game.font.draw(game.batch, markedLetters, startX, wordRectangle.y + wordRectangle.height / 2);

                game.font.setColor(1, 1, 1, 1); // White for unmarked letters
                game.font.draw(game.batch, unmarkedLetters, startX + markedLayout.width, wordRectangle.y + wordRectangle.height / 2);
            }

            // find all explosion animations to remove
            ArrayList<Explosion> explosionsToRemove = new ArrayList<Explosion>();
            for (Explosion explosion : explosions) {
                explosion.update(delta);
                explosion.render(game.batch);
                if (explosion.remove) {
                    explosionsToRemove.add(explosion);
                }
            }
            // remove all finished explosions
            explosions.removeAll(explosionsToRemove);

            // render remaining explosions
            for (Explosion explosion : explosions) {
                explosion.update(delta);
                explosion.render(game.batch);
            }

            Iterator<Rectangle> iter = words.iterator();
            while (iter.hasNext()) {
                Rectangle wordRectangle = iter.next();
                // update position of words, i.e. move them down
                wordRectangle.y -= 65 * Gdx.graphics.getDeltaTime();
                if (wordRectangle.y < 32) {
                    int wordIndex = words.indexOf(wordRectangle, true);
                    if (indexOfWordToType == wordIndex) {
                        currentTypedWord = "";
                        indexOfWordToType = -1;
                    }
                    if (wordIndex != -1) {
                        wordsList.removeIndex(wordIndex);
                        iter.remove();
                        dropSound.play();
                    }
                }
            }

            // not more words -> game end
            if (wordsList.size <= 0) {
                gameOver = true;
                gameEndTime = TimeUtils.millis();
            } else {
                // spawn a word if 2.5 sec since last drop or no more words to type
                if (TimeUtils.timeSinceMillis(lastDropTime) > 2500 || words.isEmpty()) {
                    spawnWord();
                }
            }

            game.batch.end();
        } else {
            stage.act();
            stage.draw();
        }
    }

    /**
     * resize window
     * @param width the width of window
     * @param height the height of window
     */
    @Override
    public void resize(int width, int height) {
    }

    /**
     * Called when this screen becomes the current screen. It starts the background music
     * and sets the input processor to handle UI interactions.
     */
    @Override
    public void show() {
        // Start the background music and ensure it loops
        if (music == null) {
            music = Gdx.audio.newMusic(Gdx.files.internal("audio/rpg-loop.wav"));
            music.setLooping(true);
        }
        if (!music.isPlaying()) {
            music.play();
            music.setVolume(game.getMusicVolume());
        }

        // Set the input processor to handle UI interactions
        if (stage == null) {
            stage = new Stage();
        }
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Called when this screen is no longer the current screen. It disposes of the resources
     * to avoid memory leaks.
     */
    @Override
    public void hide() {
        dispose(); // Clean up resources to avoid memory leaks
    }

    /**
     * Pauses the game. This is triggered by pressing the ESC key. It pauses the game logic
     * and music, and displays a pause menu with options to resume or exit.
     */
    @Override
    public void pause() {
        if (!state || gameOver) return; // Avoid pausing if already paused or if the game is over

        state = false;
        pauseStartTime = TimeUtils.millis(); // Capture the time at which the game is paused
        music.pause();

        if (stage == null) stage = new Stage();
        stage.clear(); // Important to clear the stage to avoid stacking UI elements

        // Setup the pause menu
        Table table = new Table();
        table.setFillParent(true);
        table.top();
        table.padTop(100);

        // Create resume and return buttons
        TextButton resumeButton = new TextButton("Resume", skin);
        TextButton returnButton = new TextButton("Return to main menu", skin);

        // Add listeners to buttons
        resumeButton.addListener(InputListenerFactory.createClickListener((event, x, y) ->
            resume()
        ));

        returnButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }));

        // Add buttons to the stage
        table.add(resumeButton);
        table.row().padTop(10);
        table.add(returnButton);
        table.row().padTop(10);

        stage.addActor(table);

        // Set the input processor to the stage to capture UI input
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Resumes the game from a paused state. It resumes the game logic and music, and
     * clears the pause menu, returning control back to the main game loop.
     */
    @Override
    public void resume() {
        if (state) return; // Avoid resuming if already running

        long pauseDuration = TimeUtils.millis() - pauseStartTime; // Calculate how long the game was paused
        gameStartTime += pauseDuration; // Adjust the game start time by the pause duration

        // Ensure the game state is set to running
        state = true;

        // Clear pause UI and resume game
        if (stage != null) {
            stage.clear(); // Clear any pause-specific UI elements
            Gdx.input.setInputProcessor(null); // Reset input processor or set it as needed for your game
        }

        // If using a BitmapFont, consider reinitializing it if it was disposed
        if (game.font == null) {
            game.font = new BitmapFont(); // Or however you initialize your font
        }

        // Reassign the input processor if it was disposed
        if (stage == null) {
            stage = new Stage();
            Gdx.input.setInputProcessor(stage);
        }

        // Play music if it should be playing
        if (!music.isPlaying()) {
            music.play();
            music.setVolume(game.getMusicVolume());
        }
    }

    /**
     * Disposes of the game screen resources when they are no longer needed, such as when
     * the game is closed. This includes textures, sound effects, music, and the UI stage.
     */
    @Override
    public void dispose() {
        // Dispose of sound objects if they are not null
        if (dropSound != null) {
            dropSound.dispose();
            dropSound = null;
        }
        if (explodeSound != null) {
            explodeSound.dispose();
            explodeSound = null;
        }
        if (otherExplodeSound != null) {
            otherExplodeSound.dispose();
            otherExplodeSound = null;
        }

        // Dispose of the music object if it's not null
        if (music != null) {
            if (music.isPlaying()) {
                music.stop();
            }
            music.dispose();
            music = null;
        }

        // Dispose of the stage if it's not null
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
        if (backgroundTexture != null) backgroundTexture.dispose();
    }
}
