package com.frank.myaws.pi;

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
    private String name;

    @Override
    public void init( String name, Integer pin, PinMode mode ) {
        this.pin = pin;
        this.pinMode = mode;
        this.name = name;
    }

    @Override
    public void toggle() {
        status = !status;
        LOGGER.info( "Received toggle. Status is {}", status );
    }

    @Override
    public void on() {
        status = true;
        LOGGER.info( "Received ON. Status is {}", status );
    }

    @Override
    public void off() {
        status = false;
        LOGGER.info( "Received OFF. Status is {}", status );
    }

    @Override
    public boolean in() {
        counter++;
        return counter %2 == 0;
    }

    @Override
    public String getName() {
        return name;
    }
}
