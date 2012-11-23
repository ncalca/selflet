package it.polimi.elet.selflet.optimization;

import java.util.Set;

import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.id.SelfLetID;
import it.polimi.elet.selflet.optimization.actions.redirect.RedirectCalculator;
import it.polimi.elet.selflet.optimization.actions.redirect.RedirectPolicy;
import it.polimi.elet.selflet.service.Service;

import org.junit.Test;

import com.google.common.collect.Sets;

import static org.mockito.Mockito.*;
import static junit.framework.Assert.*;

public class RedirectCalculatorTest {

	private static final int MAX_ITERATIONS = 1000;
	private static final double DELTA = 0.05;

	@Test
	public void testEmptySetOfRedirectPolicies() {
		Service service = mock(Service.class);
		double probability = 0;
		Set<RedirectPolicy> emptySet = Sets.newHashSet();
		RedirectCalculator redirectCalculator = new RedirectCalculator(service, emptySet, probability);
		assertFalse(redirectCalculator.performRedirect());
	}

	@Test
	public void testSingleRedirectPolicy() {
		Service service = new Service("service1");
		Set<RedirectPolicy> emptySet = Sets.newHashSet();
		ISelfLetID providerOfService = new SelfLetID(10);
		emptySet.add(createNewRedirectPolicy("service1", 0.5, providerOfService));
		for (int i = 0; i < MAX_ITERATIONS; i++) {
			RedirectCalculator redirectCalculator = new RedirectCalculator(service, emptySet, 0.3);

			if (redirectCalculator.performRedirect()) {
				ISelfLetID extractedProvider = redirectCalculator.getRedirectedProvider();
				assertEquals(providerOfService, extractedProvider);
			}

		}
	}

	@Test
	public void testPercentagesOfRedirects() {
		String serviceName = "service1";
		RedirectPolicy redirectPolicy = createNewRedirectPolicy(serviceName, 0.2);
		Service redirectedService = new Service(serviceName);

		Set<RedirectPolicy> redirectPolicies = Sets.newHashSet(redirectPolicy);
		double probabilityToPerformARedirect = 0.5;

		int numberOfRedirects = 0;
		for (int i = 0; i < MAX_ITERATIONS; i++) {
			RedirectCalculator redirectCalculator = new RedirectCalculator(redirectedService, redirectPolicies, probabilityToPerformARedirect);
			if (redirectCalculator.performRedirect()) {
				numberOfRedirects++;
			}
		}

		double percentageOfRedirects = Double.valueOf(numberOfRedirects) / MAX_ITERATIONS;
		assertEquals(probabilityToPerformARedirect, percentageOfRedirects, DELTA);
	}

	@Test
	public void testPercentagesOfRedirectsForProviders() {
		String serviceName = "service1";
		ISelfLetID provider1 = new SelfLetID(1);
		ISelfLetID provider2 = new SelfLetID(2);
		double redirectProbabilityTo1 = 0.3;
		double redirectProbabilityTo2 = 0.7;

		RedirectPolicy redirectPolicy1 = createNewRedirectPolicy(serviceName, redirectProbabilityTo1, provider1);
		RedirectPolicy redirectPolicy2 = createNewRedirectPolicy(serviceName, redirectProbabilityTo2, provider2);

		Service redirectedService = new Service(serviceName);

		Set<RedirectPolicy> redirectPolicies = Sets.newHashSet(redirectPolicy1, redirectPolicy2);
		double redirectProbability = 0.4;

		int numberOfRedirectsToProvider1 = 0;
		int numberOfRedirectsToProvider2 = 0;
		int totalRedirects = 0;

		for (int i = 0; i < MAX_ITERATIONS; i++) {
			RedirectCalculator redirectCalculator = new RedirectCalculator(redirectedService, redirectPolicies, redirectProbability);

			if (redirectCalculator.performRedirect()) {
				totalRedirects++;

				if (redirectCalculator.getRedirectedProvider().getID().equals(1)) {
					numberOfRedirectsToProvider1++;
				}

				if (redirectCalculator.getRedirectedProvider().getID().equals(2)) {
					numberOfRedirectsToProvider2++;
				}

			}
		}

		double percentageOfRedirects1 = Double.valueOf(numberOfRedirectsToProvider1) / totalRedirects;
		double percentageOfRedirects2 = Double.valueOf(numberOfRedirectsToProvider2) / totalRedirects;

		assertEquals(redirectProbabilityTo1, percentageOfRedirects1, DELTA);
		assertEquals(redirectProbabilityTo2, percentageOfRedirects2, DELTA);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidProbabilityHigher() {
		Service service = mock(Service.class);
		RedirectPolicy redirectPolicy = mock(RedirectPolicy.class);

		Set<RedirectPolicy> redirectPolicies = Sets.newHashSet(redirectPolicy);
		new RedirectCalculator(service, redirectPolicies, 1.1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidProbabilityLower() {
		Service service = mock(Service.class);
		RedirectPolicy redirectPolicy = mock(RedirectPolicy.class);

		Set<RedirectPolicy> redirectPolicies = Sets.newHashSet(redirectPolicy);
		new RedirectCalculator(service, redirectPolicies, -0.1);
	}

	private RedirectPolicy createNewRedirectPolicy(String serviceName, double probability) {
		ISelfLetID selfletID = mock(ISelfLetID.class);
		return createNewRedirectPolicy(serviceName, probability, selfletID);
	}

	private RedirectPolicy createNewRedirectPolicy(String serviceName, double probability, ISelfLetID receivingSelfletID) {
		return new RedirectPolicy(serviceName, receivingSelfletID, probability);
	}

}
