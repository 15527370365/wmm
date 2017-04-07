package model;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

/**
 * Generated by JFinal, do not modify this file.
 * <pre>
 * Example:
 * public void configPlugin(Plugins me) {
 *     ActiveRecordPlugin arp = new ActiveRecordPlugin(...);
 *     _MappingKit.mapping(arp);
 *     me.add(arp);
 * }
 * </pre>
 */
public class _MappingKit {

	public static void mapping(ActiveRecordPlugin arp) {
		arp.addMapping("combas", "id", Combas.class);
		arp.addMapping("indicator", "id", Indicator.class);
		arp.addMapping("profit", "id", Profit.class);
		arp.addMapping("use_violation", "id", UseViolation.class);
		arp.addMapping("violation", "ViolationID", Violation.class);
	}
}
