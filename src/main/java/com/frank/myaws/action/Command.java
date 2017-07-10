package com.frank.myaws.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frank.myaws.util.SmartOptional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Immutable object that represents a command received from
 * AWS.
 *
 * @author ftorriani
 */
public class Command {

    private static final Logger LOGGER = LogManager.getLogger( Command.class );

    /**
     * The the action
     */
    private Action action;

    /**
     * The location this action is directed to
     */
    private Location location;

    public Command() {
    }

    public Command( Action action, Location location ) {
        this.action = action;
        this.location = location;
    }

    public Action getAction() {
        return action;
    }

    private void setAction( Action action ) {
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
        final StringBuilder sb = new StringBuilder( "Command {" );
        sb.append( "action=" ).append( action );
        sb.append( ", location=" ).append( location );
        sb.append( '}' );
        return sb.toString();
    }

    public static SmartOptional<Command> fromMessage( String message ) {
        try {
            return SmartOptional.of( new ObjectMapper().readValue( message, Command.class ) );
        }
        catch ( Exception e ) {
            LOGGER.error( "Error", e );
            return SmartOptional.empty();
        }
    }
}
