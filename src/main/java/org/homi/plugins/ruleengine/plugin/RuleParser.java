package org.homi.plugins.ruleengine.plugin;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.homi.plugin.api.exceptions.InternalPluginException;
import org.homi.plugins.ar.specification.actions.Action;
import org.homi.plugins.ar.specification.actions.ActionQuery;
import org.homi.plugins.ruleengine.plugin.NumericCondition.Result;
import org.homi.plugins.ruleengine.plugin.Rule.TimeRange;

public class RuleParser {
	private RuleParser() {}
	
	public static IRule parse(String rule) throws InternalPluginException {
		System.out.println("***-----****PARSING RULE: " + rule);
		try {
			var lines = rule.trim().split("\n");
			System.out.println("***-----****Lines: " + Arrays.toString(lines));
			if(lines[0].startsWith("between ")) {
				System.out.println("***-----**** time constraint");
				return new Rule(rule, parseTimeRange(lines[0]), parseConditions(lines[1]), parseAction(lines[2]), parseRestTime(lines[3]) );
			}else {
				System.out.println("***-----**** no time constraint");
				var conds = parseConditions(lines[0]);
				System.out.println("***-----**** PARSED CONDITIONS");
				var a = parseAction(lines[1]);
				System.out.println("***-----**** PARSED ACTION");
				var rt = parseRestTime(lines[2]);
				System.out.println("***-----**** PARSED REST TIME");
				return new Rule(rule, conds, a, rt );
			}
		}catch (Exception e) {
			throw new InternalPluginException("invalid rule definition", e);
		}
	}

	private static long parseRestTime(String s) throws InternalPluginException {
		try {
			String time = s.trim().split("\\s+")[2];
			return Long.parseLong(time);
		} catch (Exception e) {
			throw new InternalPluginException("invalid rest period", e);
		}
	}
	
	private static Action<?> parseAction(String s) throws InternalPluginException {
		Action<?> a = null;
		try {
			ActionQuery aq = new ActionQuery();
			String[] words = s.trim().split("\\s+");
			if(words[0].equalsIgnoreCase("invoke")) {
				if(words[1].equalsIgnoreCase("service")) {
					a = Action.getAction(aq.type(ActionQuery.TYPE.SPECIFICATION).command(words[2]));
				} else if(words[1].equalsIgnoreCase("script")) {
					a = Action.getAction(aq.type(ActionQuery.TYPE.SCRIPT).command(words[2]));
				}
				for(int i=0; i<words.length -3; i++) {
					a.set(String.valueOf(i), parseArgument(words[i+3]));
				}
				if(a==null)
					throw new InternalPluginException("Invalid Command Specification");
				return a;
			}
		} catch (InternalPluginException e) {
			throw e;
		} catch (Exception e) {
			throw new InternalPluginException("Invalid Command Specification", e);
		}
		throw new InternalPluginException("Invalid Command Specification");
	}

	private static Object parseArgument(String word) throws NumberFormatException {
		return word.startsWith("\"")&& word.endsWith("\"") ? word.substring(1,  word.length()-1): Double.parseDouble(word);
		
	}
	
	private static List<Condition> parseConditions(String s) throws InternalPluginException {
		try {
			List<Condition> conditions = new ArrayList<>();
			var conStrings = Pattern.compile(" and ", Pattern.CASE_INSENSITIVE).split(s.trim());
			for(String cond: conStrings) {
				conditions.add(parseCondition(cond.trim()));
			}
			return conditions;
		} catch (Exception e) {
			throw new InternalPluginException("malformed conditions", e);
		}
	}
	
	private static Condition parseCondition(String cond) throws InternalPluginException {
		var parts = cond.split("\\s+");
		switch(parts[1]) {
		case "is":
			return new Condition(parts[0], parts[2]);
		case "=":
			return new NumericCondition(parts[0], Double.parseDouble(parts[2]), Result.EQUAL_TO);
		case ">":
			return new NumericCondition(parts[0], Double.parseDouble(parts[2]), Result.LESS_THAN);
		case "<":
			return new NumericCondition(parts[0], Double.parseDouble(parts[2]), Result.GREATER_THAN);
		}
		throw new InternalPluginException("malformed condition");
	}

	private static TimeRange parseTimeRange(String s) throws InternalPluginException {
		var times = s.trim().split("\\s+");
		if(times.length == 4) {
			try {
				LocalTime.parse(times[1]);
				LocalTime.parse(times[3]);
				return new TimeRange(times[1], times[3]);
			} catch (Exception e) {
				new InternalPluginException("invalid time range", e);
			}
		}
		throw new InternalPluginException("invalid time range declaration");
	}
}
