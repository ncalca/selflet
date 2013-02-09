package it.polimi.elet.selflet.service;

import static it.polimi.elet.selflet.events.EventTypeEnum.LOCAL_REQUEST_LOCAL_EXECUTION_COMPLETED;
import static it.polimi.elet.selflet.events.EventTypeEnum.LOCAL_REQUEST_LOCAL_EXECUTION_EXECUTE;
import static it.polimi.elet.selflet.events.EventTypeEnum.LOCAL_REQUEST_REMOTE_EXECUTION_COMPLETED;
import static it.polimi.elet.selflet.events.EventTypeEnum.LOCAL_REQUEST_REMOTE_EXECUTION_EXECUTE;
import static it.polimi.elet.selflet.events.EventTypeEnum.REMOTE_REQUEST_LOCAL_EXECUTION_COMPLETED;
import static it.polimi.elet.selflet.events.EventTypeEnum.REMOTE_REQUEST_LOCAL_EXECUTION_EXECUTE;
import it.polimi.elet.selflet.configuration.SelfletConfiguration;
import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.ISelfletEvent;
import it.polimi.elet.selflet.events.SelfletComponent;
import it.polimi.elet.selflet.events.service.IServiceEvent;
import it.polimi.elet.selflet.service.serviceEventHandlers.IServiceEventHandler;
import it.polimi.elet.selflet.service.serviceEventHandlers.IServiceEventHandlerFactory;
import it.polimi.elet.selflet.utilities.PriorityThreadFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

