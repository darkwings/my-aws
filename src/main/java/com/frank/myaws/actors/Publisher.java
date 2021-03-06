package com.frank.myaws.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;

/**
 * @author ftorriani
 */
public class Publisher extends AbstractActor {

    protected final LoggingAdapter log = Logging.getLogger( context().system(), this );

    private AWSIotMqttClient client;

    public static class Message {
        public final String topic;
        public final String payload;

        public Message( String topic, String payload ) {
            this.topic = topic;
            this.payload = payload;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder( "Message {" );
            sb.append( "topic='" ).append( topic ).append( '\'' );
            sb.append( ", payload='" ).append( payload ).append( '\'' );
            sb.append( '}' );
            return sb.toString();
        }
    }

    public static Props props( AWSIotMqttClient client ) {
        return Props.create( Publisher.class, client );
    }

    public Publisher( AWSIotMqttClient client ) {
        this.client = client;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match( Message.class, m -> {
                    log.info( "Sending {} to AWS", m );
                    MyAwsMessage message = new MyAwsMessage( m.topic, AWSIotQos.QOS0, m.payload );
                    client.publish( message ,3000 );
                }).
                matchAny( any -> log.info( "Received and ignored {}", any ) ).
                build();
    }

    /**
     * @author ftorriani
     */
    private static class MyAwsMessage extends AWSIotMessage {

        public MyAwsMessage( String topic, AWSIotQos qos, String payload ) {
            super( topic, qos, payload );
        }

        @Override
        public void onSuccess() {
            super.onSuccess();
        }

        @Override
        public void onFailure() {
            super.onFailure();
        }

        @Override
        public void onTimeout() {
            super.onTimeout();
        }
    }
}
