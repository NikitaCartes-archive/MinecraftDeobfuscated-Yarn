package net.minecraft.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

/**
 * Contains utility methods that accept or return nullable values.
 */
public class Nullables {
	@Deprecated
	public static <T> T requireNonNullElse(@Nullable T first, T second) {
		return (T)Objects.requireNonNullElse(first, second);
	}

	/**
	 * {@return the {@code value} with {@code mapper} applied if the value is not {@code null},
	 * otherwise {@code null}}
	 * 
	 * <p>This is the nullable equivalent to {@link java.util.Optional#map}.
	 */
	@Nullable
	public static <T, R> R map(@Nullable T value, Function<T, R> mapper) {
		return (R)(value == null ? null : mapper.apply(value));
	}

	/**
	 * {@return the {@code value} with {@code mapper} applied if the value is not {@code null},
	 * otherwise {@code other}}
	 * 
	 * <p>This is the nullable equivalent to {@link java.util.Optional#map} chained with
	 * {@link java.util.Optional#orElse}.
	 */
	public static <T, R> R mapOrElse(@Nullable T value, Function<T, R> mapper, R other) {
		return (R)(value == null ? other : mapper.apply(value));
	}

	/**
	 * {@return the {@code value} with {@code mapper} applied if the value is not {@code null},
	 * otherwise {@code getter.get()}}
	 * 
	 * <p>This is the nullable equivalent to {@link java.util.Optional#map} chained with
	 * {@link java.util.Optional#orElseGet}.
	 */
	public static <T, R> R mapOrElseGet(@Nullable T value, Function<T, R> mapper, Supplier<R> getter) {
		return (R)(value == null ? getter.get() : mapper.apply(value));
	}

	/**
	 * {@return the first element of {@code collection}, or {@code null} if it is empty}
	 */
	@Nullable
	public static <T> T getFirst(Collection<T> collection) {
		Iterator<T> iterator = collection.iterator();
		return (T)(iterator.hasNext() ? iterator.next() : null);
	}

	/**
	 * {@return the first element of {@code collection}, or {@code defaultValue} if it is empty}
	 */
	public static <T> T getFirstOrElse(Collection<T> collection, T defaultValue) {
		Iterator<T> iterator = collection.iterator();
		return (T)(iterator.hasNext() ? iterator.next() : defaultValue);
	}

	/**
	 * {@return the first element of {@code collection}, or {@code getter.get()} if it is empty}
	 */
	public static <T> T getFirstOrElseGet(Collection<T> collection, Supplier<T> getter) {
		Iterator<T> iterator = collection.iterator();
		return (T)(iterator.hasNext() ? iterator.next() : getter.get());
	}

	/**
	 * {@return whether {@code array} is {@code null} or empty}
	 */
	public static <T> boolean isEmpty(@Nullable T[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * {@return whether {@code array} is {@code null} or empty}
	 */
	public static boolean isEmpty(@Nullable boolean[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * {@return whether {@code array} is {@code null} or empty}
	 */
	public static boolean isEmpty(@Nullable byte[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * {@return whether {@code array} is {@code null} or empty}
	 */
	public static boolean isEmpty(@Nullable char[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * {@return whether {@code array} is {@code null} or empty}
	 */
	public static boolean isEmpty(@Nullable short[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * {@return whether {@code array} is {@code null} or empty}
	 */
	public static boolean isEmpty(@Nullable int[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * {@return whether {@code array} is {@code null} or empty}
	 */
	public static boolean isEmpty(@Nullable long[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * {@return whether {@code array} is {@code null} or empty}
	 */
	public static boolean isEmpty(@Nullable float[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * {@return whether {@code array} is {@code null} or empty}
	 */
	public static boolean isEmpty(@Nullable double[] array) {
		return array == null || array.length == 0;
	}
}
