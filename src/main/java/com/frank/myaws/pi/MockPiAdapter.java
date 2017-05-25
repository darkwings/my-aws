package com.frank.myaws.pi;

import com.frank.myaws.App;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author ftorriani
 */
public class MockPiAdapter implements PiAdapter {

    private static final Logger LOGGER = LogManager.getLogger( MockPiAdapter.class );

    private static int counter = 0;

    public void gpioMode( GpioMode mode ) {

        LOGGER.info( "GPIO mode set to {}", mode );
    }

    public void pinMode( Integer pin, PinMode mode ) {
        LOGGER.info( "pin mode set to {} for pin {}", mode, pin );
    }

    public void out( Integer pin, boolean status ) {
        LOGGER.info( "status set to '{}' for pin {}", status, pin );
    }

    public boolean in( Integer pin ) {
        counter++;
        return counter % 2 == 0;
    }
}
