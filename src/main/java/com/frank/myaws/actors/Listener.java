package com.frank.myaws.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.frank.myaws.pi.PiAdapter;

/**
 * @author ftorriani
 */
public class Listener extends AbstractActor {

    protected final LoggingAdapter log = Logging.getLogger( context().system(), this );

    private PiAdapter piAdapter;

    public static final class Message {

        public final String content;

        public Message( String content ) {
            this.content = content;
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
                match( Message.class, m -> {
                    log.info( "Received from AWS: {}", m.content );
                    piAdapter.toggle();
                } ).
                matchAny( any -> log.info( "Received and ignored {}", any ) ).
                build();
    }
}
