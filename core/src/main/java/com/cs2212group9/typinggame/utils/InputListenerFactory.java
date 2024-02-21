package com.cs2212group9.typinggame.utils;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

// from https://github.com/szsascha/libgdx-multiplayer-authentication-flow
public class InputListenerFactory {
    public static ClickListener createClickListener(TriConsumer<InputEvent, Float, Float> consumer) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                consumer.accept(event, x, y);
            }
        };
    }

}
