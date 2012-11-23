package it.polimi.elet.selflet.service.utilization;

import it.polimi.elet.selflet.configuration.SelfletConfiguration;

import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Class maintaining the response time for service. Response time information
 * expires when it is too old.
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ResponseTimeMonitor {

	private static final int MAX_RESPONSE_TIME_AGE = SelfletConfiguration.getSingleton().max_response_time_age_in_sec;

	private static final String SEPARATOR = "_";

	private final Cache<String, Long> responseTimeCache;
	private long measurementId = 0;

	public ResponseTimeMonitor() {
		responseTimeCache = CacheBuilder.newBuilder().expireAfterWrite(MAX_RESPONSE_TIME_AGE, TimeUnit.SECONDS).build();
	}

	public long getResponseTime(String service) {
		double sum = 0;
		int count = 0;

		for (Entry<String, Long> entry : responseTimeCache.asMap().entrySet()) {
			if (entry.getKey().contains(service + SEPARATOR)) {
				sum += entry.getValue();
				count++;
			}
		}

		if (count == 0) {
			return 0;
		}

		return (long) (sum / count);
	}

	public void updateResponseTime(String serviceName, long responseTime) {
		String measurementName = serviceName + SEPARATOR + (measurementId++);
		responseTimeCache.put(measurementName, responseTime);
	}

}
