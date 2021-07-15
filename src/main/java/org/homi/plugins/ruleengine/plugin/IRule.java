package org.homi.plugins.ruleengine.plugin;

public interface IRule {
	public enum State {READY, SUSPENDED, REMOVED};
	public boolean evaluate();
	public void trigger();
	public void remove();
	public String text();
}
