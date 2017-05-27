package com.frank.myaws.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.frank.myaws.action.Action;
import com.frank.myaws.pi.PiAdapter;

/**
 * @author ftorriani
 */
public class Listener extends AbstractActor {

    protected final LoggingAdapter log = Logging.getLogger( context().system(), this );

    private PiAdapter piAdapter;

    public static final class Command {

        public final Action action;

        public Command( Action action ) {
            this.action = action;
        }
    }

    public static Props props( PiAdapter piAdapter ) {
        return Props.create( Listener.class, piAdapter );
    }

    public Listener( PiAdapter piAdapter ) {
        this.piAdapter = piAdapter;
    }

    public Receive createReceive() {
        return receiveBuilder().
                match( Command.class, c -> {
                    log.debug( "Received from AWS: {}", c.action );
                    execute( c.action );
                } ).
                matchAny( any -> log.info( "Received and ignored {}", any ) ).
                build();
    }

    private void execute( Action action ) {
        switch (action.getAction()) {
            case TOGGLE_LIGHT:
                piAdapter.toggle();
                break;
            default:
                log.info( "Unknown action {}", action );
        }
    }


}
