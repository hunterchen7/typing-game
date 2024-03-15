package com.cs2212group9.typinggame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cs2212group9.typinggame.db.DBHelper;

public class TypingGame extends Game {

    SpriteBatch batch;
    BitmapFont font;

    /**
     * Create the game by initializing a login screen
     */
    public void create() {
        batch = new SpriteBatch();
        // Use LibGDX's default Arial font.
        font = new BitmapFont();
        this.setScreen(new LoginScreen(this));
    }

    /**
     * Render the game
     */
    public void render() {
        super.render(); // important!
    }

    /**
     * Remember to clean up after yourself, don't leave everything for the gc :^)
     */
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

}
