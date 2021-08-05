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
		double cv;
		if(currentValue instanceof String) {
			cv = Double.parseDouble((String)currentValue);
		}else {
			cv= (double) currentValue;
		}
		switch(result) {
		case GREATER_THAN:
			return cv > (Double) this.getComparitorValue();
		case LESS_THAN:
			return cv < (Double) this.getComparitorValue();
		default:
			return cv == (Double) this.getComparitorValue();
		}
	}
}
