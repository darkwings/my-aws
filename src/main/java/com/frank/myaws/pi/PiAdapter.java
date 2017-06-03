package com.frank.myaws.pi;

/**
 * @author ftorriani
 */
public interface PiAdapter {

    enum PinMode {
        IN, OUT
    }

    void init( String name, Integer pin, PinMode mode );

    void toggle();

    boolean in();

    String getName();
}
