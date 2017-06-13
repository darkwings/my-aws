package com.frank.myaws.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.frank.myaws.action.Action;
import com.frank.myaws.action.Location;
import com.frank.myaws.pi.PiAdapter;

import java.util.Map;

/**
 * Executes an {@link Action} received from AWS IoT topic
 *
 * @author ftorriani
 */
public class ActionExecutor extends AbstractActor {

    protected final LoggingAdapter log = Logging.getLogger( context().system(), this );

    private Map<Location, PiAdapter> piAdapters;

    public static Props props( Map<Location, PiAdapter> piAdapters ) {
        return Props.create( ActionExecutor.class, piAdapters );
    }

    public static String name() {
        return "action-executor";
    }

    public ActionExecutor( Map<Location, PiAdapter> piAdapters ) {
        this.piAdapters = piAdapters;
    }

    public Receive createReceive() {
        return receiveBuilder().
                match( Action.class, a -> {
                    log.debug( "Received from AWS: {}", a );
                    execute( a );
                } ).
                matchAny( any -> log.info( "Received and ignored {}", any ) ).
                build();
    }

    private void execute( Action action ) {
        Location location;
        PiAdapter piAdapter;
        switch (action.getAction()) {
            case TOGGLE_LIGHT:
                location = action.getLocation();
                log.info( "Executing TOGGLE_LIGHT action for Location {}", location );
                piAdapter = piAdapters.get( location );
                if ( piAdapter != null ) {
                    piAdapter.toggle();
                }
                break;
            case TURN_ON:
                location = action.getLocation();
                log.info( "Executing TURN_ON action for Location {}", location );
                piAdapter = piAdapters.get( location );
                if ( piAdapter != null ) {
                    piAdapter.toggle();
                }
                break;
            case TURN_OFF:
                location = action.getLocation();
                log.info( "Executing TURN_OFF action for Location {}", location );
                piAdapter = piAdapters.get( location );
                if ( piAdapter != null ) {
                    piAdapter.toggle();
                }
                break;
            default:
                log.info( "Unknown action {}", action );
        }
    }


}
