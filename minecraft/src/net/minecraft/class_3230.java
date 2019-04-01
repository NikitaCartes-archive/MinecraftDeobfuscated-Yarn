package net.minecraft;

import java.util.Comparator;

public class class_3230<T> {
	private final String field_14034;
	private final Comparator<T> field_14029;
	public static final class_3230<class_3902> field_14030 = method_14291("start", (arg, arg2) -> 0);
	public static final class_3230<class_3902> field_17264 = method_14291("dragon", (arg, arg2) -> 0);
	public static final class_3230<class_1923> field_14033 = method_14291("player", Comparator.comparingLong(class_1923::method_8324));
	public static final class_3230<class_1923> field_14031 = method_14291("forced", Comparator.comparingLong(class_1923::method_8324));
	public static final class_3230<class_1923> field_14032 = method_14291("unknown", Comparator.comparingLong(class_1923::method_8324));

	public static <T> class_3230<T> method_14291(String string, Comparator<T> comparator) {
		return new class_3230<>(string, comparator);
	}

	protected class_3230(String string, Comparator<T> comparator) {
		this.field_14034 = string;
		this.field_14029 = comparator;
	}

	public String toString() {
		return this.field_14034;
	}

	public Comparator<T> method_14292() {
		return this.field_14029;
	}
}
