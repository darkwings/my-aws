package com.frank.myaws.aws;

import akka.actor.ActorRef;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.frank.myaws.action.Action;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Subscribes from Messages from AWS IoT
 *
 * @author ftorriani
 */
public class FromAwsTopic extends AWSIotTopic {

    private static final Logger LOGGER = LogManager.getLogger( FromAwsTopic.class );

    private ActorRef listener;

    public FromAwsTopic( String topic, AWSIotQos qos, ActorRef listener ) {
        super( topic, qos );
        this.listener = listener;
    }

    @Override
    public void onMessage( AWSIotMessage message ) {
        Action.fromMessage( message.getStringPayload() ).ifPresent( action -> {
            listener.tell( action, ActorRef.noSender() );
        } ).orElse( () -> {
            LOGGER.warn( "Unknown message {}", message.getStringPayload() );
        } );
    }
}
