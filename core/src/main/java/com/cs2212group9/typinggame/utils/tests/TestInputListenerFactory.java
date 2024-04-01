package com.cs2212group9.typinggame.utils.tests;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cs2212group9.typinggame.utils.InputListenerFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the InputListenerFactory class
 */
public class TestInputListenerFactory {
    /**
     * Test creating a ClickListener
     */
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
