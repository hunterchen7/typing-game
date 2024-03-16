package com.cs2212group9.typinggame;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
import com.cs2212group9.typinggame.db.DBHelper;
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

    Array<String> wordsList;

    private String currentTypedWord = "";
    private int score = 0;
    private int indexOfWordToType = -1; // Index of the word that the player is currently typing

    public GameScreen(final TypingGame gam) {
        this.game = gam;
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
        wordsList = new Array<String>();
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
        wordsList.add("Happy", "Apple","Hello","Bob");
        lastDropTime = TimeUtils.nanoTime();
    }

    private boolean state = true;
    private String selectedWord = ""; // The word currently being typed/spelled
    private boolean wordSelected = false; // Flag to indicate if a word is currently selected

    /*
    private void checkWordSelection() {
        // If no word is selected and the typed character corresponds to a word
        if (!wordSelected && currentTypedWord.length() == 1) {
            for (String word : wordsList) {
                if (word.toUpperCase().startsWith(currentTypedWord.toUpperCase())) {
                    selectedWord = word;
                    wordSelected = true;
                    break;
                }
            }
        }
    }


    private void moveBucketToWord() {
        if (!wordSelected) return;

        for (int i = 0; i < wordsList.size; i++) {
            if (wordsList.get(i).equalsIgnoreCase(selectedWord)) {
                Rectangle wordRect = words.get(i);
                bucket.x = wordRect.x + (wordRect.width - bucket.width) / 2; // Center the bucket under the word
                return;
            }
        }
    }

    private void moveBucketToWordStartingWith(char startingChar) {
        for (int i = 0; i < wordsList.size; i++) {
            if (wordsList.get(i).toLowerCase().startsWith(String.valueOf(startingChar))) {
                Rectangle wordRectangle = words.get(i);
                bucket.x = wordRectangle.x + wordRectangle.width / 2 - bucket.width / 2; // Center the bucket under the word
                currentTypedWord = wordsList.get(i); // Set the current word being typed
                return; // Exit once the first match is found
            }
        }
        // If no word starts with the typed letter, do not move the bucket
    }

    // Call this method in your render method to remove words that are off the screen or have been completed/misspelled
    private void removeWord(int index) {
        if (index >= 0 && index < wordsList.size) {
            wordsList.removeIndex(index);
            words.removeIndex(index);
        }
    }
    */

    private void handleInput() {
        if (indexOfWordToType == -1) return; // If no word is being typed, don't process input

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
    }

    /*
    private void checkWordCompletion() {
        if (wordSelected && currentTypedWord.equalsIgnoreCase(selectedWord)) {
            score++; // Increment score for word completion
            int index = wordsList.indexOf(selectedWord, false);
            wordsList.removeIndex(index); // Remove the completed word
            words.removeIndex(index); // Also remove the corresponding Rectangle
            currentTypedWord = ""; // Reset typed word for the next target
            selectedWord = ""; // Reset selected word
            wordSelected = false; // No word is currently selected
        } else if (wordSelected && !selectedWord.startsWith(currentTypedWord)) {
            // If the user has started typing a word and types a wrong letter,
            // reset the typed word and deselect the current word
            currentTypedWord = "";
            selectedWord = "";
            wordSelected = false;
        }
    }
    */

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
        ScreenUtils.clear(0, 0, 0.2f, 1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Update the bottom-most word index
        updateWordToTypeIndex();

        game.batch.begin();
        game.font.draw(game.batch, "Drops Collected: " + score, 0, 480);
        game.batch.draw(bucketImage, bucket.x, bucket.y);

        for (int i = 0; i < words.size; i++) {
            Rectangle wordRectangle = words.get(i);
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
            game.font.setColor(1, 1, 0, 1); // Yellow for marked letters
            game.font.draw(game.batch, markedLetters,
                wordRectangle.x + (wordRectangle.width - (markedLayout.width + unmarkedLayout.width)) / 2,
                wordRectangle.y + wordRectangle.height / 2);

            // Draw the unmarked part of the word
            game.font.setColor(1, 1, 1, 1); // White for unmarked letters
            game.font.draw(game.batch, unmarkedLetters,
                wordRectangle.x + (wordRectangle.width - (markedLayout.width + unmarkedLayout.width)) / 2 + markedLayout.width,
                wordRectangle.y + wordRectangle.height / 2);
        }
        game.batch.end();

        handleInput();

        // Process falling words
        Iterator<Rectangle> iter = words.iterator();
        while (iter.hasNext()) {
            Rectangle wordRectangle = iter.next();
            int index = words.indexOf(wordRectangle, true);
            wordRectangle.y -= 200 * Gdx.graphics.getDeltaTime();
            if (wordRectangle.y + 64 < 0) {
                iter.remove();
                wordsList.removeIndex(index); // Remove the corresponding word text
                if (indexOfWordToType == index) {
                    // Reset the typing if the bottom word was dropped
                    currentTypedWord = "";
                    indexOfWordToType = -1;
                }
            } else if (wordRectangle.overlaps(bucket)) {
                dropsGathered++;
                dropSound.play();
                iter.remove();
                wordsList.removeIndex(index); // Remove the corresponding word text
            }
        }

        // Check if a new word needs to be spawned
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();
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
            game.setScreen(new MainMenuScreen(game));
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
