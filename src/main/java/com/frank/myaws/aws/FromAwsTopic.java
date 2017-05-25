package com.frank.myaws.aws;

import akka.actor.ActorRef;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.frank.myaws.actors.Listener;

/**
 * Messages from AWS
 *
 * @author ftorriani
 */
public class FromAwsTopic extends AWSIotTopic {

    private ActorRef listener;

    public FromAwsTopic( String topic, AWSIotQos qos, ActorRef listener ) {
        super( topic, qos );
        this.listener = listener;
    }

    @Override
    public void onMessage( AWSIotMessage message ) {
        listener.tell( new Listener.Message( message.getStringPayload() ), ActorRef.noSender() );
    }
}
