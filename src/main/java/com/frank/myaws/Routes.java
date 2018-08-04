package com.frank.myaws;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.PatternsCS;
import akka.util.Timeout;
import com.frank.myaws.actors.web.WebEventHandlerMessages;
import scala.concurrent.duration.Duration;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import akka.http.javadsl.marshallers.jackson.Jackson;

import static akka.http.javadsl.model.StatusCodes.OK;

/**
 * @author ftorriani
 */
public class Routes extends AllDirectives {

    private final Timeout timeout = new Timeout( Duration.create( 5, TimeUnit.SECONDS ) );

    private final ActorRef webActor;
    private final LoggingAdapter log;

    public Routes( ActorSystem system, ActorRef webActor ) {
        this.webActor = webActor;
        log = Logging.getLogger( system, this );
    }

    public Route createRoutes() {
        return route(
                post( () ->
                        path( "reconnect", () -> {
                            CompletionStage<WebEventHandlerMessages.ActionPerformed> action = PatternsCS
                                    .ask( webActor, new WebEventHandlerMessages.Reconnect(), timeout )
                                    .thenApply( obj -> (WebEventHandlerMessages.ActionPerformed) obj );
                            return onSuccess( () -> action,
                                    performed -> {
                                        log.info( "Action {}", performed.getDescription() );
                                        return complete( OK, performed, Jackson.marshaller() );
                                    } );
                        } ) ),
                post( () ->
                        path( "disconnect", () -> {
                            CompletionStage<WebEventHandlerMessages.ActionPerformed> action = PatternsCS
                                    .ask( webActor, new WebEventHandlerMessages.Disconnect(), timeout )
                                    .thenApply( obj -> (WebEventHandlerMessages.ActionPerformed) obj );
                            return onSuccess( () -> action,
                                    performed -> {
                                        log.info( "Action {}", performed.getDescription() );
                                        return complete( OK, performed, Jackson.marshaller() );
                                    } );
                        } ) ),
                post( () ->
                        path( "connect", () -> {
                            CompletionStage<WebEventHandlerMessages.ActionPerformed> action = PatternsCS
                                    .ask( webActor, new WebEventHandlerMessages.Connect(), timeout )
                                    .thenApply( obj -> (WebEventHandlerMessages.ActionPerformed) obj );
                            return onSuccess( () -> action,
                                    performed -> {
                                        log.info( "Action {}", performed.getDescription() );
                                        return complete( OK, performed, Jackson.marshaller() );
                                    } );
                        } ) )
        );
    }
}
