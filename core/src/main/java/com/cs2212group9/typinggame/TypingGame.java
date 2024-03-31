package com.cs2212group9.typinggame;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

/**
 * This class is responsible for starting and shutting down the game.
 * @author Group 9 members
 * @version 1.0
 */
public class TypingGame extends Game {
    /** Rendering 2D images */
    SpriteBatch batch;
    /** Rendering font */
    BitmapFont font;
    Texture backgroundTexture;
    private String username;
    private float sfxVolume = 1f;
    private float musicVolume = 0.4f;

    /**
     * default constructor for the TypingGame class.
     */
    public TypingGame() {
        super();
    }

    /**
     * Changes the volume of sound effects.
     * @param volume The new volume level for sound effects.
     */
    public void changeSFXVolume(float volume) {
        sfxVolume = volume;
    }

    /**
     * Changes the volume of the background music.
     * @param volume The new volume level for background music.
     */
    public void changeMusicVolume(float volume) {
        musicVolume = volume;
    }

    /**
     * Returns the volume of sound effects.
     * @return The volume level of sound effects.
     */
    public float getSFXVolume() {
        return sfxVolume;
    }

    /**
     * Returns the volume of background music.
     * @return The volume level of background music.
     */
    public float getMusicVolume() {
        return musicVolume;
    }

    /**
     * Returns the username of the player.
     * @return The username of the player.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the player.
     * @param username The username of the player.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Create the game by initializing a login screen
     */
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        // Initialize the background texture
        backgroundTexture = new Texture(Gdx.files.internal("background_game_screen.png"));
        // Set the initial screen of the game
        this.setScreen(new LoginScreen(this));
    }

    /**
     * Render the game
     */
    public void render() {
        super.render(); // important!
    }

    /**
     * close the window
     */
    public void dispose() {
        // Dispose all the disposables
        batch.dispose();
        font.dispose();
        backgroundTexture.dispose(); // Ensure to dispose of the background texture
    }
}
