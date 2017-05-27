package com.frank.myaws.util;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author ftorriani
 */
public class SmartOptionalTest {

    static class Tested {
        String message;
    }

    static class OrElse {
        String message;
    }

    @Test
    public void smartOptionalOrElse() {

        SmartOptional<Tested> opt = SmartOptional.ofNullable( null );

        OrElse orElse = new OrElse();
        opt.ifPresent( t -> t.message = "OK" ).orElse( () -> orElse.message = "orElse" );

        assertThat( orElse.message ).isEqualTo( "orElse" );
    }

    @Test
    public void smartOptionalIfPresent() {

        Tested tested = new Tested();
        SmartOptional<Tested> opt = SmartOptional.ofNullable( tested );

        OrElse orElse = new OrElse();
        opt.ifPresent( t -> t.message = "OK" ).orElse( () -> orElse.message = "orElse" );

        assertThat( tested.message ).isEqualTo( "OK" );
        assertThat( orElse.message ).isNull();
    }
}