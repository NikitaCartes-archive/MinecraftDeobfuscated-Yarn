package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;

public class class_2806 {
	private static final EnumSet<class_2902.class_2903> field_19159 = EnumSet.of(class_2902.class_2903.field_13195, class_2902.class_2903.field_13194);
	private static final EnumSet<class_2902.class_2903> field_19160 = EnumSet.of(
		class_2902.class_2903.field_13200, class_2902.class_2903.field_13202, class_2902.class_2903.field_13197, class_2902.class_2903.field_13203
	);
	public static final class_2806 field_12798 = method_16555("empty", null, -1, field_19159, class_2806.class_2808.field_12808, (arg, arg2, list, arg3) -> {
	});
	public static final class_2806 field_16423 = method_16557(
		"structure_starts", field_12798, 0, field_19159, class_2806.class_2808.field_12808, (arg, arg2, arg3, arg4, arg5, function, list, arg6) -> {
			if (!arg6.method_12009().method_12165(method_16561()) || !arg6.method_12038()) {
				class_1923 lv = arg6.method_12004();
				arg5.method_15557(lv, true);
			}

			if (!arg6.method_12009().method_12165(arg)) {
				if (arg2.method_8401().method_220()) {
					arg3.method_16129(arg6, arg3, arg4);
				}

				if (arg6 instanceof class_2839) {
					((class_2839)arg6).method_12308(arg);
				}
			}

			return CompletableFuture.completedFuture(arg6);
		}
	);
	public static final class_2806 field_16422 = method_16555(
		"structure_references",
		field_16423,
		8,
		field_19159,
		class_2806.class_2808.field_12808,
		(arg, arg2, list, arg3) -> arg2.method_16130(new class_3233(arg, list), arg3)
	);
	public static final class_2806 field_12794 = method_16555(
		"biomes", field_16422, 0, field_19159, class_2806.class_2808.field_12808, (arg, arg2, list, arg3) -> arg2.method_12106(arg3)
	);
	public static final class_2806 field_12804 = method_16555(
		"noise", field_12794, 8, field_19159, class_2806.class_2808.field_12808, (arg, arg2, list, arg3) -> arg2.method_12088(new class_3233(arg, list), arg3)
	);
	public static final class_2806 field_12796 = method_16555(
		"surface", field_12804, 0, field_19159, class_2806.class_2808.field_12808, (arg, arg2, list, arg3) -> arg2.method_12110(arg3)
	);
	public static final class_2806 field_12801 = method_16555(
		"carvers",
		field_12796,
		0,
		field_19159,
		class_2806.class_2808.field_12808,
		(arg, arg2, list, arg3) -> arg2.method_12108(arg3, class_2893.class_2894.field_13169)
	);
	public static final class_2806 field_12790 = method_16555(
		"liquid_carvers",
		field_12801,
		0,
		field_19160,
		class_2806.class_2808.field_12808,
		(arg, arg2, list, arg3) -> arg2.method_12108(arg3, class_2893.class_2894.field_13166)
	);
	public static final class_2806 field_12795 = method_16555(
		"features",
		field_12790,
		8,
		field_19160,
		class_2806.class_2808.field_12808,
		(arg, arg2, list, arg3) -> {
			class_2902.method_16684(
				arg3,
				EnumSet.of(class_2902.class_2903.field_13197, class_2902.class_2903.field_13203, class_2902.class_2903.field_13200, class_2902.class_2903.field_13202)
			);
			arg2.method_12102(new class_3233(arg, list));
		}
	);
	public static final class_2806 field_12805 = method_16557(
		"light", field_12795, 1, field_19160, class_2806.class_2808.field_12808, (arg, arg2, arg3, arg4, arg5, function, list, arg6) -> {
			arg6.method_17032(arg5);
			boolean bl = arg6.method_12009().method_12165(arg) && arg6.method_12038();
			if (!arg6.method_12009().method_12165(arg)) {
				((class_2839)arg6).method_12308(arg);
			}

			return arg5.method_17310(arg6, bl);
		}
	);
	public static final class_2806 field_12786 = method_16555(
		"spawn", field_12805, 0, field_19160, class_2806.class_2808.field_12808, (arg, arg2, list, arg3) -> arg2.method_12107(new class_3233(arg, list))
	);
	public static final class_2806 field_12800 = method_16555(
		"heightmaps", field_12786, 0, field_19160, class_2806.class_2808.field_12808, (arg, arg2, list, arg3) -> {
		}
	);
	public static final class_2806 field_12803 = method_16557(
		"full",
		field_12800,
		0,
		field_19160,
		class_2806.class_2808.field_12807,
		(arg, arg2, arg3, arg4, arg5, function, list, arg6) -> (CompletableFuture<class_2791>)function.apply(arg6)
	);
	private static final List<class_2806> field_12791 = ImmutableList.of(
		field_12803, field_12795, field_12790, field_16423, field_16423, field_16423, field_16423, field_16423, field_16423, field_16423, field_16423
	);
	private static final IntList field_12788 = class_156.method_654(new IntArrayList(method_16558().size()), intArrayList -> {
		int i = 0;

		for (int j = method_16558().size() - 1; j >= 0; j--) {
			while (i + 1 < field_12791.size() && j <= ((class_2806)field_12791.get(i + 1)).method_16559()) {
				i++;
			}

			intArrayList.add(0, i);
		}
	});
	private final String field_12789;
	private final int field_16646;
	private final class_2806 field_16647;
	private final class_2806.class_2807 field_12792;
	private final int field_12802;
	private final class_2806.class_2808 field_12787;
	private final EnumSet<class_2902.class_2903> field_12793;

