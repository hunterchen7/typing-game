package com.cs2212group9.typinggame.utils;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestInputListenerFactory {
    @Test
    void testCreateClickListener() {
        var ref = new Object() { // anon object to hold a mutable int
            int count = 1;
        };
        ClickListener listener = InputListenerFactory.createClickListener((event, x, y) -> {
            System.out.println("clicked at " + x + ", " + y);
            ref.count++;
        });
        listener.clicked(null, 1, 2);
        assertEquals(2, ref.count);
    }
}
