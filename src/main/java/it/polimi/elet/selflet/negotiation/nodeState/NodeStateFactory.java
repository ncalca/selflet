package it.polimi.elet.selflet.negotiation.nodeState;

import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.knowledge.Neighbor;
import it.polimi.elet.selflet.negotiation.INeighborManager;
import it.polimi.elet.selflet.service.IRunningServiceManager;
import it.polimi.elet.selflet.service.Service;
import it.polimi.elet.selflet.service.ServiceExecutionStats;
import it.polimi.elet.selflet.service.utilization.IPerformanceMonitor;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static it.polimi.elet.selflet.negotiation.nodeState.NodeStateGenericDataEnum.*;

/**
 * Implementation of node state factory
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
@Singleton
public class NodeStateFactory implements INodeStateFactory {

	private final IServiceKnowledge serviceKnowledge;
	private final ISelfLetID selfletID;
	private final IPerformanceMonitor performanceMonitor;
	private final INeighborManager neighborManager;
	private final IRunningServiceManager runningServiceManager;

	@Inject
	public NodeStateFactory(ISelfLetID selfletID, IServiceKnowledge serviceKnowledge, IPerformanceMonitor performanceMonitor, INeighborManager neighborManager,
			IRunningServiceManager runningServiceManager) {
		this.selfletID = selfletID;
		this.serviceKnowledge = serviceKnowledge;
		this.performanceMonitor = performanceMonitor;
		this.neighborManager = neighborManager;
		this.runningServiceManager = runningServiceManager;
	}

	/**
	 * Return an object representing the current node state
	 * */
	public INodeState createNodeState() {
		INodeState nodeState = new NodeState(selfletID);
		fillAvailableServices(nodeState);
		fillPerformanceData(nodeState);
		fillKnownNeighbors(nodeState);
		fillServiceExecutionData(nodeState);
		return nodeState;
	}

	private void fillServiceExecutionData(INodeState nodeState) {
		ServiceExecutionStats serviceExecutionStats = runningServiceManager.getServiceExecutionStats();
		Map<String, Serializable> serviceExecutionData = Maps.newHashMap();
		serviceExecutionData.put("Active threads", serviceExecutionStats.getActiveCount());
		serviceExecutionData.put("Completed services", serviceExecutionStats.getCompletedTaskCount());
		serviceExecutionData.put("Queue length", serviceExecutionStats.getQueueLength());
		nodeState.addGenericData(serviceExecutionData);
	}

	private void fillAvailableServices(INodeState nodeState) {
		nodeState.addAvailableServices(getAvailableServices());
	}

	private void fillKnownNeighbors(INodeState nodeState) {
		Set<ISelfLetID> knownNeighbors = Sets.newHashSet();
		Set<Neighbor> neighbors = neighborManager.getNeighbors();

		for (Neighbor neighbor : neighbors) {
			knownNeighbors.add(neighbor.getId());
		}

		nodeState.addKnownNeighbors(knownNeighbors);
	}

	private void fillPerformanceData(INodeState nodeState) {
		Map<String, Serializable> performanceData = Maps.newHashMap();

		for (String serviceName : getAvailableServices()) {
			performanceData.put(REQUEST_RATE.toString() + ": " + serviceName, performanceMonitor.getServiceRequestRate(serviceName));
			performanceData.put(THROUGHPUT.toString() + ": " + serviceName, performanceMonitor.getServiceThroughput(serviceName));
			performanceData.put(SERVICE_UTILIZATION.toString() + ": " + serviceName, performanceMonitor.getServiceUtilization(serviceName));
			performanceData.put(RESPONSE_TIME.toString() + ": " + serviceName, new Double(performanceMonitor.getServiceResponseTimeInMsec(serviceName)));
		}

		performanceData.put(CPU_UTILIZATION.toString(), performanceMonitor.getCurrentTotalCPUUtilization());
		nodeState.setUtilization(performanceMonitor.getCurrentTotalCPUUtilization());
		//TODO
		performanceData.put(CPU_UTILIZATION_UPPER_BOUND.toString(), performanceMonitor.getCPUUtilizationUpperBound());
		nodeState.setUtilizationUpperBound(performanceMonitor.getCPUUtilizationUpperBound());
		
		nodeState.addGenericData(performanceData);
	}

	private Set<String> getAvailableServices() {
		Set<String> availableServices = Sets.newHashSet();
		for (Service service : serviceKnowledge.getServices()) {
			if (service.isLocallyAvailable()) {
				availableServices.add(service.getName());
			}
		}
		return availableServices;
	}

}
