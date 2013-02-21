package it.polimi.elet.selflet.autonomic;

import it.polimi.elet.selflet.ability.IAbilityExecutionEnvironment;
import it.polimi.elet.selflet.events.EventTypeEnum;
import it.polimi.elet.selflet.events.ISelfletEvent;
import it.polimi.elet.selflet.events.SelfletComponent;
import it.polimi.elet.selflet.events.drools.RuleAddedEvent;
import it.polimi.elet.selflet.events.drools.RuleFiredEvent;
import it.polimi.elet.selflet.events.drools.RuleRemovedEvent;
import it.polimi.elet.selflet.exceptions.AutonomicManagerException;
import it.polimi.elet.selflet.exceptions.NotImplementedExeception;
import it.polimi.elet.selflet.knowledge.IGeneralKnowledge;
import it.polimi.elet.selflet.knowledge.IKnowledgesContainer;
import it.polimi.elet.selflet.knowledge.IServiceKnowledge;
import it.polimi.elet.selflet.negotiation.INegotiationManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.log4j.Logger;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.DefaultAgendaEventListener;
import org.drools.rule.Package;
import org.drools.rule.Rule;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author silvia
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 */
@Singleton
public class AutonomicManagerDrools extends SelfletComponent implements IAutonomicManager {

	private static final Logger LOG = Logger.getLogger(AutonomicManagerDrools.class);

	private final IAutonomicActuator autonomicAttuator;
	private final INegotiationManager negotiationManager;
	private final IServiceKnowledge serviceKnowledge;
	private final IGeneralKnowledge generalKnowledge;

	// TODO change to list of paths
	private String ruleFilePath;

	private boolean running = false;
	private RuleBase ruleBase;
	private WorkingMemory workingMemory;

	@Inject
	public AutonomicManagerDrools(INegotiationManager negotiationManager,
			IAbilityExecutionEnvironment abilityExecutionEnvironment, IKnowledgesContainer knowledges,
			IAutonomicActuator autonomicAttuator) {
		this.negotiationManager = negotiationManager;
		this.serviceKnowledge = knowledges.getServiceKnowledge();
		this.generalKnowledge = knowledges.getGeneralKnowledge();
		this.autonomicAttuator = autonomicAttuator;
	}

	@Override
	public void addRuleFile(String ruleFilePath) {
		this.ruleFilePath = ruleFilePath;
	}

	public void addRuleFiles(List<String> rules) {
		// TODO make it generic so that it accepts multiple rules

		if (rules.isEmpty()) {
			LOG.debug("No rules found");
		} else {
			String rule = rules.get(0);
			LOG.debug("Adding only one rule: " + rule);
			addRuleFile(rule);
		}
	}

	@Override
	public List<String> getRuleFiles() {
		List<String> rules = Lists.newArrayList();
		rules.add(ruleFilePath);
		return rules;
	}

	public void start() {

		ruleBase = RuleBaseFactory.newRuleBase();

		if (ruleFilePath == null || ruleFilePath.length() == 0) {
			LOG.debug("Empty rule");
			return;
		}

		Exception ex = null;
		PackageBuilder builder = new PackageBuilder();
		try {
			Reader reader = new InputStreamReader(new FileInputStream(ruleFilePath));
			builder.addPackageFromDrl(reader);

			ruleBase.addPackage(builder.getPackage());

			workingMemory = ruleBase.newStatefulSession();

			addGlobalsToWorkingMemory();

			workingMemory.addEventListener(new DefaultAgendaEventListener() {
				@Override
				public void afterActivationFired(final AfterActivationFiredEvent event, final WorkingMemory workMem) {
					fireRuleFiredEvent(event.getActivation().getRule().getName());
				}
			});

		} catch (DroolsParserException e) {
			ex = e;
			LOG.error("Drools parsing exception", e);
		} catch (FileNotFoundException e) {
			ex = e;
			LOG.error("Rule file not found", e);
		} catch (Exception e) {
			ex = e;
			LOG.error("Generic error while loading rule file", e);
		} finally {
			if (ex != null) {
				// removeRule(ruleFilePath);
				throw new AutonomicManagerException("Autonomic Manager: exception while loading rules", ex);
			}
		}

		running = true;
	}

	private void addGlobalsToWorkingMemory() {
		workingMemory.setGlobal("negotiationManager", negotiationManager);
		workingMemory.setGlobal("autonomicActuator", autonomicAttuator);
		workingMemory.setGlobal("serviceKnowledge", serviceKnowledge);
		workingMemory.setGlobal("knowledgeBase", generalKnowledge);
	}

	public void eventReceived(ISelfletEvent event) {

		if (!running) {
			return;
		}

		final ISelfletEvent e = event;

		workingMemory.insert(e);
		workingMemory.fireAllRules();
	}

	@Override
	public int getNumberOfLoadedPackages() {
		return workingMemory.getRuleBase().getPackages().length;
	}

	public void addRule(Object ruleToBeAdded) {

		if (!running) {
			throw new AutonomicManagerException("Cannot add rule. Autonomic manager is not running");
		}

		if (ruleToBeAdded instanceof Package) {

			Package rulePackage = (Package) ruleToBeAdded;
			try {
				ruleBase.addPackage(rulePackage);
			} catch (Exception e) {
				LOG.error("Error while adding rule " + ruleToBeAdded + " in rule base", e);
			}

			fireRuleAddedEvent(rulePackage.getName());
		} else if (ruleToBeAdded instanceof Rule) {

			Rule rule = (Rule) ruleToBeAdded;

			Package pacckage = new Package(rule.getName());
			pacckage.addRule(rule);

			try {
				ruleBase.addPackage(pacckage);
			} catch (Exception e) {
				LOG.error("Error while insering package " + ruleToBeAdded + " in rule base", e);
			}

			fireRuleAddedEvent(rule.getName());
		} else {
			throw new IllegalStateException();
		}

	}

	public void removeRule(Object ruleToBeRemoved) {

		if (!running) {
			throw new AutonomicManagerException("Cannot remove rule. Autonomic manager is not running");
		}

		if (ruleToBeRemoved instanceof String) {
			ruleBase.removePackage((String) ruleToBeRemoved);
			fireRuleRemovedEvent((String) ruleToBeRemoved);

		} else if (ruleToBeRemoved instanceof String[]) {
			String packageName = ((String[]) ruleToBeRemoved)[0];
			String ruleName = ((String[]) ruleToBeRemoved)[1];

			ruleBase.removeRule(packageName, ruleName);
			fireRuleRemovedEvent(ruleName);
		} else {
			throw new IllegalStateException();
		}

	}

	private void fireRuleFiredEvent(String ruleName) {
		dispatchEvent(RuleFiredEvent.class, ruleName);
	}

	private void fireRuleAddedEvent(String ruleName) {
		dispatchEvent(RuleAddedEvent.class, ruleName);
	}

	private void fireRuleRemovedEvent(String ruleName) {
		dispatchEvent(RuleRemovedEvent.class, ruleName);
	}

	@Override
	public ImmutableSet<EventTypeEnum> getReceivedEvents() {
		return ImmutableSet.of(EventTypeEnum.ALL_EVENTS);
	}

}
