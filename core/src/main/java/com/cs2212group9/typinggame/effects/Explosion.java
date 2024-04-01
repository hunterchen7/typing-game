package com.cs2212group9.typinggame.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * This class is responsible for creating, rendering and destroying the explosion effect when a word is typed
 */
public class Explosion {
    /** length of each frame in the explosion animation, 1/60 for 60 fps */
    public static final float FRAME_LENGTH = 1.0f / 60.0f;
    /** offset of the explosion from the center of the word */
    public static final int OFFSET = 96;
    /** size of each frame in the explosion animation, based on atlas */
    public static final int SIZE = 256;
    /** explosion animation */
    private static Animation<TextureRegion> anim = null;
    float x, y;
    float stateTime = 0;

    /** indicates to parent class to remove this explosion */
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
            // split atlas into 2D array
            TextureRegion[][] temp = TextureRegion.split(new Texture("effects/explosion3.png"), SIZE, SIZE);
            TextureRegion[] explosionFrames = new TextureRegion[temp.length * temp[0].length];
            // flatten 2D TextureRegion array into 1D array
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
        batch.draw(anim.getKeyFrame(stateTime), x, y, SIZE, SIZE);
    }

}
