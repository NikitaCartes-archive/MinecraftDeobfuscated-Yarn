package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface class_1924 {
	List<class_1297> method_8333(@Nullable class_1297 arg, class_238 arg2, @Nullable Predicate<? super class_1297> predicate);

	<T extends class_1297> List<T> method_8390(Class<? extends T> class_, class_238 arg, @Nullable Predicate<? super T> predicate);

	List<? extends class_1657> method_18456();

	default List<class_1297> method_8335(@Nullable class_1297 arg, class_238 arg2) {
		return this.method_8333(arg, arg2, class_1301.field_6155);
	}

	default boolean method_8611(@Nullable class_1297 arg, class_265 arg2) {
		return arg2.method_1110()
			? true
			: this.method_8335(arg, arg2.method_1107())
				.stream()
				.filter(arg2x -> !arg2x.field_5988 && arg2x.field_6033 && (arg == null || !arg2x.method_5794(arg)))
				.noneMatch(arg2x -> class_259.method_1074(arg2, class_259.method_1078(arg2x.method_5829()), class_247.field_16896));
	}

	default <T extends class_1297> List<T> method_18467(Class<? extends T> class_, class_238 arg) {
		return this.method_8390(class_, arg, class_1301.field_6155);
	}

	default Stream<class_265> method_20743(@Nullable class_1297 arg, class_238 arg2, Set<class_1297> set) {
		if (arg2.method_995() < 1.0E-7) {
			return Stream.empty();
		} else {
			class_238 lv = arg2.method_1014(1.0E-7);
			return this.method_8335(arg, lv)
				.stream()
				.filter(argx -> !set.contains(argx))
				.filter(arg2x -> arg == null || !arg.method_5794(arg2x))
				.flatMap(arg2x -> Stream.of(arg2x.method_5827(), arg == null ? null : arg.method_5708(arg2x)))
				.filter(Objects::nonNull)
				.filter(lv::method_994)
				.map(class_259::method_1078);
		}
	}

	@Nullable
	default class_1657 method_8604(double d, double e, double f, double g, @Nullable Predicate<class_1297> predicate) {
		double h = -1.0;
		class_1657 lv = null;

		for (class_1657 lv2 : this.method_18456()) {
			if (predicate == null || predicate.test(lv2)) {
				double i = lv2.method_5649(d, e, f);
				if ((g < 0.0 || i < g * g) && (h == -1.0 || i < h)) {
					h = i;
					lv = lv2;
				}
			}
		}

		return lv;
	}

	@Nullable
	default class_1657 method_18460(class_1297 arg, double d) {
		return this.method_18459(arg.field_5987, arg.field_6010, arg.field_6035, d, false);
	}

	@Nullable
	default class_1657 method_18459(double d, double e, double f, double g, boolean bl) {
		Predicate<class_1297> predicate = bl ? class_1301.field_6156 : class_1301.field_6155;
		return this.method_8604(d, e, f, g, predicate);
	}

	@Nullable
	default class_1657 method_18457(double d, double e, double f) {
		double g = -1.0;
		class_1657 lv = null;

		for (class_1657 lv2 : this.method_18456()) {
			if (class_1301.field_6155.test(lv2)) {
				double h = lv2.method_5649(d, lv2.field_6010, e);
				if ((f < 0.0 || h < f * f) && (g == -1.0 || h < g)) {
					g = h;
					lv = lv2;
				}
			}
		}

		return lv;
	}

	default boolean method_18458(double d, double e, double f, double g) {
		for (class_1657 lv : this.method_18456()) {
			if (class_1301.field_6155.test(lv) && class_1301.field_6157.test(lv)) {
				double h = lv.method_5649(d, e, f);
				if (g < 0.0 || h < g * g) {
					return true;
				}
			}
		}

		return false;
	}

	@Nullable
	default class_1657 method_18462(class_4051 arg, class_1309 arg2) {
		return this.method_18468(this.method_18456(), arg, arg2, arg2.field_5987, arg2.field_6010, arg2.field_6035);
	}

	@Nullable
	default class_1657 method_18463(class_4051 arg, class_1309 arg2, double d, double e, double f) {
		return this.method_18468(this.method_18456(), arg, arg2, d, e, f);
	}

	@Nullable
	default class_1657 method_18461(class_4051 arg, double d, double e, double f) {
		return this.method_18468(this.method_18456(), arg, null, d, e, f);
	}

	@Nullable
	default <T extends class_1309> T method_18465(
		Class<? extends T> class_, class_4051 arg, @Nullable class_1309 arg2, double d, double e, double f, class_238 arg3
	) {
		return this.method_18468(this.method_8390(class_, arg3, null), arg, arg2, d, e, f);
	}

	@Nullable
	default <T extends class_1309> T method_18468(List<? extends T> list, class_4051 arg, @Nullable class_1309 arg2, double d, double e, double f) {
		double g = -1.0;
		T lv = null;

		for (T lv2 : list) {
			if (arg.method_18419(arg2, lv2)) {
				double h = lv2.method_5649(d, e, f);
				if (g == -1.0 || h < g) {
					g = h;
					lv = lv2;
				}
			}
		}

		return lv;
	}

	default List<class_1657> method_18464(class_4051 arg, class_1309 arg2, class_238 arg3) {
		List<class_1657> list = Lists.<class_1657>newArrayList();

		for (class_1657 lv : this.method_18456()) {
			if (arg3.method_1008(lv.field_5987, lv.field_6010, lv.field_6035) && arg.method_18419(arg2, lv)) {
				list.add(lv);
			}
		}

		return list;
	}

	default <T extends class_1309> List<T> method_18466(Class<? extends T> class_, class_4051 arg, class_1309 arg2, class_238 arg3) {
		List<T> list = this.method_8390(class_, arg3, null);
		List<T> list2 = Lists.<T>newArrayList();

		for (T lv : list) {
			if (arg.method_18419(arg2, lv)) {
				list2.add(lv);
			}
		}

		return list2;
	}

	@Nullable
	default class_1657 method_18470(UUID uUID) {
		for (int i = 0; i < this.method_18456().size(); i++) {
			class_1657 lv = (class_1657)this.method_18456().get(i);
			if (uUID.equals(lv.method_5667())) {
				return lv;
			}
		}

		return null;
	}
}
