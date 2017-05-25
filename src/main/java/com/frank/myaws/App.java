package com.frank.myaws;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.frank.myaws.actors.Listener;
import com.frank.myaws.actors.Publisher;
import com.frank.myaws.aws.FromAwsTopic;
import com.frank.myaws.pi.PiAdapter;
import scala.concurrent.duration.FiniteDuration;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author ftorriani
 */
public class App {

    private static final Logger LOGGER = LogManager.getLogger( App.class );

    private String type;
    private String clientId;
    private String endpoint;
    private String certificate;
    private String privateKey;
    private Integer bcmPin;

    public static void main( String... args ) throws Exception {

        if ( args.length != 6 ) {
            LOGGER.error( "Params: [type] [clientId] [endpoint] [path_to_certificate] [path_to_private_key] [bcm_pin]" );
            System.exit( 1 );
        }

        String type = args[ 0 ];
        String clientId = args[ 1 ];
        String endpoint = args[ 2 ];
        String certificate = args[ 3 ];
        String privateKey = args[ 4 ];
        Integer bcmPin = Integer.parseInt( args[ 5 ] );

        new App( type, clientId, endpoint, certificate, privateKey, bcmPin ).start();

    }

    private App( String type, String clientId, String endpoint,
                 String certificate, String privateKey, Integer bcmPin ) {
        this.type = type;
        this.clientId = clientId;
        this.endpoint = endpoint;
        this.certificate = certificate;
        this.privateKey = privateKey;
        this.bcmPin = bcmPin;
    }

    private void start() throws Exception {
        ActorSystem system = ActorSystem.create();

        SampleUtil.KeyStorePasswordPair pair = SampleUtil.
                getKeyStorePasswordPair( certificate, privateKey );

        LOGGER.info( "Connecting to endpoint '{}'", endpoint );
        LOGGER.info( "Cliend ID is '{}'", clientId );

        AWSIotMqttClient client = new AWSIotMqttClient( endpoint, clientId,
                pair.keyStore, pair.keyPassword );
        client.connect();

        if ( "client".equals( type ) ) {
            LOGGER.info( "Pushing to AWS" );

            String toAwsTopic = system.settings().
                    config().getString( "my-aws.aws-iot.push-send-topic" );

            ActorRef publisher = system.actorOf( Publisher.props( client ), "Publisher" );

            system.scheduler().schedule( FiniteDuration.apply( 5, SECONDS ),
                    FiniteDuration.apply( 5, SECONDS ),
                    publisher, new Publisher.Message( toAwsTopic,
                            "{\"time\" : " + System.nanoTime() + "}" ),
                    system.dispatcher(), ActorRef.noSender() );
        }
        else {

            LOGGER.info( "Listening...." );

            String fromAwsTopic = system.settings().
                    config().getString( "my-aws.aws-iot.push-receive-topic" );
            String adapterClassName = system.settings().
                    config().getString( "my-aws.internal.pi-adapter" );

            PiAdapter adapter = (PiAdapter) Class.forName( adapterClassName ).newInstance();
            adapter.init( bcmPin, PiAdapter.PinMode.OUT );

            ActorRef listener = system.actorOf( Listener.props( adapter ), "Listener" );
            FromAwsTopic topic = new FromAwsTopic( fromAwsTopic, AWSIotQos.QOS0, listener );
            client.subscribe( topic );
        }
    }

}
