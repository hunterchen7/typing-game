package com.cs2212group9.typinggame.utils;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

/**
 * Factory class to create input listeners for buttons
 */
// from https://github.com/szsascha/libgdx-multiplayer-authentication-flow
public class InputListenerFactory {
    /**
     * Create a ClickListener with a consumer
     * @param consumer - the consumer to be called when the event is triggered
     * @return the ClickListener
     */
    public static ClickListener createClickListener(TriConsumer<InputEvent, Float, Float> consumer) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                try {
                    consumer.accept(event, x, y);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
