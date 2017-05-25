package com.frank.myaws.pi;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author ftorriani
 */
public class Pi4JAdapterTest {


    @Test
    public void getPin() {

        Pin pin = Pi4JAdapter.getPin( 25 ).get();

        assertThat( pin ).isEqualTo( RaspiPin.GPIO_25 );
    }
}