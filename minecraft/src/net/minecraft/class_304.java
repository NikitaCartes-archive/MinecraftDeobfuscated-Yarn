package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_304 implements Comparable<class_304> {
	private static final Map<String, class_304> field_1657 = Maps.<String, class_304>newHashMap();
	private static final Map<class_3675.class_306, class_304> field_1658 = Maps.<class_3675.class_306, class_304>newHashMap();
	private static final Set<String> field_1652 = Sets.<String>newHashSet();
	private static final Map<String, Integer> field_1656 = class_156.method_654(Maps.<String, Integer>newHashMap(), hashMap -> {
		hashMap.put("key.categories.movement", 1);
		hashMap.put("key.categories.gameplay", 2);
		hashMap.put("key.categories.inventory", 3);
		hashMap.put("key.categories.creative", 4);
		hashMap.put("key.categories.multiplayer", 5);
		hashMap.put("key.categories.ui", 6);
		hashMap.put("key.categories.misc", 7);
	});
	private final String field_1660;
	private final class_3675.class_306 field_1654;
	private final String field_1659;
	private class_3675.class_306 field_1655;
	private boolean field_1653;
	private int field_1661;

	public static void method_1420(class_3675.class_306 arg) {
		class_304 lv = (class_304)field_1658.get(arg);
		if (lv != null) {
			lv.field_1661++;
		}
	}

	public static void method_1416(class_3675.class_306 arg, boolean bl) {
		class_304 lv = (class_304)field_1658.get(arg);
		if (lv != null) {
			lv.field_1653 = bl;
		}
	}

	public static void method_1424() {
		for (class_304 lv : field_1657.values()) {
			if (lv.field_1655.method_1442() == class_3675.class_307.field_1668 && lv.field_1655.method_1444() != class_3675.field_16237.method_1444()) {
				lv.field_1653 = class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), lv.field_1655.method_1444());
			}
		}
	}

	public static void method_1437() {
		for (class_304 lv : field_1657.values()) {
			lv.method_1425();
		}
	}

	public static void method_1426() {
		field_1658.clear();

		for (class_304 lv : field_1657.values()) {
			field_1658.put(lv.field_1655, lv);
		}
	}

	public class_304(String string, int i, String string2) {
		this(string, class_3675.class_307.field_1668, i, string2);
	}

	public class_304(String string, class_3675.class_307 arg, int i, String string2) {
		this.field_1660 = string;
		this.field_1655 = arg.method_1447(i);
		this.field_1654 = this.field_1655;
		this.field_1659 = string2;
		field_1657.put(string, this);
		field_1658.put(this.field_1655, this);
		field_1652.add(string2);
	}

	public boolean method_1434() {
		return this.field_1653;
	}

	public String method_1423() {
		return this.field_1659;
	}

	public boolean method_1436() {
		if (this.field_1661 == 0) {
			return false;
		} else {
			this.field_1661--;
			return true;
		}
	}

	private void method_1425() {
		this.field_1661 = 0;
		this.field_1653 = false;
	}

	public String method_1431() {
		return this.field_1660;
	}

	public class_3675.class_306 method_1429() {
		return this.field_1654;
	}

	public void method_1422(class_3675.class_306 arg) {
		this.field_1655 = arg;
	}

	public int method_1430(class_304 arg) {
		return this.field_1659.equals(arg.field_1659)
			? class_1074.method_4662(this.field_1660).compareTo(class_1074.method_4662(arg.field_1660))
			: ((Integer)field_1656.get(this.field_1659)).compareTo((Integer)field_1656.get(arg.field_1659));
	}

	public static Supplier<String> method_1419(String string) {
		class_304 lv = (class_304)field_1657.get(string);
		return lv == null ? () -> string : lv::method_16007;
	}

	public boolean method_1435(class_304 arg) {
		return this.field_1655.equals(arg.field_1655);
	}

	public boolean method_1415() {
		return this.field_1655.equals(class_3675.field_16237);
	}

	public boolean method_1417(int i, int j) {
		return i == class_3675.field_16237.method_1444()
			? this.field_1655.method_1442() == class_3675.class_307.field_1671 && this.field_1655.method_1444() == j
			: this.field_1655.method_1442() == class_3675.class_307.field_1668 && this.field_1655.method_1444() == i;
	}

	public boolean method_20246(class_3675.class_306 arg) {
		return arg.equals(this.field_1655);
	}

	public boolean method_1433(int i) {
		return this.field_1655.method_1442() == class_3675.class_307.field_1672 && this.field_1655.method_1444() == i;
	}

	public String method_16007() {
		String string = this.field_1655.method_1441();
		int i = this.field_1655.method_1444();
		String string2 = null;
		switch (this.field_1655.method_1442()) {
			case field_1668:
				string2 = class_3675.method_15988(i);
				break;
			case field_1671:
				string2 = class_3675.method_15982(i);
				break;
			case field_1672:
				String string3 = class_1074.method_4662(string);
				string2 = Objects.equals(string3, string) ? class_1074.method_4662(class_3675.class_307.field_1672.method_15989(), i + 1) : string3;
		}

		return string2 == null ? class_1074.method_4662(string) : string2;
	}

	public boolean method_1427() {
		return this.field_1655.equals(this.field_1654);
	}

	public String method_1428() {
		return this.field_1655.method_1441();
	}
}
