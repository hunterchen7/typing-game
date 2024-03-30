package com.cs2212group9.typinggame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cs2212group9.typinggame.db.DBHelper;
import com.badlogic.gdx.graphics.Texture;
import com.cs2212group9.typinggame.LoginScreen;
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

    public String getUsername() {
        return username;
    }

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
