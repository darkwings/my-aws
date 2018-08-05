package com.frank.myaws.actors.aws;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.frank.myaws.client.AwsClient;

/**
 * @author ftorriani
 */
public class Publisher extends AbstractActor {

    protected final LoggingAdapter log = Logging.getLogger( context().system(), this );

    private AwsClient client;

    /**
     * The message to be pushed to AWS
     */
    public static class Message {
        public final String topic;
        public final String payload;

        public Message( String topic, String payload ) {
            this.topic = topic;
            this.payload = payload;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder( "Command {" );
            sb.append( "topic='" ).append( topic ).append( '\'' );
            sb.append( ", payload='" ).append( payload ).append( '\'' );
            sb.append( '}' );
            return sb.toString();
        }
    }

    public static Props props( AwsClient client ) {
        return Props.create( Publisher.class, client );
    }

    public static String name() {
        return "publisher";
    }

    public Publisher( AwsClient client ) {
        this.client = client;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match( Message.class, m -> {
                    log.info( "Sending {} to AWS", m );
                    MyAwsMessage message = new MyAwsMessage( m.topic, AWSIotQos.QOS0, m.payload );
                    client.publish( message , 3000L );
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
