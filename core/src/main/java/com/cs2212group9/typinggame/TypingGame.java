package com.cs2212group9.typinggame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class TypingGame extends ApplicationAdapter {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture smile;
    private Rectangle square;
    private Texture fith;
    private Array<Rectangle> fiths;
    private long lastDropTime;

    private void spawnRainDrop() {
        Rectangle fith = new Rectangle();
        fith.x = MathUtils.random(0, 800-64);
        fith.y = 480;
        fith.width = 64;
        fith.height = 64;
        fiths.add(fith);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1200, 800);
        batch = new SpriteBatch();
        smile = new Texture("sprites/blue_smile.png");
        fith = new Texture("sprites/fire_in_the_hole.png");

        fiths = new Array<Rectangle>();
        spawnRainDrop();

        square = new Rectangle();
        square.x = (float) 1200 / 2 - (float) 64 / 2;
        square.y = 20;
        square.width = 64;
        square.height = 64;
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(smile, square.x, square.y);
        for (Rectangle f : fiths) {
            batch.draw(fith, f.x, f.y);
        }
        batch.end();

        // follow mouse
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            square.x = touchPos.x - (float) 64 / 2;
        }

        // move with left/right arrow keys
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) square.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) square.x += 200 * Gdx.graphics.getDeltaTime();

        // keep within bounds
        if (square.x < 0) square.x = 0;
        if (square.x > 1200 - 64) square.x = 1200 - 64;

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRainDrop();

        for (Iterator<Rectangle> iter = fiths.iterator(); iter.hasNext(); ) {
            Rectangle fith = iter.next();
            fith.y -= 200 * Gdx.graphics.getDeltaTime();
            if (fith.y + 64 < 0) iter.remove();
            if (fith.overlaps(square)) {
                iter.remove();
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        smile.dispose();
        fith.dispose();
    }
}
