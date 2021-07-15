package org.homi.plugins.ruleengine.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.homi.plugin.api.exceptions.PluginException;
import org.homi.plugin.api.observer.IObserver;
import org.homi.plugin.specification.exceptions.ArgumentLengthException;
import org.homi.plugin.specification.exceptions.InvalidArgumentException;
import org.homi.plugins.ar.specification.actions.Action;
import org.homi.plugins.ar.specification.actions.ActionQuery;
import org.homi.plugins.ar.specification.actions.ActionQuery.TYPE;

public class Rule implements IRule {

	private List<Condition> conditions;
	private Action<?> action;
	private Map<String, Object> values = new ConcurrentHashMap<>();
	private State state = State.READY;
	private long restTime;
	private List<IObserver> observers = new ArrayList<>();
	private TimeRange timeRange;
	private String text;
	
	public static class TimeRange{
		private String startTime;
		private String endTime;

		public TimeRange(String startTime, String endTime) {
			this.startTime = startTime;
			this.endTime = endTime;
		}
		
		public boolean isSatisfied(){
			return TimeService.nowIsBetween(startTime, endTime);
		}
	}

	public Rule(String text, List<Condition> conditions, Action<?> action, long restTime) {
		this.text = text;
		this.conditions = conditions;
		this.action = action;
		this.restTime = restTime;
		initializeConditions();
	}
	
	public Rule(String text, TimeRange tr, List<Condition> conditions, Action<?> action, long restTime) {
		this.text = text;
		this.conditions = conditions;
		this.action = action;
		this.restTime = restTime;
		this.timeRange = tr;
		initializeConditions();
	}

	private void initializeConditions() {
		this.conditions.stream().forEach((condition)->{
			ActionQuery aq = new ActionQuery();
			Action<Void> observerAction;
			try {
				observerAction = Action.getAction(aq.type(TYPE.SPECIFICATION).specificationID("DatastoreSpec").command("OBSERVE"));
				IObserver o = new IObserver() {
					@Override
					public void update(Object... updateValue) {
						values.put(condition.getDatumKey(), updateValue[0]);	
						evaluate();
					}};
				this.observers.add(o);
				
				observerAction.run(condition.getDatumKey(), o);
			} catch (InvalidArgumentException | ArgumentLengthException | PluginException e) {
				e.printStackTrace();
			}
		});
	}
	
	@Override
	public boolean evaluate() {
		if( this.state != State.REMOVED && this.state == State.READY && allConditionsSatisfied() && this.timeRange!=null && this.timeRange.isSatisfied() ) {
			this.trigger();
			this.state = State.SUSPENDED;
			TimeService.scheduleTask(()->{if(this.state!= State.REMOVED) this.state = State.READY;}, this.restTime);
			return true;
		}
		return false;
	}

	private boolean allConditionsSatisfied() {
		return this.conditions.stream().allMatch(
				(condition)->{
					return condition.isSatisfiedBy(this.values.getOrDefault(condition.getDatumKey(), null));}
				);
	}

	@Override
	public void trigger(){
		try {
			this.action.run();
		} catch (InvalidArgumentException | ArgumentLengthException | PluginException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void remove() {
		this.state = State.REMOVED;
		//TODO: DETACH LISTENERS FUNCRTIONALITY NOT SUPPORTED BY DATASTORE
//		Action<Void> observerAction;
//		ActionQuery aq = new ActionQuery();
//		try {
//			observerAction = Action.getAction(aq.type(TYPE.SPECIFICATION).specificationID("DatastoreSpec").command("OBSERVE"));
//			this.observers.forEach((o)->{
//				
//			});
//		} catch (InvalidArgumentException | ArgumentLengthException | PluginException e) {
//			e.printStackTrace();
//		}
	}
	
	@Override
	public String text() {
		return this.text;
	}
	
	

}
