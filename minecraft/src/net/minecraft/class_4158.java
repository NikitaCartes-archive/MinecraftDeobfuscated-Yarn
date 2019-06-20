package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class class_4158 {
	private static final Predicate<class_4158> field_18500 = arg -> ((Set)class_2378.field_17167
				.method_10220()
				.map(class_3852::method_19198)
				.collect(Collectors.toSet()))
			.contains(arg);
	public static final Predicate<class_4158> field_18501 = arg -> true;
	private static final Set<class_2680> field_19227 = (Set<class_2680>)ImmutableList.of(
			class_2246.field_10069,
			class_2246.field_10461,
			class_2246.field_10527,
			class_2246.field_10288,
			class_2246.field_10109,
			class_2246.field_10141,
			class_2246.field_10561,
			class_2246.field_10621,
			class_2246.field_10326,
			class_2246.field_10180,
			class_2246.field_10230,
			class_2246.field_10410,
			class_2246.field_10610,
			class_2246.field_10019,
			class_2246.field_10120,
			class_2246.field_10356
		)
		.stream()
		.flatMap(arg -> arg.method_9595().method_11662().stream())
		.filter(arg -> arg.method_11654(class_2244.field_9967) == class_2742.field_12560)
		.collect(ImmutableSet.toImmutableSet());
	private static final Map<class_2680, class_4158> field_18849 = Maps.<class_2680, class_4158>newHashMap();
	public static final class_4158 field_18502 = method_20358("unemployed", ImmutableSet.of(), 1, null, field_18500);
	public static final class_4158 field_18503 = method_20357("armorer", method_20356(class_2246.field_16333), 1, class_3417.field_18826);
	public static final class_4158 field_18504 = method_20357("butcher", method_20356(class_2246.field_16334), 1, class_3417.field_18827);
	public static final class_4158 field_18505 = method_20357("cartographer", method_20356(class_2246.field_16336), 1, class_3417.field_18828);
	public static final class_4158 field_18506 = method_20357("cleric", method_20356(class_2246.field_10333), 1, class_3417.field_18829);
	public static final class_4158 field_18507 = method_20357("farmer", method_20356(class_2246.field_17563), 1, class_3417.field_18817);
	public static final class_4158 field_18508 = method_20357("fisherman", method_20356(class_2246.field_16328), 1, class_3417.field_18818);
	public static final class_4158 field_18509 = method_20357("fletcher", method_20356(class_2246.field_16331), 1, class_3417.field_18819);
	public static final class_4158 field_18510 = method_20357("leatherworker", method_20356(class_2246.field_10593), 1, class_3417.field_18820);
	public static final class_4158 field_18511 = method_20357("librarian", method_20356(class_2246.field_16330), 1, class_3417.field_18821);
	public static final class_4158 field_18512 = method_20357("mason", method_20356(class_2246.field_16335), 1, class_3417.field_18822);
	public static final class_4158 field_18513 = method_20357("nitwit", ImmutableSet.of(), 1, null);
	public static final class_4158 field_18514 = method_20357("shepherd", method_20356(class_2246.field_10083), 1, class_3417.field_18823);
	public static final class_4158 field_18515 = method_20357("toolsmith", method_20356(class_2246.field_16329), 1, class_3417.field_18824);
	public static final class_4158 field_18516 = method_20357("weaponsmith", method_20356(class_2246.field_16337), 1, class_3417.field_18825);
	public static final class_4158 field_18517 = method_20357("home", field_19227, 1, null);
	public static final class_4158 field_18518 = method_20357("meeting", method_20356(class_2246.field_16332), 32, null);
	private final String field_18519;
	private final Set<class_2680> field_18850;
	private final int field_18521;
	@Nullable
	private final class_3414 field_18522;
	private final Predicate<class_4158> field_18523;

	private static Set<class_2680> method_20356(class_2248 arg) {
		return ImmutableSet.copyOf(arg.method_9595().method_11662());
	}

	private class_4158(String string, Set<class_2680> set, int i, @Nullable class_3414 arg, Predicate<class_4158> predicate) {
		this.field_18519 = string;
		this.field_18850 = ImmutableSet.copyOf(set);
		this.field_18521 = i;
		this.field_18522 = arg;
		this.field_18523 = predicate;
	}

	private class_4158(String string, Set<class_2680> set, int i, @Nullable class_3414 arg) {
		this.field_18519 = string;
		this.field_18850 = ImmutableSet.copyOf(set);
		this.field_18521 = i;
		this.field_18522 = arg;
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

	private static class_4158 method_20357(String string, Set<class_2680> set, int i, @Nullable class_3414 arg) {
		return method_20354(class_2378.field_18792.method_10272(new class_2960(string), new class_4158(string, set, i, arg)));
	}

	private static class_4158 method_20358(String string, Set<class_2680> set, int i, @Nullable class_3414 arg, Predicate<class_4158> predicate) {
		return method_20354(class_2378.field_18792.method_10272(new class_2960(string), new class_4158(string, set, i, arg, predicate)));
	}

	private static class_4158 method_20354(class_4158 arg) {
		arg.field_18850.forEach(arg2 -> {
			class_4158 lv = (class_4158)field_18849.put(arg2, arg);
			if (lv != null) {
				throw new IllegalStateException(String.format("%s is defined in too many tags", arg2));
			}
		});
		return arg;
	}

	public static Optional<class_4158> method_19516(class_2680 arg) {
		return Optional.ofNullable(field_18849.get(arg));
	}

	public static Stream<class_2680> method_19518() {
		return field_18849.keySet().stream();
	}
}
