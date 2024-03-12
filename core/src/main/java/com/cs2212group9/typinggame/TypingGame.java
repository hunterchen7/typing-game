package com.cs2212group9.typinggame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cs2212group9.typinggame.db.DBHelper;

public class TypingGame extends Game {

    SpriteBatch batch;
    BitmapFont font;

    public void create() {
        DBHelper dbHelper = new DBHelper();
        dbHelper.createNewTable();

        batch = new SpriteBatch();
        // Use LibGDX's default Arial font.
        font = new BitmapFont();
        this.setScreen(new LoginScreen(this));
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }

}
