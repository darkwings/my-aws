package com.frank.myaws.actors.client;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.frank.myaws.actors.client.ClientActionHandlerMessages.*;
import com.frank.myaws.client.AwsClient;
import com.frank.myaws.topic.FromAwsTopic;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

/**
 * @author ftorriani
 */
public class ClientActionHandler extends AbstractActor {

    protected final LoggingAdapter log = Logging.getLogger( context().system(), this );

    private FromAwsTopic topic;
    private AwsClient client;

    public static Props props( FromAwsTopic topic, AwsClient client ) {
        return Props.create( ClientActionHandler.class, topic, client );
    }

    public static String name() {
        return "web-event-handler";
    }

    private ClientActionHandler( FromAwsTopic topic, AwsClient client ) {
        this.topic = topic;
        this.client = client;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match( Disconnect.class, d -> {
                    log.debug( "Received from web: {}", d );
                    client.disconnect();

                    getSender().tell( new ActionPerformed( "Disconnected AWS client " ), getSelf() );
                } ).
                match( Connect.class, d -> {
                    log.debug( "Received from web: {}", d );

                    client.connect();
                    client.subscribe( topic );

                    getSender().tell( new ActionPerformed( "Connected AWS client and topic subscribed" ), getSelf() );
                } ).
                match( InternalConnect.class, d -> {
                    log.debug( "Received from web: {}", d );

                    client.connect();
                    client.subscribe( topic );
                } ).
                match( Reconnect.class, d -> {
                    log.debug( "Received from web: {}", d );

                    log.info( "Client disconnects" );
                    client.disconnect();

                    getContext().getSystem().scheduler().scheduleOnce( Duration.ofSeconds( 4 ), self(), new InternalConnect(),
                            getContext().getSystem().dispatcher(), ActorRef.noSender() );

                    getSender().tell( new ActionPerformed( "Disconnected and reconnected topic " + topic.getTopic() ), getSelf() );
                } ).
                matchAny( any -> log.info( "Received and ignored {}", any ) ).
                build();
    }
}
