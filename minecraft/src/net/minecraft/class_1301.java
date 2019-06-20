package net.minecraft;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class class_1301 {
	public static final Predicate<class_1297> field_6154 = class_1297::method_5805;
	public static final Predicate<class_1309> field_6157 = class_1309::method_5805;
	public static final Predicate<class_1297> field_6153 = arg -> arg.method_5805() && !arg.method_5782() && !arg.method_5765();
	public static final Predicate<class_1297> field_6152 = arg -> arg instanceof class_1263 && arg.method_5805();
	public static final Predicate<class_1297> field_6156 = arg -> !(arg instanceof class_1657) || !arg.method_7325() && !((class_1657)arg).method_7337();
	public static final Predicate<class_1297> field_6155 = arg -> !arg.method_7325();

	public static Predicate<class_1297> method_5909(double d, double e, double f, double g) {
		double h = g * g;
		return arg -> arg != null && arg.method_5649(d, e, f) <= h;
	}

	public static Predicate<class_1297> method_5911(class_1297 arg) {
		class_270 lv = arg.method_5781();
		class_270.class_271 lv2 = lv == null ? class_270.class_271.field_1437 : lv.method_1203();
		return (Predicate<class_1297>)(lv2 == class_270.class_271.field_1435
			? Predicates.alwaysFalse()
			: field_6155.and(
				arg4 -> {
					if (!arg4.method_5810()) {
						return false;
					} else if (!arg.field_6002.field_9236 || arg4 instanceof class_1657 && ((class_1657)arg4).method_7340()) {
						class_270 lvx = arg4.method_5781();
						class_270.class_271 lv2x = lvx == null ? class_270.class_271.field_1437 : lvx.method_1203();
						if (lv2x == class_270.class_271.field_1435) {
							return false;
						} else {
							boolean bl = lv != null && lv.method_1206(lvx);
							return (lv2 == class_270.class_271.field_1440 || lv2x == class_270.class_271.field_1440) && bl
								? false
								: lv2 != class_270.class_271.field_1434 && lv2x != class_270.class_271.field_1434 || bl;
						}
					} else {
						return false;
					}
				}
			));
	}

	public static Predicate<class_1297> method_5913(class_1297 arg) {
		return arg2 -> {
			while (arg2.method_5765()) {
				arg2 = arg2.method_5854();
				if (arg2 == arg) {
					return false;
				}
			}

			return true;
		};
	}

	public static class class_1302 implements Predicate<class_1297> {
		private final class_1799 field_6158;

		public class_1302(class_1799 arg) {
			this.field_6158 = arg;
		}

		public boolean method_5916(@Nullable class_1297 arg) {
			if (!arg.method_5805()) {
				return false;
			} else if (!(arg instanceof class_1309)) {
				return false;
			} else {
				class_1309 lv = (class_1309)arg;
				return lv.method_18397(this.field_6158);
			}
		}
	}
}
