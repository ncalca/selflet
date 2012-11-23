package it.polimi.elet.selflet.events.service;

import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.SelfletEvent;
import it.polimi.elet.selflet.service.RunningService;

/**
 * 
 * This event represents a generic need to execute a service and it is typically
 * catched by autonomic manager which decides how to execute the service
 * request.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * @author silvia
 */
public class ServiceNeededEvent extends SelfletEvent {

	private final String serviceName;
	private final String resultDestination;
	private final RunningService callingService;

	/**
	 * @param serviceName
	 *            the service name
	 * @param resultDestination
	 *            knowledge property that will contain the result
	 * @param callingService
	 *            the service that made this request
	 * */
	public ServiceNeededEvent(String serviceName, String resultDestination, RunningService callingService) {
		this.serviceName = serviceName;
		this.resultDestination = resultDestination;
		this.callingService = callingService;
	}

	public EventTypeEnum getEventType() {
		return EventTypeEnum.SERVICE_NEEDED;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getResultDestination() {
		return resultDestination;
	}

	public RunningService getCallingService() {
		return callingService;
	}

	@Override
	public String toString() {
		return "Needed service " + serviceName + " called from " + callingService.getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((callingService == null) ? 0 : callingService.hashCode());
		result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
		result = prime * result + ((resultDestination == null) ? 0 : resultDestination.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ServiceNeededEvent)) {
			return false;
		}
		ServiceNeededEvent other = (ServiceNeededEvent) obj;
		if (callingService == null) {
			if (other.callingService != null) {
				return false;
			}
		} else if (!callingService.equals(other.callingService)) {
			return false;
		}
		if (serviceName == null) {
			if (other.serviceName != null) {
				return false;
			}
		} else if (!serviceName.equals(other.serviceName)) {
			return false;
		}
		if (resultDestination == null) {
			if (other.resultDestination != null) {
				return false;
			}
		} else if (!resultDestination.equals(other.resultDestination)) {
			return false;
		}
		return true;
	}

}
