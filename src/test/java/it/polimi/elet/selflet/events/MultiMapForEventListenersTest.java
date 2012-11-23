package it.polimi.elet.selflet.events;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.IEventListener;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Simple test just to understand how the HashMultimap works
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class MultiMapForEventListenersTest {

	@Test
	public void testDoubleInsertion() {
		Multimap<EventTypeEnum, IEventListener> listenerMapping = HashMultimap.create();
		IEventListener eventListener1 = mock(IEventListener.class);
		listenerMapping.put(EventTypeEnum.TEST_EVENT, eventListener1);
		listenerMapping.put(EventTypeEnum.TEST_EVENT, eventListener1);
		assertEquals(1, listenerMapping.get(EventTypeEnum.TEST_EVENT).size());
	}

	@Test
	public void emptyMultiMap() {
		Multimap<EventTypeEnum, IEventListener> listenerMapping = HashMultimap.create();
		assertEquals(0, listenerMapping.get(EventTypeEnum.TEST_EVENT).size());
	}

}
