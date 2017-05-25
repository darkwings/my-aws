package com.frank.myaws.pi;

/**
 * @author ftorriani
 */
public interface PiAdapter {

    enum PinMode {
        IN, OUT
    }

    enum GpioMode {
        GPIO, BCM;
    }

    void gpioMode( GpioMode mode );

    void pinMode( Integer pin, PinMode mode );

    void out( Integer pin, boolean status );

    boolean in( Integer pin );
}
