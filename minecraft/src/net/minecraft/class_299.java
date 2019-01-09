package net.minecraft;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_299 extends class_3439 {
	private final class_1863 field_1639;
	private final Map<class_314, List<class_516>> field_1638 = Maps.<class_314, List<class_516>>newHashMap();
	private final List<class_516> field_1637 = Lists.<class_516>newArrayList();

	public class_299(class_1863 arg) {
		this.field_1639 = arg;
	}

	public void method_1401() {
		this.field_1637.clear();
		this.field_1638.clear();
		Table<class_314, String, class_516> table = HashBasedTable.create();

		for (class_1860 lv : this.field_1639.method_8126()) {
			if (!lv.method_8118()) {
				class_314 lv2 = method_1400(lv);
				String string = lv.method_8112();
				class_516 lv3;
				if (string.isEmpty()) {
					lv3 = this.method_1394(lv2);
				} else {
					lv3 = table.get(lv2, string);
					if (lv3 == null) {
						lv3 = this.method_1394(lv2);
						table.put(lv2, string, lv3);
					}
				}

				lv3.method_2654(lv);
			}
		}
	}

	private class_516 method_1394(class_314 arg) {
		class_516 lv = new class_516();
		this.field_1637.add(lv);
		((List)this.field_1638.computeIfAbsent(arg, argx -> Lists.newArrayList())).add(lv);
		if (arg == class_314.field_1811 || arg == class_314.field_1808 || arg == class_314.field_1812) {
			((List)this.field_1638.computeIfAbsent(class_314.field_1804, argx -> Lists.newArrayList())).add(lv);
		} else if (arg == class_314.field_17111 || arg == class_314.field_17112) {
			((List)this.field_1638.computeIfAbsent(class_314.field_17110, argx -> Lists.newArrayList())).add(lv);
		} else if (arg == class_314.field_17114) {
			((List)this.field_1638.computeIfAbsent(class_314.field_17113, argx -> Lists.newArrayList())).add(lv);
		} else {
			((List)this.field_1638.computeIfAbsent(class_314.field_1809, argx -> Lists.newArrayList())).add(lv);
		}

		return lv;
	}

	private static class_314 method_1400(class_1860 arg) {
		if (arg instanceof class_3861) {
			if (arg.method_8110().method_7909() instanceof class_1789) {
				return class_314.field_1808;
			} else {
				return arg.method_8110().method_7909() instanceof class_1747 ? class_314.field_1811 : class_314.field_1812;
			}
		} else if (arg instanceof class_3859) {
			return arg.method_8110().method_7909() instanceof class_1747 ? class_314.field_17111 : class_314.field_17112;
		} else if (arg instanceof class_3862) {
			return class_314.field_17114;
		} else {
			class_1799 lv = arg.method_8110();
			class_1761 lv2 = lv.method_7909().method_7859();
			if (lv2 == class_1761.field_7931) {
				return class_314.field_1806;
			} else if (lv2 == class_1761.field_7930 || lv2 == class_1761.field_7916) {
				return class_314.field_1813;
			} else {
				return lv2 == class_1761.field_7914 ? class_314.field_1803 : class_314.field_1810;
			}
		}
	}

	public static List<class_314> method_1395(class_1729<?> arg) {
		if (arg instanceof class_1714 || arg instanceof class_1723) {
			return Lists.<class_314>newArrayList(class_314.field_1809, class_314.field_1813, class_314.field_1806, class_314.field_1810, class_314.field_1803);
		} else if (arg instanceof class_3858) {
			return Lists.<class_314>newArrayList(class_314.field_1804, class_314.field_1808, class_314.field_1811, class_314.field_1812);
		} else if (arg instanceof class_3705) {
			return Lists.<class_314>newArrayList(class_314.field_17110, class_314.field_17111, class_314.field_17112);
		} else {
			return arg instanceof class_3706 ? Lists.<class_314>newArrayList(class_314.field_17113, class_314.field_17114) : Lists.<class_314>newArrayList();
		}
	}

	public List<class_516> method_1393() {
		return this.field_1637;
	}

	public List<class_516> method_1396(class_314 arg) {
		return (List<class_516>)this.field_1638.getOrDefault(arg, Collections.emptyList());
	}
}
