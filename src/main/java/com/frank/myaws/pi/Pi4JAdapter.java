package com.frank.myaws.pi;

import com.pi4j.io.gpio.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 * @author ftorriani
 */
public class Pi4JAdapter implements PiAdapter {

    private static final Logger LOGGER = LogManager.getLogger( Pi4JAdapter.class );

    private GpioController gpio;
    private Integer pin;
    private PinMode mode;

    private GpioPinDigitalOutput out;

    private boolean initialized;

    public Pi4JAdapter() {
        gpio = GpioFactory.getInstance();
        initialized = false;
    }

    @Override
    public void init( Integer pin, PinMode mode ) {
        this.pin = pin;
        this.mode = mode;

        if ( mode == PinMode.OUT ) {

            getPin( pin ).ifPresent( raspiPin -> {
                out = gpio.provisionDigitalOutputPin( raspiPin, "MyPin", PinState.HIGH );
                out.setShutdownOptions( true, PinState.LOW );
                out.low();
                initialized = true;
            } );
        }
    }

    @Override
    public void toggle() {
        if (!initialized) {
            LOGGER.warn( "Adapter not initalized" );
            return;
        }

        if ( mode == PinMode.IN ) {
            LOGGER.info( "Pin {} is IN mode, toggle not supported()", pin );
            return;
        }

        out.toggle();
    }

    @Override
    public boolean in() {
        if ( mode == PinMode.OUT ) {
            LOGGER.info( "Pin {} is OUT mode, read NOT supported()", pin );
            return false;
        }

        // TODO
        return false;
    }

    protected static Optional<Pin> getPin( Integer gpioPinNum ) {
        if (gpioPinNum == null) {
            return Optional.empty();
        }
        if (gpioPinNum < 1 || gpioPinNum > 31 ) {
            LOGGER.warn( "Pin {} not valid", gpioPinNum );
            return Optional.empty();
        }

//        StringBuilder builder = new StringBuilder( "GPIO_" );
//        if ( gpioPinNum < 10 ) {
//            builder.append( "0" );
//        }
//        builder.append( gpioPinNum );

        return Optional.of( RaspiPin.getPinByAddress( gpioPinNum ) );
    }
}
