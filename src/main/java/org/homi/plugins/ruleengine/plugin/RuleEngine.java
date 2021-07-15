package org.homi.plugins.ruleengine.plugin;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.homi.plugin.api.PluginID;
import org.homi.plugin.api.basicplugin.AbstractBasicPlugin;
import org.homi.plugin.api.commander.Commander;
import org.homi.plugin.api.commander.CommanderBuilder;
import org.homi.plugin.api.exceptions.InternalPluginException;
import org.homi.plugins.ar.specification.actions.Action;
import org.homi.plugins.ar.specification.actions.ActionQuery;
import org.homi.plugins.ruleengine.specification.RuleEngineSpec;

@PluginID(id="RuleEngine")
public class RuleEngine extends AbstractBasicPlugin {

	private static List<IRule> rules = new ArrayList<>();
	
	@Override
	public void setup() {
		Action.setPluginProvider(this.getPluginProvider());
		ActionQuery aq = new ActionQuery();
		

		CommanderBuilder<RuleEngineSpec> cb = new CommanderBuilder<>(RuleEngineSpec.class);
		Commander<RuleEngineSpec> c = cb
				.onCommandEquals(RuleEngineSpec.ADD_RULE, this::addRule)
				.onCommandEquals(RuleEngineSpec.REMOVE_RULE, this::removeRule)
				.onCommandEquals(RuleEngineSpec.GET_RULES, this::getRules)
				.build(); 
		
		this.addCommander(RuleEngineSpec.class, c);
		
//		try {
//			new Rule(List.of(new Condition("ruleKey", "satisfied")), Action.getAction(aq.type(TYPE.SCRIPT).command("ScriptCommand")), 5000);
//		} catch (InvalidArgumentException | ArgumentLengthException | PluginException e) {
//			e.printStackTrace();
//		}
		
	}

	
	private synchronized Boolean addRule(Object...args) throws InternalPluginException {
		try {
			String ruleText = (String) args[0];
			IRule r = RuleParser.parse(ruleText);
			RuleEngine.add(r);
			return true;
		} catch (Exception e) {
			throw new InternalPluginException(e);
		}
	}

	private synchronized Boolean removeRule(Object...args) throws InternalPluginException {
		try {
			Integer ruleID = (Integer) args[0];
			var r = RuleEngine.rules.get(ruleID);
			r.remove();
			RuleEngine.remove(r);
			return true;
		} catch (Exception e) {
			throw new InternalPluginException(e);
		}
	}
	

	private synchronized Map<Integer, String> getRules(Object...args) throws InternalPluginException {
		Map<Integer, String> result = new HashMap<>();
		for(int i =0; i< RuleEngine.rules.size(); i++) {
			result.put(i, RuleEngine.rules.get(i).text());
		}
		return result;
	}
	
	@Override
	public void teardown() {
		RuleEngine.rules.forEach(IRule::remove);
	}

//	public static void main(String[] args) {
//		// [at time (8)] 
//		// if time between and condition(datum is x)
//		// action
//		// sleep()
//		System.out.println(LocalTime.now().until(LocalTime.of(20, 30, 0), ChronoUnit.MILLIS));	
//	}

	public static void add(IRule rule) {
		RuleEngine.rules.add(rule);
	}
	
	public static void remove(IRule rule) {
		RuleEngine.rules.remove(rule);
	}
	
	public static List<IRule> getRules() {
		return List.copyOf(RuleEngine.rules);
	}
	
}
