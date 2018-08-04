package com.frank.myaws;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.frank.myaws.action.Location;
import com.frank.myaws.actors.aws.CommandExecutor;
import com.frank.myaws.actors.aws.Publisher;
import com.frank.myaws.aws.FromAwsTopic;
import com.frank.myaws.pi.PiAdapter;
import com.typesafe.config.Config;
import scala.concurrent.duration.FiniteDuration;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author ftorriani
 */
public class App extends AllDirectives {

    private static final Logger LOGGER = LogManager.getLogger( App.class );

    private String type;
    private String clientId;
    private String endpoint;
    private String certificate;
    private String privateKey;

    public static void main( String... args ) throws Exception {

        if ( args.length != 5 ) {
            LOGGER.error( "Params: [type] [clientId] [endpoint] [path_to_certificate] [path_to_private_key]" );
            System.exit( 1 );
        }

        String type = args[ 0 ];
        String clientId = args[ 1 ];
        String endpoint = args[ 2 ];
        String certificate = args[ 3 ];
        String privateKey = args[ 4 ];
//        Integer bcmPin = Integer.parseInt( args[ 5 ] );

        new App( type, clientId, endpoint, certificate, privateKey ).start();

    }

    private App( String type, String clientId, String endpoint,
                 String certificate, String privateKey ) {
        this.type = type;
        this.clientId = clientId;
        this.endpoint = endpoint;
        this.certificate = certificate;
        this.privateKey = privateKey;
    }

    private void start() throws Exception {
        ActorSystem system = ActorSystem.create( "my-aws" );

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

            ActorRef publisher = system.actorOf( Publisher.props( client ), Publisher.name() );

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

            Map<Location, PiAdapter> adapters = transform(
                    system.settings().config().getString( "my-aws.internal.pi-adapter" ),
                    system.settings().config().getConfigList( "my-aws.locations" ) );

            ActorRef actionExecutor = system.actorOf( CommandExecutor.props( adapters ),
                    CommandExecutor.name() );
            FromAwsTopic topic = new FromAwsTopic( fromAwsTopic, AWSIotQos.QOS0, actionExecutor );
            client.subscribe( topic );

            final Http http = Http.get( system );
            final ActorMaterializer materializer = ActorMaterializer.create( system );
        }
    }

    private Route createRoute() {

        return route(
                path( "hello", () ->
                        get( () ->
                                complete( "<h1>Say hello to akka-http</h1>" ) ) ) );
    }

    private Map<Location, PiAdapter> transform( String adapterClassName,
                                                List<? extends Config> configList )
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Objects.requireNonNull( configList );

        Map<Location, PiAdapter> locations = new HashMap<>();
        for ( Config c : configList ) {
            Integer pin = c.getInt( "pin" );
            Location location = Location.valueOf( c.getString( "location" ) );

            PiAdapter adapter = (PiAdapter) Class.forName( adapterClassName ).newInstance();
            adapter.init( location.name(), pin, PiAdapter.PinMode.OUT );

            locations.put( location, adapter );
        }

        return locations;
    }

}