	private static class_2806 method_16555(
		String string, @Nullable class_2806 arg, int i, EnumSet<class_2902.class_2903> enumSet, class_2806.class_2808 arg2, class_2806.class_3768 arg3
	) {
		return method_16557(string, arg, i, enumSet, arg2, arg3);
	}

	private static class_2806 method_16557(
		String string, @Nullable class_2806 arg, int i, EnumSet<class_2902.class_2903> enumSet, class_2806.class_2808 arg2, class_2806.class_2807 arg3
	) {
		return class_2378.method_10226(class_2378.field_16643, string, new class_2806(string, arg, i, enumSet, arg2, arg3));
	}

	public static List<class_2806> method_16558() {
		List<class_2806> list = Lists.<class_2806>newArrayList();

		class_2806 lv;
		for (lv = field_12803; lv.method_16560() != lv; lv = lv.method_16560()) {
			list.add(lv);
		}

		list.add(lv);
		Collections.reverse(list);
		return list;
	}

	private static class_2806 method_16561() {
		return field_12805;
	}

	public static class_2806 method_12161(int i) {
		if (i >= field_12791.size()) {
			return field_12798;
		} else {
			return i < 0 ? field_12803 : (class_2806)field_12791.get(i);
		}
	}

	public static int method_12155() {
		return field_12791.size();
	}

	public static int method_12175(class_2806 arg) {
		return field_12788.getInt(arg.method_16559());
	}

	class_2806(String string, @Nullable class_2806 arg, int i, EnumSet<class_2902.class_2903> enumSet, class_2806.class_2808 arg2, class_2806.class_2807 arg3) {
		this.field_12789 = string;
		this.field_16647 = arg == null ? this : arg;
		this.field_12792 = arg3;
		this.field_12802 = i;
		this.field_12787 = arg2;
		this.field_12793 = enumSet;
		this.field_16646 = arg == null ? 0 : arg.method_16559() + 1;
	}

	public int method_16559() {
		return this.field_16646;
	}

	public String method_12172() {
		return this.field_12789;
	}

	public class_2806 method_16560() {
		return this.field_16647;
	}

	public CompletableFuture<class_2791> method_12154(
		class_3218 arg, class_2794<?> arg2, class_3485 arg3, class_3227 arg4, Function<class_2791, CompletableFuture<class_2791>> function, List<class_2791> list
	) {
		return this.field_12792.doWork(this, arg, arg2, arg3, arg4, function, list, (class_2791)list.get(list.size() / 2));
	}

	public int method_12152() {
		return this.field_12802;
	}

	public class_2806.class_2808 method_12164() {
		return this.field_12787;
	}

	public static class_2806 method_12168(String string) {
		return class_2378.field_16643.method_10223(class_2960.method_12829(string));
	}

	public EnumSet<class_2902.class_2903> method_12160() {
		return this.field_12793;
	}

	public boolean method_12165(class_2806 arg) {
		return this.method_16559() >= arg.method_16559();
	}

	public String toString() {
		return class_2378.field_16643.method_10221(this).toString();
	}

	interface class_2807 {
		CompletableFuture<class_2791> doWork(
			class_2806 arg,
			class_3218 arg2,
			class_2794<?> arg3,
			class_3485 arg4,
			class_3227 arg5,
			Function<class_2791, CompletableFuture<class_2791>> function,
			List<class_2791> list,
			class_2791 arg6
		);
	}

	public static enum class_2808 {
		field_12808,
		field_12807;
	}

	interface class_3768 extends class_2806.class_2807 {
		@Override
		default CompletableFuture<class_2791> doWork(
			class_2806 arg,
			class_3218 arg2,
			class_2794<?> arg3,
			class_3485 arg4,
			class_3227 arg5,
			Function<class_2791, CompletableFuture<class_2791>> function,
			List<class_2791> list,
			class_2791 arg6
		) {
			if (!arg6.method_12009().method_12165(arg)) {
				this.doWork(arg2, arg3, list, arg6);
				if (arg6 instanceof class_2839) {
					((class_2839)arg6).method_12308(arg);
				}
			}

			return CompletableFuture.completedFuture(arg6);
		}

		void doWork(class_3218 arg, class_2794<?> arg2, List<class_2791> list, class_2791 arg3);
	}
}
