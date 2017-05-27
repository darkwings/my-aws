package com.frank.myaws.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frank.myaws.util.SmartOptional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author ftorriani
 */
public class Action {

    private static final Logger LOGGER = LogManager.getLogger( Action.class );

    public enum Type {
        TOGGLE_LIGHT
    }

    private Type action;

    public Action() {
    }

    public Action( Type action ) {
        this.action = action;
    }

    public Type getAction() {
        return action;
    }

    private void setAction( Type action ) {
        this.action = action;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder( "Action {" );
        sb.append( "action=" ).append( action );
        sb.append( '}' );
        return sb.toString();
    }

    public static SmartOptional<Action> fromMessage( String message ) {
        try {
            return SmartOptional.of( new ObjectMapper().readValue( message, Action.class ) );
        }
        catch ( Exception e ) {
            LOGGER.error( "Error", e );
            return SmartOptional.empty();
        }
    }
}
