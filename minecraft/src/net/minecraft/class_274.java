package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Optional;

public class class_274 {
	public static final Map<String, class_274> field_1455 = Maps.<String, class_274>newHashMap();
	public static final class_274 field_1468 = new class_274("dummy");
	public static final class_274 field_1462 = new class_274("trigger");
	public static final class_274 field_1456 = new class_274("deathCount");
	public static final class_274 field_1463 = new class_274("playerKillCount");
	public static final class_274 field_1457 = new class_274("totalKillCount");
	public static final class_274 field_1453 = new class_274("health", true, class_274.class_275.field_1471);
	public static final class_274 field_1464 = new class_274("food", true, class_274.class_275.field_1472);
	public static final class_274 field_1459 = new class_274("air", true, class_274.class_275.field_1472);
	public static final class_274 field_1452 = new class_274("armor", true, class_274.class_275.field_1472);
	public static final class_274 field_1460 = new class_274("xp", true, class_274.class_275.field_1472);
	public static final class_274 field_1465 = new class_274("level", true, class_274.class_275.field_1472);
	public static final class_274[] field_1466 = new class_274[]{
		new class_274("teamkill." + class_124.field_1074.method_537()),
		new class_274("teamkill." + class_124.field_1058.method_537()),
		new class_274("teamkill." + class_124.field_1077.method_537()),
		new class_274("teamkill." + class_124.field_1062.method_537()),
		new class_274("teamkill." + class_124.field_1079.method_537()),
		new class_274("teamkill." + class_124.field_1064.method_537()),
		new class_274("teamkill." + class_124.field_1065.method_537()),
		new class_274("teamkill." + class_124.field_1080.method_537()),
		new class_274("teamkill." + class_124.field_1063.method_537()),
		new class_274("teamkill." + class_124.field_1078.method_537()),
		new class_274("teamkill." + class_124.field_1060.method_537()),
		new class_274("teamkill." + class_124.field_1075.method_537()),
		new class_274("teamkill." + class_124.field_1061.method_537()),
		new class_274("teamkill." + class_124.field_1076.method_537()),
		new class_274("teamkill." + class_124.field_1054.method_537()),
		new class_274("teamkill." + class_124.field_1068.method_537())
	};
	public static final class_274[] field_1458 = new class_274[]{
		new class_274("killedByTeam." + class_124.field_1074.method_537()),
		new class_274("killedByTeam." + class_124.field_1058.method_537()),
		new class_274("killedByTeam." + class_124.field_1077.method_537()),
		new class_274("killedByTeam." + class_124.field_1062.method_537()),
		new class_274("killedByTeam." + class_124.field_1079.method_537()),
		new class_274("killedByTeam." + class_124.field_1064.method_537()),
		new class_274("killedByTeam." + class_124.field_1065.method_537()),
		new class_274("killedByTeam." + class_124.field_1080.method_537()),
		new class_274("killedByTeam." + class_124.field_1063.method_537()),
		new class_274("killedByTeam." + class_124.field_1078.method_537()),
		new class_274("killedByTeam." + class_124.field_1060.method_537()),
		new class_274("killedByTeam." + class_124.field_1075.method_537()),
		new class_274("killedByTeam." + class_124.field_1061.method_537()),
		new class_274("killedByTeam." + class_124.field_1076.method_537()),
		new class_274("killedByTeam." + class_124.field_1054.method_537()),
		new class_274("killedByTeam." + class_124.field_1068.method_537())
	};
	private final String field_1454;
	private final boolean field_1461;
	private final class_274.class_275 field_1467;

	public class_274(String string) {
		this(string, false, class_274.class_275.field_1472);
	}

	protected class_274(String string, boolean bl, class_274.class_275 arg) {
		this.field_1454 = string;
		this.field_1461 = bl;
		this.field_1467 = arg;
		field_1455.put(string, this);
	}

	public static Optional<class_274> method_1224(String string) {
		if (field_1455.containsKey(string)) {
			return Optional.of(field_1455.get(string));
		} else {
			int i = string.indexOf(58);
			return i < 0
				? Optional.empty()
				: class_2378.field_11152
					.method_17966(class_2960.method_12838(string.substring(0, i), '.'))
					.flatMap(arg -> method_1223(arg, class_2960.method_12838(string.substring(i + 1), '.')));
		}
	}

	private static <T> Optional<class_274> method_1223(class_3448<T> arg, class_2960 arg2) {
		return arg.method_14959().method_17966(arg2).map(arg::method_14956);
	}

	public String method_1225() {
		return this.field_1454;
	}

	public boolean method_1226() {
		return this.field_1461;
	}

	public class_274.class_275 method_1227() {
		return this.field_1467;
	}

	public static enum class_275 {
		field_1472("integer"),
		field_1471("hearts");

		private final String field_1469;
		private static final Map<String, class_274.class_275> field_1470;

		private class_275(String string2) {
			this.field_1469 = string2;
		}

		public String method_1228() {
			return this.field_1469;
		}

		public static class_274.class_275 method_1229(String string) {
			return (class_274.class_275)field_1470.getOrDefault(string, field_1472);
		}

		static {
			Builder<String, class_274.class_275> builder = ImmutableMap.builder();

			for (class_274.class_275 lv : values()) {
				builder.put(lv.field_1469, lv);
			}

			field_1470 = builder.build();
		}
	}
}
