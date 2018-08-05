package com.frank.myaws.actors.aws;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.CallingThreadDispatcher;
import akka.testkit.TestActorRef;
import akka.testkit.TestKit;
import com.frank.myaws.action.Command;
import com.frank.myaws.action.Location;
import com.frank.myaws.pi.PiAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.Map;

import static com.frank.myaws.action.Location.*;
import static com.frank.myaws.action.Action.TOGGLE_LIGHT;
import static org.mockito.Mockito.*;

/**
 * @author ftorriani
 */
public class ActionExecutorTest {

    private ActorSystem system;

    @Before
    public void setUp() {

        system = ActorSystem.create();
    }

    @After
    public void tearDown() {

        TestKit.shutdownActorSystem( system,
                Duration.create("5 seconds"), true );
    }

    @Test
    public void adapterIsCalledIfLocationIsValid() throws InterruptedException {

        Map<Location, PiAdapter> map = new HashMap<>();

        PiAdapter bedroomAdapter = mock(PiAdapter.class);
        PiAdapter kitchenAdapter = mock(PiAdapter.class);
        map.put( BEDROOM, bedroomAdapter );
        map.put( KITCHEN, kitchenAdapter );

        TestActorRef<CommandExecutor> listener =
                TestActorRef.create( system, CommandExecutor.props( map ).
                        withDispatcher( CallingThreadDispatcher.Id() ) );

        listener.tell( new Command( TOGGLE_LIGHT, BEDROOM ),
                ActorRef.noSender() );

        Thread.sleep( 3000 );

        verify( bedroomAdapter ).toggle();
        verifyZeroInteractions( kitchenAdapter );
    }

    @Test
    public void noAdapterIsCalledIfLocationNotRegistered() throws InterruptedException {

        Map<Location, PiAdapter> map = new HashMap<>();

        PiAdapter bedroomAdapter = mock(PiAdapter.class);
        PiAdapter kitchenAdapter = mock(PiAdapter.class);
        map.put( BEDROOM, bedroomAdapter );
        map.put( KITCHEN, kitchenAdapter );

        TestActorRef<CommandExecutor> listener =
                TestActorRef.create( system, CommandExecutor.props( map ).
                        withDispatcher( CallingThreadDispatcher.Id() ) );

        listener.tell( new Command( TOGGLE_LIGHT, BATHROOM1 ),
                ActorRef.noSender() );

        Thread.sleep( 3000 );

        verifyZeroInteractions( bedroomAdapter, kitchenAdapter );
    }
}