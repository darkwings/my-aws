package com.frank.myaws.actors.web;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.frank.myaws.actors.web.WebEventHandlerMessages.ActionPerformed;
import com.frank.myaws.actors.web.WebEventHandlerMessages.Connect;
import com.frank.myaws.actors.web.WebEventHandlerMessages.Disconnect;
import com.frank.myaws.actors.web.WebEventHandlerMessages.Reconnect;
import com.frank.myaws.topic.FromAwsTopic;

/**
 * @author ftorriani
 */
public class WebEventHandler extends AbstractActor {

    protected final LoggingAdapter log = Logging.getLogger( context().system(), this );

    private FromAwsTopic topic;
    private AWSIotMqttClient client;

    public static Props props( FromAwsTopic topic, AWSIotMqttClient client ) {
        return Props.create( WebEventHandler.class, topic, client );
    }

    public static String name() {
        return "web-event-handler";
    }

    private WebEventHandler( FromAwsTopic topic, AWSIotMqttClient client ) {
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
                match( Reconnect.class, d -> {
                    log.debug( "Received from web: {}", d );

                    log.info( "Client disconnects" );
                    client.disconnect();

                    // Sleep needed??

                    log.info( "Client connecting and subscribing" );
                    client.connect();
                    client.subscribe( topic );

                    getSender().tell( new ActionPerformed( "Disconnected and reconnected topic " + topic.getTopic() ), getSelf() );
                } ).
                matchAny( any -> log.info( "Received and ignored {}", any ) ).
                build();
    }
}
