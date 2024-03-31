package com.cs2212group9.typinggame.effects;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class WordBackground extends Actor {
    private final Texture texture;

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

    @Override
    public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }
}
