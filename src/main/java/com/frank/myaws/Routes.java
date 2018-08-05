package com.frank.myaws;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.PatternsCS;
import akka.util.Timeout;
import com.frank.myaws.actors.client.ClientActionHandlerMessages.ActionPerformed;
import com.frank.myaws.actors.client.ClientActionHandlerMessages.Connect;
import com.frank.myaws.actors.client.ClientActionHandlerMessages.Disconnect;
import com.frank.myaws.actors.client.ClientActionHandlerMessages.Reconnect;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static akka.http.javadsl.model.ContentTypes.TEXT_HTML_UTF8;
import static akka.http.javadsl.model.StatusCodes.OK;

/**
 * @author ftorriani
 */
public class Routes extends AllDirectives {

    private final Timeout timeout = new Timeout( Duration.create( 20, TimeUnit.SECONDS ) );

    private final ActorRef webActor;
    private final LoggingAdapter log;

    private String home;

    public Routes( ActorSystem system, ActorRef webActor ) {
        this.webActor = webActor;
        log = Logging.getLogger( system, this );

        try {
            URL url = Resources.getResource( "home.html" );
            home = Resources.toString( url, Charsets.UTF_8 );
        }
        catch ( IOException e ) {
            // Paranoia
        }
    }

    public Route createRoutes() {
        return route(
                get( () ->
                        path( "home", () -> complete( HttpEntities.create( TEXT_HTML_UTF8,
                                home.replaceAll( "%MESSAGE%", "" ) ) ) ) ),
                post( () ->
                        path( "reconnect", () -> {
                            CompletionStage<ActionPerformed> action = PatternsCS
                                    .ask( webActor, new Reconnect(), timeout )
                                    .thenApply( obj -> (ActionPerformed) obj );
                            return onSuccess( () -> action,
                                    performed -> {
                                        log.info( "Action {}", performed.getDescription() );
                                        return complete( HttpEntities.create( TEXT_HTML_UTF8,
                                                home.replaceAll( "%MESSAGE%", "Reconnect requested" ) ) );
                                    } );
                        } ) )
//                ,
//                post( () ->
//                        path( "disconnect", () -> {
//                            CompletionStage<ActionPerformed> action = PatternsCS
//                                    .ask( webActor, new Disconnect(), timeout )
//                                    .thenApply( obj -> (ActionPerformed) obj );
//                            return onSuccess( () -> action,
//                                    performed -> {
//                                        log.info( "Action {}", performed.getDescription() );
//                                        return complete( OK, performed, Jackson.marshaller() );
//                                    } );
//                        } ) ),
//                post( () ->
//                        path( "connect", () -> {
//                            CompletionStage<ActionPerformed> action = PatternsCS
//                                    .ask( webActor, new Connect(), timeout )
//                                    .thenApply( obj -> (ActionPerformed) obj );
//                            return onSuccess( () -> action,
//                                    performed -> {
//                                        log.info( "Action {}", performed.getDescription() );
//                                        return complete( OK, performed, Jackson.marshaller() );
//                                    } );
//                        } ) )
        );
    }
}
