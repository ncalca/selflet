package it.polimi.elet.selflet.events;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import it.polimi.elet.selflet.utilities.ThreadUtilities;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class EventDispatcherTest {

	private static final int SLEEP_TIME = 10;
	private IEventDispatcher eventDispatcher;

	@Test
	public void testNoListeners() {
		eventDispatcher = new EventDispatcher();
		ThreadUtilities.submitGenericJob(eventDispatcher);

		ISelfletComponent component = createEmptyComponent();

		eventDispatcher.registerSelfLetComponent(component);
		TestEvent testEvent = new TestEvent();
		eventDispatcher.dispatchEvent(testEvent);
		verify(component, never()).eventReceived(testEvent);
	}

	@Test
	public void testEventDelivery() {
		eventDispatcher = new EventDispatcher();
		ThreadUtilities.submitGenericJob(eventDispatcher);

		ISelfletComponent component1 = createEmptyComponent();
		ISelfletComponent component2 = createTestComponent();

		eventDispatcher.registerSelfLetComponent(component1);
		eventDispatcher.registerSelfLetComponent(component2);

		TestEvent testEvent = new TestEvent();
		eventDispatcher.dispatchEvent(testEvent);

		sleep();
		verify(component1, never()).eventReceived(testEvent);
		verify(component2, times(1)).eventReceived(testEvent);
	}

	private void sleep() {
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private ISelfletComponent createTestComponent() {
		ISelfletComponent component = mock(ISelfletComponent.class);
		ImmutableSet<EventTypeEnum> events = ImmutableSet.of(EventTypeEnum.TEST_EVENT);
		when(component.getReceivedEvents()).thenReturn(events);
		return component;
	}

	private ISelfletComponent createEmptyComponent() {
		ISelfletComponent component = mock(ISelfletComponent.class);
		ImmutableSet<EventTypeEnum> events = ImmutableSet.of();
		when(component.getReceivedEvents()).thenReturn(events);
		return component;
	}

}
