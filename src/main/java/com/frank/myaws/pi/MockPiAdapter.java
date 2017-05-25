package com.frank.myaws.pi;

/**
 * @author ftorriani
 */
public class MockPiAdapter implements PiAdapter {

    private static int counter = 0;

    public void gpioMode( GpioMode mode ) {
        System.out.println( "GPIO mode set to " + mode );
    }

    public void pinMode( Integer pin, PinMode mode ) {
        System.out.println( "pin mode set to " + mode + " for pin " + pin );
    }

    public void out( Integer pin, boolean status ) {
        System.out.println( "status set to '" + status + "' for pin " + pin );
    }

    public boolean in( Integer pin ) {
        counter++;
        return counter % 2 == 0;
    }
}
