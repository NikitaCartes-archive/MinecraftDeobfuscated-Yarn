package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class class_4158 {
	public static final Predicate<class_4158> field_18500 = arg -> ((Set)class_2378.field_17167
				.method_10220()
				.map(class_3852::method_19198)
				.collect(Collectors.toSet()))
			.contains(arg);
	public static final Predicate<class_4158> field_18501 = arg -> true;
	public static final class_4158 field_18502 = method_19160("unemployed", class_3481.field_18830, 1, null, field_18500);
	public static final class_4158 field_18503 = method_19159("armorer", class_3481.field_18831, 1, class_3417.field_18826);
	public static final class_4158 field_18504 = method_19159("butcher", class_3481.field_18832, 1, class_3417.field_18827);
	public static final class_4158 field_18505 = method_19159("cartographer", class_3481.field_18833, 1, class_3417.field_18828);
	public static final class_4158 field_18506 = method_19159("cleric", class_3481.field_18834, 1, class_3417.field_18829);
	public static final class_4158 field_18507 = method_19159("farmer", class_3481.field_18835, 1, class_3417.field_18817);
	public static final class_4158 field_18508 = method_19159("fisherman", class_3481.field_18836, 1, class_3417.field_18818);
	public static final class_4158 field_18509 = method_19159("fletcher", class_3481.field_18837, 1, class_3417.field_18819);
	public static final class_4158 field_18510 = method_19159("leatherworker", class_3481.field_18838, 1, class_3417.field_18820);
	public static final class_4158 field_18511 = method_19159("librarian", class_3481.field_18839, 1, class_3417.field_18821);
	public static final class_4158 field_18512 = method_19159("mason", class_3481.field_18840, 1, class_3417.field_18822);
	public static final class_4158 field_18513 = method_19159("nitwit", class_3481.field_18841, 1, null);
	public static final class_4158 field_18514 = method_19159("shepherd", class_3481.field_18842, 1, class_3417.field_18823);
	public static final class_4158 field_18515 = method_19159("toolsmith", class_3481.field_18843, 1, class_3417.field_18824);
	public static final class_4158 field_18516 = method_19159("weaponsmith", class_3481.field_18844, 1, class_3417.field_18825);
	public static final class_4158 field_18517 = method_19159("home", class_3481.field_16443, 1, null);
	public static final class_4158 field_18518 = method_19159("meeting", class_3481.field_18846, 32, null);
	private static final Map<class_2680, class_4158> field_18849 = Maps.<class_2680, class_4158>newHashMap();
	private final String field_18519;
	private final class_3494<class_2248> field_18520;
	private final Set<class_2680> field_18850 = Sets.<class_2680>newHashSet();
	private final int field_18521;
	@Nullable
	private final class_3414 field_18522;
	private final Predicate<class_4158> field_18523;

	private class_4158(String string, class_3494<class_2248> arg, int i, @Nullable class_3414 arg2, Predicate<class_4158> predicate) {
		this.field_18519 = string;
		this.field_18520 = arg;
		this.field_18521 = i;
		this.field_18522 = arg2;
		this.field_18523 = predicate;
	}

	private class_4158(String string, class_3494<class_2248> arg, int i, @Nullable class_3414 arg2) {
		this.field_18519 = string;
		this.field_18520 = arg;
		this.field_18521 = i;
		this.field_18522 = arg2;
		this.field_18523 = argx -> argx == this;
	}

	public int method_19161() {
		return this.field_18521;
	}

	public Predicate<class_4158> method_19164() {
		return this.field_18523;
	}

	public String toString() {
		return this.field_18519;
	}

	@Nullable
	public class_3414 method_19166() {
		return this.field_18522;
	}

	private static class_4158 method_19159(String string, class_3494<class_2248> arg, int i, @Nullable class_3414 arg2) {
		return class_2378.field_18792.method_10272(new class_2960(string), new class_4158(string, arg, i, arg2));
	}

	private static class_4158 method_19160(String string, class_3494<class_2248> arg, int i, @Nullable class_3414 arg2, Predicate<class_4158> predicate) {
		return class_2378.field_18792.method_10272(new class_2960(string), new class_4158(string, arg, i, arg2, predicate));
	}

	public static Optional<class_4158> method_19516(class_2680 arg) {
		return Optional.ofNullable(field_18849.get(arg));
	}

	private static boolean method_19517(class_2680 arg) {
		return !arg.method_11602(class_3481.field_16443) || arg.method_11654(class_2244.field_9967) != class_2742.field_12557;
	}

	public static Stream<class_2680> method_19518() {
		return field_18849.keySet().stream();
	}

	public static CompletableFuture<Void> method_19515(
		class_3302.class_4045 arg, class_3300 arg2, class_3695 arg3, class_3695 arg4, Executor executor, Executor executor2
	) {
		return arg.method_18352(class_3902.field_17274)
			.thenRunAsync(
				() -> {
					field_18849.clear();
					class_2378.field_18792.forEach(argx -> argx.field_18850.clear());
					class_2378.field_11146
						.method_10220()
						.filter(argx -> argx.method_9525(class_3481.field_18847))
						.forEach(
							argx -> {
								List<class_4158> list = (List<class_4158>)class_2378.field_18792
									.method_10220()
									.filter(arg2x -> arg2x.field_18520.method_15141(argx))
									.collect(Collectors.toList());
								if (list.size() > 1) {
									throw new IllegalStateException(String.format("%s is defined in too many tags", argx));
								} else {
									class_4158 lv = (class_4158)list.get(0);
									argx.method_9595().method_11662().stream().filter(class_4158::method_19517).forEach(arg2x -> {
										lv.field_18850.add(arg2x);
										field_18849.put(arg2x, lv);
									});
								}
							}
						);
				},
				executor2
			);
	}
}
