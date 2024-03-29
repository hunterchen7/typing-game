package com.cs2212group9.typinggame;

// Import necessary LibGDX and Java utilities
import java.util.Iterator;
import com.badlogic.gdx.Gdx;
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
import com.cs2212group9.typinggame.db.DBScores;
import com.cs2212group9.typinggame.utils.InputListenerFactory;

public class GameScreen implements Screen {
    // Declaration of all class member variables
    final TypingGame game;
    Texture wordImage;
    Sound dropSound, explodeSound, otherExplodeSound;
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
    private int wordsTyped = 0;
    private int indexOfWordToType = -1;
    private long gameStartTime, gameEndTime;
    private long pauseStartTime;
    private boolean gameOver = false;
    private final int levelId;
    private boolean scoreSet = false;

    /**
     * Constructs the game screen with necessary settings and initializes game objects.
     *
     * @param gam The main game object, providing access to shared resources.
     * @param levelId The level ID to load specific level settings and words.
     */

    public GameScreen(final TypingGame gam, final int levelId) {
        this.game = gam;
        // Load sound effects and music
        dropSound = Gdx.audio.newSound(Gdx.files.internal("audio/forceField_000.ogg"));
        explodeSound = Gdx.audio.newSound(Gdx.files.internal("audio/vine-boom.mp3"));
        otherExplodeSound = Gdx.audio.newSound(Gdx.files.internal("audio/explosionCrunch_000.ogg"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/rpg-loop.wav"));
        rainMusic.setLooping(true);

        // Set up the camera for 2D rendering
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // Initialize words array and load words for the current level
        words = new Array<Rectangle>();
        waves = DBLevel.getLevelWaves(levelId);
        Array<String> wordsPool = DBLevel.getLevelWords(levelId);
        wordsList = new Array<>();
        for (int i = 0; i < waves; i++) {
            wordsList.add(wordsPool.random());
        }
        spawnWord();

        // Initialize the stage for UI elements
        stage = new Stage();
        gameStartTime = TimeUtils.millis(); // Record the start time of the game
        this.levelId = levelId;
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
        raindrop.y = 480; // Start at the top of the screen
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
     * Displays the game over screen with the player's final score and the total time taken.
     * It allows the player to restart or exit to the main menu.
     */
    private void displayGameOverScreen() {
        if (gameEndTime == 0) gameEndTime = TimeUtils.millis(); // Set end time if not already set
        long totalTimeInSeconds = (gameEndTime - gameStartTime) / 1000;

        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        // Display "Game Over" and statistics
        String gameOverText;
        String nextLevel = "";
        int levelMinScore = DBLevel.getMinScores().get(levelId);
        if (score >= levelMinScore) {
            gameOverText = "Congratulations, You completed the level!";
            nextLevel = "You have unlocked level " + (levelId + 1) + "!";
        } else {
            gameOverText = "You lose! You need at least " + levelMinScore + " points to pass this level.";
        }
        game.font.draw(game.batch, gameOverText, 320, 300);
        game.font.draw(game.batch, nextLevel, 320, 275);
        game.font.draw(game.batch, "Words Typed: " + wordsTyped, 320, 250);
        game.font.draw(game.batch, "Final Score: " + score, 320, 225);
        game.font.draw(game.batch, "Time Consumed: " + totalTimeInSeconds + " seconds", 320, 200);
        game.font.draw(game.batch, "Press Enter to return to the main menu", 320, 175);
        game.batch.end();

        // Add the score to the database, make sure it's only added once
        if (!scoreSet) {
            // current username stored in preferences
            String username = this.game.getUsername();
            DBScores.addScore(username, this.levelId, score);
            scoreSet = true;
        }

        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isTouched()) {
            dispose();
            game.setScreen(new MainMenuScreen(game)); // Return to main menu
        }
    }

    /**
     * The main game loop method called by the LibGDX framework. It clears the screen,
     * updates the camera, handles input, updates game objects, and draws the current
     * game state to the screen. It also checks for game over conditions.
     */
    @Override
    public void render(float delta) {
        if (wordsList.size <= 0 && !gameOver) {
            gameOver = true;
            gameEndTime = TimeUtils.millis();
        }
        if (gameOver) {
            displayGameOverScreen();
            return; // Skip the rest of the render method
        }

        handleInput();

        if (state) {
            ScreenUtils.clear(0, 0, 0, 1);
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
                String wordText = wordsList.get(words.indexOf(wordRectangle, true));
                String markedLetters = "";
                String unmarkedLetters = wordText;

                if (indexOfWordToType == words.indexOf(wordRectangle, true)) {
                    int correctChars = Math.min(currentTypedWord.length(), wordText.length());
                    markedLetters = wordText.substring(0, correctChars);
                    unmarkedLetters = wordText.substring(correctChars);
                }

                GlyphLayout markedLayout = new GlyphLayout(game.font, markedLetters);
                GlyphLayout unmarkedLayout = new GlyphLayout(game.font, unmarkedLetters);

                float totalWidth = markedLayout.width + unmarkedLayout.width;
                float startX = wordRectangle.x + (wordRectangle.width / 2) - (totalWidth / 2);

                game.font.setColor(0, 1, 0, 1); // Green for marked letters
                game.font.draw(game.batch, markedLetters, startX, wordRectangle.y + wordRectangle.height / 2);

                game.font.setColor(1, 1, 1, 1); // White for unmarked letters
                game.font.draw(game.batch, unmarkedLetters, startX + markedLayout.width, wordRectangle.y + wordRectangle.height / 2);
            }

            game.batch.end();

            Iterator<Rectangle> iter = words.iterator();
            while (iter.hasNext()) {
                Rectangle wordRectangle = iter.next();
                wordRectangle.y -= 75 * Gdx.graphics.getDeltaTime();
                if (wordRectangle.y < 64) {
                    int wordIndex = words.indexOf(wordRectangle, true);
                    if (indexOfWordToType == wordIndex) {
                        currentTypedWord = "";
                        indexOfWordToType = -1;
                    }
                    if (wordIndex != -1) {
                        wordsList.removeIndex(wordIndex);
                        iter.remove();
                    }
                }
            }

            if (wordsList.size <= 0) {
                gameOver = true;
                gameEndTime = TimeUtils.millis();
            } else {
                if (TimeUtils.timeSinceMillis(lastDropTime) > 2000 || words.isEmpty()) {
                    spawnWord();
                }
            }
        } else {
            stage.act();
            stage.draw();
        }
    }

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
        if (rainMusic == null) {
            rainMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/rpg-loop.wav"));
            rainMusic.setLooping(true);
        }
        if (!rainMusic.isPlaying()) {
            // rainMusic.play();
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
        rainMusic.pause();

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
        resumeButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            resume();
        }));

        returnButton.addListener(InputListenerFactory.createClickListener((event, x, y) -> {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }));

        // Add buttons to the stage
        table.add(resumeButton).width(300);
        table.row().padTop(10);
        table.add(returnButton).width(300);
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

        // Check if music and sound effects are initialized, otherwise, initialize them
        if (rainMusic == null) {
            rainMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/rpg-loop.wav"));
            rainMusic.setLooping(true);
        }
        if (dropSound == null) {
            dropSound = Gdx.audio.newSound(Gdx.files.internal("audio/forceField_000.ogg"));
        }
        if (explodeSound == null) {
            explodeSound = Gdx.audio.newSound(Gdx.files.internal("audio/vine-boom.mp3"));
        }
        if (otherExplodeSound == null) {
            otherExplodeSound = Gdx.audio.newSound(Gdx.files.internal("audio/explosionCrunch_000.ogg"));
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
        if (!rainMusic.isPlaying()) {
            // rainMusic.play();
        }
    }

    /**
     * Disposes of the game screen resources when they are no longer needed, such as when
     * the game is closed. This includes textures, sound effects, music, and the UI stage.
     */
    @Override
    public void dispose() {
        // Dispose of the texture if it's not null and not used elsewhere
        if (wordImage != null) {
            wordImage.dispose();
            wordImage = null; // Nullify the reference to avoid reuse.
        }

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
        if (rainMusic != null) {
            if (rainMusic.isPlaying()) {
                rainMusic.stop();
            }
            rainMusic.dispose();
            rainMusic = null;
        }

        // Dispose of the stage if it's not null
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
    }
}
