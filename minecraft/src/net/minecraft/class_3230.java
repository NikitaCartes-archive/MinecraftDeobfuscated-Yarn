package net.minecraft;

import java.util.Comparator;

public class class_3230<T> {
	private final String field_14034;
	private final Comparator<T> field_14029;
	private final long field_19348;
	public static final class_3230<class_3902> field_14030 = method_14291("start", (arg, arg2) -> 0);
	public static final class_3230<class_3902> field_17264 = method_14291("dragon", (arg, arg2) -> 0);
	public static final class_3230<class_1923> field_14033 = method_14291("player", Comparator.comparingLong(class_1923::method_8324));
	public static final class_3230<class_1923> field_14031 = method_14291("forced", Comparator.comparingLong(class_1923::method_8324));
	public static final class_3230<class_1923> field_19270 = method_14291("light", Comparator.comparingLong(class_1923::method_8324));
	public static final class_3230<class_2265> field_19280 = method_14291("portal", Comparator.comparingLong(class_2265::method_20475));
	public static final class_3230<Integer> field_19347 = method_20628("post_teleport", Integer::compareTo, 5);
	public static final class_3230<class_1923> field_14032 = method_20628("unknown", Comparator.comparingLong(class_1923::method_8324), 1);

	public static <T> class_3230<T> method_14291(String string, Comparator<T> comparator) {
		return new class_3230<>(string, comparator, 0L);
	}

	public static <T> class_3230<T> method_20628(String string, Comparator<T> comparator, int i) {
		return new class_3230<>(string, comparator, (long)i);
	}

	protected class_3230(String string, Comparator<T> comparator, long l) {
		this.field_14034 = string;
		this.field_14029 = comparator;
		this.field_19348 = l;
	}

	public String toString() {
		return this.field_14034;
	}

	public Comparator<T> method_14292() {
		return this.field_14029;
	}

	public long method_20629() {
		return this.field_19348;
	}
}
