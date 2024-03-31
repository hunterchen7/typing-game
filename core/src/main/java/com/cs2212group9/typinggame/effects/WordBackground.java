package com.cs2212group9.typinggame.effects;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Represents the black background behind the word in the game.
 */
public class WordBackground extends Actor {
    private final Texture texture;

    /**
     * Creates a new WordBackground with the given dimensions.
     * @param x the x-coordinate of the background
     * @param y the y-coordinate of the background
     * @param width the width of the background
     * @param height the height of the background
     */
    public WordBackground(float x, float y, float width, float height) {
        Pixmap pixmap = new Pixmap((int) width, (int) height, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.fillRectangle(0, 0, (int) width, (int) height);
        texture = new Texture(pixmap);
        pixmap.dispose();
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
    }

    /**
     * Draws the background.
     * @param batch the batch to draw the background on
     * @param parentAlpha the alpha value of the parent
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }
}
