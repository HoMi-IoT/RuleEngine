module org.homi.plugins.ruleengine.plugin {
	requires org.homi.plugin.api;
	requires org.homi.plugins.actionRegistry.specification;
	requires org.homi.plugin.specification;
	requires org.homi.plugins.ruleengine.specification;
	
	provides org.homi.plugin.api.basicplugin.IBasicPlugin
		with org.homi.plugins.ruleengine.plugin.RuleEngine;
}