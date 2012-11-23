package it.polimi.elet.selflet.optimization.actions.redirect;

import it.polimi.elet.selflet.service.Service;
import it.polimi.elet.selflet.service.utilization.IPerformanceMonitor;
import it.polimi.elet.selflet.utilities.MathUtil;

import java.util.Set;

import com.google.inject.Inject;

/**
 * An implementation of redirect calculator factory
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class RedirectCalculatorFactory implements IRedirectCalculatorFactory {

	private final IPerformanceMonitor performanceMonitor;

	@Inject
	public RedirectCalculatorFactory(IPerformanceMonitor performanceMonitor) {
		this.performanceMonitor = performanceMonitor;
	}

	@Override
	public IRedirectCalculator create(Service service, Set<RedirectPolicy> redirectPolicies) {
		double redirectProbability = createRedirectProbability(service);
		return new RedirectCalculator(service, redirectPolicies, redirectProbability);
	}

	private double createRedirectProbability(Service service) {
		double serviceResponseTimeInMsec = performanceMonitor.getServiceResponseTimeInMsec(service.getName());
		double maxResponseTimInMsec = service.getMaxResponseTimeInMsec();

		if (serviceResponseTimeInMsec <= maxResponseTimInMsec) {
			// ok no need to redirect
			return 0;
		}

		double ratio = 1 - (maxResponseTimInMsec / serviceResponseTimeInMsec);
		return MathUtil.limitNumber(ratio, 0, 1);
	}
}
