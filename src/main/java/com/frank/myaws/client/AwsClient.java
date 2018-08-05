package com.frank.myaws.client;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.frank.myaws.client.AwsClient.Status.CONNECTED;
import static com.frank.myaws.client.AwsClient.Status.DISCONNECTED;

/**
 * @author ftorriani
 */
public class AwsClient {

    private static final Logger LOGGER = LogManager.getLogger( AwsClient.class );

    public enum Status {
        CONNECTED, DISCONNECTED
    }

    private AWSIotMqttClient client;

    private Status status;

    private String endpoint;
    private String clientId;
    private SampleUtil.KeyStorePasswordPair pair;

    public AwsClient( String endpoint, String clientId,
                      SampleUtil.KeyStorePasswordPair pair ) {
        this.endpoint = endpoint;
        this.clientId = clientId;
        this.pair = pair;


    }

    public void connect() throws AwsClientException {

        if ( status == CONNECTED ) {
            LOGGER.info( "Client already connected. Exiting" );
            return;
        }
        try {
            client = new AWSIotMqttClient( endpoint, clientId,
                    pair.keyStore, pair.keyPassword );
            client.connect();
            status = CONNECTED;
        }
        catch ( Exception e ) {
            throw new AwsClientException( "AWS cLient error", e );
        }
    }

    public void subscribe( AWSIotTopic topic ) throws AwsClientException {
        if ( status == DISCONNECTED ) {
            LOGGER.info( "Client disconnected. Exiting" );
            return;
        }

        try {
            client.subscribe( topic );
        }
        catch ( Exception e ) {
            throw new AwsClientException( "AWS client error", e );
        }
    }

    public void connectAndSubscribe( AWSIotTopic topic ) throws AwsClientException {
        connect();
        try {
            client.subscribe( topic );
        }
        catch ( Exception e ) {
            throw new AwsClientException( "AWS client error", e );
        }
    }

    public void publish( AWSIotMessage message, Long timeout ) throws AwsClientException {
        if ( status == DISCONNECTED ) {
            LOGGER.info( "Client disconnected. Exiting" );
            return;
        }

        try {
            client.publish( message, timeout );
        }
        catch ( Exception e ) {
            throw new AwsClientException( "AWS client error", e );
        }
    }

    public void disconnect() throws AwsClientException {

        if ( status == DISCONNECTED ) {
            LOGGER.info( "Client already disconnected. Exiting" );
            return;
        }

        try {
            client.disconnect();
            client = null;
            status = DISCONNECTED;
        }
        catch ( Exception e ) {
            throw new AwsClientException( "AWS client error", e );
        }
    }
}
