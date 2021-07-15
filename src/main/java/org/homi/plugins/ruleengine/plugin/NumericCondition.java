package org.homi.plugins.ruleengine.plugin;

public class NumericCondition extends Condition {

	
	public enum Result {GREATER_THAN, LESS_THAN, EQUAL_TO}

	private Result result;
	public NumericCondition(String datumKey, Double comparitorValue, Result result) {
		super(datumKey, comparitorValue);
		this.result = result;
	}
	
	@Override
	public boolean isSatisfiedBy(Object currentValue) {
		switch(result) {
		case GREATER_THAN:
			return (Double) currentValue > (Double) this.getComparitorValue();
		case LESS_THAN:
			return (Double) currentValue < (Double) this.getComparitorValue();
		default:
			return (Double) currentValue == (Double) this.getComparitorValue();
		}
	}
}
