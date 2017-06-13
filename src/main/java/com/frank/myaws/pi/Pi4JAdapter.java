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
    private String name;

    private GpioPinDigitalOutput out;

    private boolean initialized;

    public Pi4JAdapter() {
        gpio = GpioFactory.getInstance();
        initialized = false;
    }

    @Override
    public void init( String name, Integer pin, PinMode mode ) {
        LOGGER.info( "Adapter {}: setting mode {} on pin {}", name, mode, pin );
        this.pin = pin;
        this.mode = mode;
        this.name = name;

        if ( mode == PinMode.OUT ) {
            getPin( pin ).ifPresent( raspiPin -> {
                LOGGER.info( "Initializing pin {}", raspiPin );
                out = gpio.provisionDigitalOutputPin( raspiPin, "MyPin", PinState.HIGH );
                out.setShutdownOptions( true, PinState.LOW );
                out.low();
                initialized = true;
            } );
        }
    }

    @Override
    public void toggle() {
        checkInitialized();

        LOGGER.debug( "toggle() called on adapter {}", name );

        if ( mode == PinMode.IN ) {
            LOGGER.info( "Adapter {}: pin {} is IN mode, toggle not supported()", name, pin );
            return;
        }

        out.toggle();
    }

    @Override
    public void on() {
        checkInitialized();
        LOGGER.debug( "on() called on adapter {}", name );

        if ( mode == PinMode.IN ) {
            LOGGER.info( "Adapter {}: pin {} is IN mode, toggle not supported()", name, pin );
            return;
        }

        out.high();
    }

    @Override
    public void off() {
        checkInitialized();
        LOGGER.debug( "off() called on adapter {}", name );

        if ( mode == PinMode.IN ) {
            LOGGER.info( "Adapter {}: pin {} is IN mode, toggle not supported()", name, pin );
            return;
        }

        out.low();
    }

    void checkInitialized() {
        if (!initialized) {
            LOGGER.warn( "Adapter {} not initalized", name );
            throw new IllegalStateException( "Adapter " + name + " not initialized" );
        }
    }

    @Override
    public boolean in() {
        if ( mode == PinMode.OUT ) {
            LOGGER.info( "Adapter {}: pin {} is OUT mode, read NOT supported()", name, pin );
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

        return Optional.of( RaspiPin.getPinByAddress( gpioPinNum ) );
    }

    @Override
    public String getName() {
        return name;
    }
}
