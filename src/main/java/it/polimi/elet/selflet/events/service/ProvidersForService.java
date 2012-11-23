package it.polimi.elet.selflet.events.service;

import static it.polimi.elet.selflet.utilities.CollectionUtils.randomElement;
import it.polimi.elet.selflet.exceptions.InvalidValueException;
import it.polimi.elet.selflet.exceptions.NotFoundException;
import it.polimi.elet.selflet.id.ISelfLetID;
import it.polimi.elet.selflet.negotiation.ServiceAskModeEnum;
import it.polimi.elet.selflet.negotiation.ServiceOfferModeEnum;
import it.polimi.elet.selflet.negotiation.ServiceProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

/**
 * Manages all the aspects related to service providers of a service
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ProvidersForService {

	private final String serviceName;

	private final Set<ISelfLetID> canDoProviders = new HashSet<ISelfLetID>();
	private final Set<ISelfLetID> canTeachProviders = new HashSet<ISelfLetID>();
	private final Set<ISelfLetID> knowDoProviders = new HashSet<ISelfLetID>();
	private final Set<ISelfLetID> knowTeachProviders = new HashSet<ISelfLetID>();

	private boolean canBoth = false;
	private boolean canDo = false;
	private boolean canTeach = false;
	private boolean knowsBoth = false;
	private boolean knowsDo = false;
	private boolean knowsTeach = false;

	public ProvidersForService(String serviceName) {
		this.serviceName = serviceName;
	}

	public void addProvider(ServiceProvider provider) {
		ISelfLetID selfletID = provider.getProviderID();
		ServiceOfferModeEnum mode = provider.getOfferMode();

		switch (mode) {

		case Both:
			canDoProviders.add(selfletID);
			canTeachProviders.add(selfletID);
			setLocalOfferMode(ServiceOfferModeEnum.KnowsWhoCanBoth, true);
			break;
		case CanDo:
			canDoProviders.add(selfletID);
			setLocalOfferMode(ServiceOfferModeEnum.KnowsWhoCanDo, true);
			break;
		case CanTeach:
			canTeachProviders.add(selfletID);
			setLocalOfferMode(ServiceOfferModeEnum.KnowsWhoCanTeach, true);
			break;
		case KnowsWhoCanBoth:
			knowDoProviders.add(selfletID);
			knowTeachProviders.add(selfletID);
			// Don't set any local offer mode in this case
			break;
		case KnowsWhoCanDo:
			knowDoProviders.add(selfletID);
			// Don't set any local offer mode in this case
			break;
		case KnowsWhoCanTeach:
			knowTeachProviders.add(selfletID);
			// Don't set any local offer mode in this case
			break;
		case None:
			// A bit unorthodox, but it makes sense
			removeProvider(selfletID);
		}
	}

	public void removeProvider(ISelfLetID name) {

		canDoProviders.remove(name);
		canTeachProviders.remove(name);

		knowDoProviders.remove(name);
		knowTeachProviders.remove(name);

		if (canDoProviders.isEmpty()) {
			setLocalOfferMode(ServiceOfferModeEnum.KnowsWhoCanDo, false);
		}

		if (canTeachProviders.isEmpty()) {
			setLocalOfferMode(ServiceOfferModeEnum.KnowsWhoCanTeach, false);
		}

	}

	public void setLocalOfferMode(ServiceOfferModeEnum mode, boolean isOffered) {

		switch (mode) {
		case Both:
			canDo = isOffered;
			canTeach = isOffered;
			canBoth = isOffered;
		case CanDo:
			canDo = isOffered;
			canBoth = (canDo && canTeach);
			break;
		case CanTeach:
			canTeach = isOffered;
			canBoth = (canDo && canTeach);
			break;
		case KnowsWhoCanBoth:
			knowsDo = isOffered;
			knowsTeach = isOffered;
			knowsBoth = isOffered;
		case KnowsWhoCanDo:
			knowsDo = isOffered;
			knowsBoth = (knowsDo && knowsTeach);
			break;
		case KnowsWhoCanTeach:
			knowsTeach = isOffered;
			knowsBoth = (knowsDo && knowsTeach);
			break;
		case None:
			if (isOffered) {
				canDo = false;
				canTeach = false;
				canBoth = false;
				knowsDo = false;
				knowsTeach = false;
				knowsBoth = false;
			} else {
				throw new InvalidValueException("Can' set the mode None to false");
			}
		}

	}

	public boolean hasLocalOfferMode(ServiceOfferModeEnum mode) {
		switch (mode) {

		case Both:
			return canBoth;
		case CanDo:
			return canDo;

		case CanTeach:
			return canTeach;

		case KnowsWhoCanBoth:
			return knowsBoth;

		case KnowsWhoCanDo:
			return knowsDo;

		case KnowsWhoCanTeach:
			return knowsTeach;

		case None:
			return (!(canBoth || canDo || canTeach || knowsBoth || knowsDo || knowsTeach));

		default:
			return false;
		}
	}

	private ServiceProvider getTeachProvider() {

		if (!canTeachProviders.isEmpty()) {
			return new ServiceProvider(randomElement(canTeachProviders), ServiceOfferModeEnum.CanTeach);

		} else if (!knowTeachProviders.isEmpty()) {
			return new ServiceProvider(randomElement(knowTeachProviders), ServiceOfferModeEnum.KnowsWhoCanTeach);
		}

		throw new NotFoundException("No provider has been found for service " + serviceName);

	}

	private ServiceProvider getDoProvider() {

		if (!canDoProviders.isEmpty()) {
			return new ServiceProvider(randomElement(canDoProviders), ServiceOfferModeEnum.CanDo);

		} else if (!knowDoProviders.isEmpty()) {
			return new ServiceProvider(randomElement(knowDoProviders), ServiceOfferModeEnum.KnowsWhoCanDo);
		}

		throw new NotFoundException("No provider has been found for service " + serviceName);

	}

	private ServiceProvider getAnyProvider() {

		// By default, prefer Do over Teach. No specific reason,
		// this is likely to change in the future with some kind
		// of metrics.
		if (!canDoProviders.isEmpty()) {
			return new ServiceProvider(randomElement(canDoProviders), ServiceOfferModeEnum.CanDo);

		} else if (!canTeachProviders.isEmpty()) {
			return new ServiceProvider(randomElement(canTeachProviders), ServiceOfferModeEnum.CanTeach);

		} else if (!knowDoProviders.isEmpty()) {
			return new ServiceProvider(randomElement(knowDoProviders), ServiceOfferModeEnum.KnowsWhoCanDo);

		} else if (!knowTeachProviders.isEmpty()) {
			return new ServiceProvider(randomElement(knowTeachProviders), ServiceOfferModeEnum.KnowsWhoCanTeach);

		}

		throw new NotFoundException("No provider has been found for service " + serviceName);

	}

	public ServiceProvider getPreferredProvider(ServiceAskModeEnum mode) {
		switch (mode) {

		case Any:
			return getAnyProvider();

		case Do:
			return getDoProvider();

		case Teach:
			return getTeachProvider();

		default:
			throw new NotFoundException("No provider has been found for service " + serviceName);
		}
	}

	public List<Object> getAllProviders() {
		List<Object> list = Lists.newArrayList();
		list.addAll(Lists.newArrayList(canDoProviders));
		list.addAll(Lists.newArrayList(canTeachProviders));
		list.addAll(Lists.newArrayList(knowDoProviders));
		list.add(Lists.newArrayList(knowTeachProviders));
		return list;
	}

	public List<ServiceOfferModeEnum> getOfferModes() {
		List<ServiceOfferModeEnum> modes = new ArrayList<ServiceOfferModeEnum>();

		for (ServiceOfferModeEnum mode : ServiceOfferModeEnum.values()) {
			if (hasLocalOfferMode(mode)) {
				modes.add(mode);
			}
		}

		if (modes.isEmpty()) {
			modes.add(ServiceOfferModeEnum.None);
		}
		return modes;
	}

}
