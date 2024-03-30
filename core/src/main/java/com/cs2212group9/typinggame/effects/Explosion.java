package com.cs2212group9.typinggame.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Random;

public class Explosion {
    public static final float FRAME_LENGTH = 1.0f / 60.0f;
    public static final int OFFSET = 96;
    public static final int SIZE = 256;

    private static Animation<?> anim = null;
    float x, y;
    float stateTime = 0;

    public boolean remove = false;

    /**
     * Creates an explosion at the given coordinates
     * @param x x-coordinate of the explosion
     * @param y y-coordinate of the explosion
     */
    public Explosion(float x, float y) {
        this.x = x - OFFSET;
        this.y = y - OFFSET;
        // initialize animation
        if (anim == null) {
            TextureRegion[][] temp = TextureRegion.split(new Texture("effects/explosion3.png"), SIZE, SIZE);
            // flatten 2D TextureRegion array into 1D array
            TextureRegion[] explosionFrames = new TextureRegion[temp.length * temp[0].length];
            int index = 0;
            for (TextureRegion[] textureRegions : temp) {
                for (TextureRegion textureRegion : textureRegions) {
                    explosionFrames[index++] = textureRegion;
                }
            }
            // create animation
            anim = new Animation<>(FRAME_LENGTH, explosionFrames);
        }
    }

    /**
     * Updates the explosion
     * @param delta time since last update
     */
    public void update(float delta) {
        stateTime += delta;
        if (anim.isAnimationFinished(stateTime)) {
            remove = true;
        }
    }

    /**
     * Renders the explosion
     * @param batch SpriteBatch to render the explosion
     */
    public void render(SpriteBatch batch) {
        batch.draw((TextureRegion) anim.getKeyFrame(stateTime), x, y, SIZE, SIZE);
    }

}
