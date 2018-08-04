package com.frank.myaws.actors.aws;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.frank.myaws.action.aws.Command;
import com.frank.myaws.action.aws.Location;
import com.frank.myaws.pi.PiAdapter;

import java.util.Map;

/**
 * Executes an {@link Command} received from AWS IoT topic
 *
 * @author ftorriani
 */
public class CommandExecutor extends AbstractActor {

    protected final LoggingAdapter log = Logging.getLogger( context().system(), this );

    private Map<Location, PiAdapter> piAdapters;

    public static Props props( Map<Location, PiAdapter> piAdapters ) {
        return Props.create( CommandExecutor.class, piAdapters );
    }

    public static String name() {
        return "action-executor";
    }

    public CommandExecutor( Map<Location, PiAdapter> piAdapters ) {
        this.piAdapters = piAdapters;
    }

    public Receive createReceive() {
        return receiveBuilder().
                match( Command.class, a -> {
                    log.debug( "Received from AWS: {}", a );
                    execute( a );
                } ).
                matchAny( any -> log.info( "Received and ignored {}", any ) ).
                build();
    }

    private void execute( Command command ) {
        Location location;
        PiAdapter piAdapter;
        switch ( command.getAction()) {
            case TOGGLE_LIGHT:
                location = command.getLocation();
                log.info( "Executing TOGGLE_LIGHT command for Location {}", location );
                piAdapter = piAdapters.get( location );
                if ( piAdapter != null ) {
                    piAdapter.toggle();
                }
                break;
            case TURN_ON:
                location = command.getLocation();
                log.info( "Executing TURN_ON command for Location {}", location );
                piAdapter = piAdapters.get( location );
                if ( piAdapter != null ) {
                    piAdapter.on();
                }
                break;
            case TURN_OFF:
                location = command.getLocation();
                log.info( "Executing TURN_OFF command for Location {}", location );
                piAdapter = piAdapters.get( location );
                if ( piAdapter != null ) {
                    piAdapter.off();
                }
                break;
            default:
                log.info( "Unknown command {}", command );
        }
    }


}