import polimi.reds.MessageID;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This class is in charge of handling service requests and executing them
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class RunningServiceManager extends SelfletComponent implements IRunningServiceManager {

	private static final Logger LOG = Logger.getLogger(RunningServiceManager.class);

	private static final int MAX_SERVICE_EXECUTION_TIME_IN_MSEC = SelfletConfiguration.getSingleton().maxServiceExecutionTimeInSec * 1000;
	private static final int SERVICE_THREAD_POOL_SIZE = SelfletConfiguration.getSingleton().serviceThreadPoolSize;
	private static final int SERVICE_THREAD_POOL_PRIORITY = Thread.MAX_PRIORITY;

	private static final ThreadPoolExecutor MAIN_SERVICE_THREAD_POOL = (ThreadPoolExecutor) Executors
			.newFixedThreadPool(SERVICE_THREAD_POOL_SIZE, new PriorityThreadFactory(SERVICE_THREAD_POOL_PRIORITY,
					"Main Service Thread"));

	private static final ThreadPoolExecutor CHILD_SERVICE_THREAD_POOL = (ThreadPoolExecutor) Executors
			.newFixedThreadPool(Integer.MAX_VALUE, new PriorityThreadFactory(SERVICE_THREAD_POOL_PRIORITY,
					"Child Service Thread"));

	private final IServiceEventHandlerFactory serviceEventHandlerFactory;
	private final Map<EventTypeEnum, IServiceEventHandler> serviceEventHandlers;
	private final Map<MessageID, RunningService> pendingServices;
	private final List<RunningService> activeServices;

	@Inject
	public RunningServiceManager(IServiceEventHandlerFactory serviceEventHandlerFactory) {
		this.serviceEventHandlers = Maps.newHashMap();
		this.pendingServices = Maps.newConcurrentMap();
		this.serviceEventHandlerFactory = serviceEventHandlerFactory;
		this.activeServices = Lists.newCopyOnWriteArrayList();
		initEventHandlers();
		MAIN_SERVICE_THREAD_POOL.prestartAllCoreThreads();
	}

	private void initEventHandlers() {

		ImmutableSet<EventTypeEnum> serviceEvents = ImmutableSet.of(LOCAL_REQUEST_LOCAL_EXECUTION_EXECUTE,
				REMOTE_REQUEST_LOCAL_EXECUTION_EXECUTE, LOCAL_REQUEST_REMOTE_EXECUTION_EXECUTE,
				LOCAL_REQUEST_LOCAL_EXECUTION_COMPLETED, REMOTE_REQUEST_LOCAL_EXECUTION_COMPLETED,
				LOCAL_REQUEST_REMOTE_EXECUTION_COMPLETED);

		for (EventTypeEnum eventType : serviceEvents) {
			serviceEventHandlers.put(eventType, serviceEventHandlerFactory.create(eventType));
		}
	}

	public void eventReceived(ISelfletEvent event) {

		LOG.debug("Event received: " + event);
		EventTypeEnum eventType = event.getEventType();

		if (!serviceEventHandlers.containsKey(eventType)) {
			throw new IllegalArgumentException("There is not service event handler associated with event " + eventType);
		}

		IServiceEventHandler serviceEventHandler = serviceEventHandlers.get(eventType);
		serviceEventHandler.handleEvent((IServiceEvent) event);
	}

	@Override
	public void addServiceWaitingForRemoteReply(RunningService callingService, MessageID messageID) {
		LOG.debug("Suspending service " + callingService + ", waiting for message " + messageID);
		pendingServices.put(messageID, callingService);
	}

	@Override
	public void resumePendingService(MessageID messageID) {
		if (pendingServices.containsKey(messageID)) {
			RunningService serviceToResume = pendingServices.get(messageID);
			serviceToResume.resumeService();
			pendingServices.remove(messageID);
		} else {
			LOG.error("Trying to wake up a non existent running service");
		}
	}

	@Override
	public void startService(RunningService newService) {
		activeServices.add(newService);
		if (newService.isChildService()) {
			CHILD_SERVICE_THREAD_POOL.execute(newService);
		} else {
			MAIN_SERVICE_THREAD_POOL.execute(newService);
		}

	}

	public void cleanOldRequests() {
		int removed = 0;
		for (RunningService runningService : activeServices) {
			if (runningService.getServiceLifeTimeInMillis() > MAX_SERVICE_EXECUTION_TIME_IN_MSEC) {
				removeRunningService(runningService);
				removed++;
			}
		}
		if (removed > 0) {
			LOG.warn("Removed " + removed + " requests due to timeout");
		}
	}

	private void removeRunningService(RunningService runningServiceToRemove) {
		activeServices.remove(runningServiceToRemove);
		if (runningServiceToRemove.isChildService()) {
			CHILD_SERVICE_THREAD_POOL.remove(runningServiceToRemove);
		} else {
			MAIN_SERVICE_THREAD_POOL.remove(runningServiceToRemove);
		}
	}

	@Override
	public ImmutableSet<EventTypeEnum> getReceivedEvents() {
		return ImmutableSet.of(LOCAL_REQUEST_LOCAL_EXECUTION_EXECUTE, REMOTE_REQUEST_LOCAL_EXECUTION_EXECUTE,
				LOCAL_REQUEST_REMOTE_EXECUTION_EXECUTE, LOCAL_REQUEST_LOCAL_EXECUTION_COMPLETED,
				REMOTE_REQUEST_LOCAL_EXECUTION_COMPLETED, LOCAL_REQUEST_REMOTE_EXECUTION_COMPLETED);
	}

	@Override
	public ServiceExecutionStats getServiceExecutionStats() {
		ServiceExecutionStats serviceExecutionStats = new ServiceExecutionStats();

		int activeCount = MAIN_SERVICE_THREAD_POOL.getActiveCount() + CHILD_SERVICE_THREAD_POOL.getActiveCount();

		LOG.debug("Active count: MAIN_SERVICE_THREAD_POOL: " + MAIN_SERVICE_THREAD_POOL.getActiveCount()
				+ " CHILD_SERVICE_THREAD_POOL: " + CHILD_SERVICE_THREAD_POOL.getActiveCount());
		serviceExecutionStats.setActiveCount(activeCount);

		long completed = MAIN_SERVICE_THREAD_POOL.getCompletedTaskCount()
				+ CHILD_SERVICE_THREAD_POOL.getCompletedTaskCount();
		serviceExecutionStats.setCompletedTaskCount(completed);
		serviceExecutionStats.setQueueLength(MAIN_SERVICE_THREAD_POOL.getQueue().size()
				+ MAIN_SERVICE_THREAD_POOL.getQueue().size());
		return serviceExecutionStats;
	}

}