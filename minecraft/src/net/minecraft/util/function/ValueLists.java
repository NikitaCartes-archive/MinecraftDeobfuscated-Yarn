package net.minecraft.util.function;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.minecraft.util.math.MathHelper;

public class ValueLists {
	private static <T> IntFunction<T> createIdToValueFunction(ToIntFunction<T> valueToIdFunction, T[] values) {
		if (values.length == 0) {
			throw new IllegalArgumentException("Empty value list");
		} else {
			Int2ObjectMap<T> int2ObjectMap = new Int2ObjectOpenHashMap<>();

			for (T object : values) {
				int i = valueToIdFunction.applyAsInt(object);
				T object2 = int2ObjectMap.put(i, object);
				if (object2 != null) {
					throw new IllegalArgumentException("Duplicate entry on id " + i + ": current=" + object + ", previous=" + object2);
				}
			}

			return int2ObjectMap;
		}
	}

	public static <T> IntFunction<T> createIdToValueFunction(ToIntFunction<T> valueToIdFunction, T[] values, T fallback) {
		IntFunction<T> intFunction = createIdToValueFunction(valueToIdFunction, values);
		return index -> Objects.requireNonNullElse(intFunction.apply(index), fallback);
	}

	private static <T> T[] validate(ToIntFunction<T> valueToIndexFunction, T[] values) {
		int i = values.length;
		if (i == 0) {
			throw new IllegalArgumentException("Empty value list");
		} else {
			T[] objects = (T[])values.clone();
			Arrays.fill(objects, null);

			for (T object : values) {
				int j = valueToIndexFunction.applyAsInt(object);
				if (j < 0 || j >= i) {
					throw new IllegalArgumentException("Values are not continous, found index " + j + " for value " + object);
				}

				T object2 = objects[j];
				if (object2 != null) {
					throw new IllegalArgumentException("Duplicate entry on id " + j + ": current=" + object + ", previous=" + object2);
				}

				objects[j] = object;
			}

			for (int k = 0; k < i; k++) {
				if (objects[k] == null) {
					throw new IllegalArgumentException("Missing value at index: " + k);
				}
			}

			return objects;
		}
	}

	public static <T> IntFunction<T> createIdToValueFunction(ToIntFunction<T> valueToIdFunction, T[] values, ValueLists.OutOfBoundsHandling outOfBoundsHandling) {
		T[] objects = (T[])validate(valueToIdFunction, values);
		int i = objects.length;

		return switch (outOfBoundsHandling) {
			case ZERO -> {
				T object = objects[0];
				yield index -> index >= 0 && index < i ? objects[index] : object;
			}
			case WRAP -> index -> objects[MathHelper.floorMod(index, i)];
			case CLAMP -> index -> objects[MathHelper.clamp(index, 0, i - 1)];
		};
	}

	public static enum OutOfBoundsHandling {
		ZERO,
		WRAP,
		CLAMP;
	}
}
