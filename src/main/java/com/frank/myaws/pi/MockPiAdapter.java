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

    private Integer pin;
    private PinMode pinMode;
    private boolean status = false;

    @Override
    public void init( Integer pin, PinMode mode ) {
        this.pin = pin;
        this.pinMode = mode;
    }

    @Override
    public void toggle() {
        status = !status;
        LOGGER.info( "Received toggle. Status is {}", status );
    }

    @Override
    public boolean in() {
        counter++;
        return counter %2 == 0;
    }
}