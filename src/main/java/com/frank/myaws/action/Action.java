package com.frank.myaws.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frank.myaws.util.SmartOptional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Immutable object that represents an action invocation received from
 * AWS.
 *
 * @author ftorriani
 */
public class Action {

    private static final Logger LOGGER = LogManager.getLogger( Action.class );

    /**
     * The type of the action
     */
    private Type action;

    /**
     * The location this action is directed to
     */
    private Location location;

    public Action() {
    }

    public Action( Type action, Location location ) {
        this.action = action;
        this.location = location;
    }

    public Type getAction() {
        return action;
    }

    private void setAction( Type action ) {
        this.action = action;
    }

    public Location getLocation() {
        return location;
    }

    private void setLocation( Location location ) {
        this.location = location;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder( "Action {" );
        sb.append( "action=" ).append( action );
        sb.append( ", location=" ).append( location );
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
