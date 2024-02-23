package com.cs2212group9.typinggame.utils;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.security.NoSuchAlgorithmException;

// from https://github.com/szsascha/libgdx-multiplayer-authentication-flow
public class InputListenerFactory {
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
