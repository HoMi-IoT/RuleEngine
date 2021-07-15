package org.homi.plugins.ruleengine.plugin;

public class Condition {
	private String datumKey;
	private Object comparitorValue;

	public Condition(String datumKey, Object comparitorValue) {
		this.datumKey = datumKey;
		this.comparitorValue = comparitorValue;
	}

	public String getDatumKey() {
		return datumKey;
	}
	
	public Object getComparitorValue() {
		return comparitorValue;
	}
	
	public boolean isSatisfiedBy(Object currentValue) {
		return currentValue.equals(this.comparitorValue);
	}
	
}
