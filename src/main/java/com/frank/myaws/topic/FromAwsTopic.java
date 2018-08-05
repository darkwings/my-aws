package com.frank.myaws.topic;

import akka.actor.ActorRef;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.frank.myaws.action.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Subscribes from Messages from AWS IoT
 *
 * @author ftorriani
 */
public class FromAwsTopic extends AWSIotTopic {

    private static final Logger LOGGER = LogManager.getLogger( FromAwsTopic.class );

    private ActorRef actionExecutor;

    public FromAwsTopic( String topic, AWSIotQos qos, ActorRef actionExecutor ) {
        super( topic, qos );
        this.actionExecutor = actionExecutor;
    }

    @Override
    public void onMessage( AWSIotMessage message ) {
        Command.fromMessage( message.getStringPayload() ).
                ifPresent( command -> actionExecutor.tell( command, ActorRef.noSender() ) ).
                orElse( () -> LOGGER.warn( "Unknown message {}", message.getStringPayload() ) );
    }
}
