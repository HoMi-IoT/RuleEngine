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
		System.out.println("++++++++CURRENT VALUE IS" + currentValue);
		System.out.println("++++++++COMP VALUE IS" + this.comparitorValue);
		System.out.println("currentValue.equals(this.comparitorValue): " + currentValue.equals(this.comparitorValue));
		return currentValue.equals(this.comparitorValue);
	}
	
}
