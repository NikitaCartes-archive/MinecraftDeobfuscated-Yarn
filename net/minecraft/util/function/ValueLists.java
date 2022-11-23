/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.function;

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
        }
        Int2ObjectOpenHashMap<T> int2ObjectMap = new Int2ObjectOpenHashMap<T>();
        for (T object : values) {
            int i = valueToIdFunction.applyAsInt(object);
            T object2 = int2ObjectMap.put(i, object);
            if (object2 == null) continue;
            throw new IllegalArgumentException("Duplicate entry on id " + i + ": current=" + object + ", previous=" + object2);
        }
        return int2ObjectMap;
    }

    public static <T> IntFunction<T> createIdToValueFunction(ToIntFunction<T> valueToIdFunction, T[] values, T fallback) {
        IntFunction intFunction = ValueLists.createIdToValueFunction(valueToIdFunction, values);
        return index -> Objects.requireNonNullElse(intFunction.apply(index), fallback);
    }

    private static <T> T[] validate(ToIntFunction<T> valueToIndexFunction, T[] values) {
        int i = values.length;
        if (i == 0) {
            throw new IllegalArgumentException("Empty value list");
        }
        Object[] objects = (Object[])values.clone();
        Arrays.fill(objects, null);
        for (T object : values) {
            int j = valueToIndexFunction.applyAsInt(object);
            if (j < 0 || j >= i) {
                throw new IllegalArgumentException("Values are not continous, found index " + j + " for value " + object);
            }
            Object object2 = objects[j];
            if (object2 != null) {
                throw new IllegalArgumentException("Duplicate entry on id " + j + ": current=" + object + ", previous=" + object2);
            }
            objects[j] = object;
        }
        for (int k = 0; k < i; ++k) {
            if (objects[k] != null) continue;
            throw new IllegalArgumentException("Missing value at index: " + k);
        }
        return objects;
    }

    public static <T> IntFunction<T> createIdToValueFunction(ToIntFunction<T> valueToIdFunction, T[] values, OutOfBoundsHandling outOfBoundsHandling) {
        Object[] objects = ValueLists.validate(valueToIdFunction, values);
        int i = objects.length;
        return switch (outOfBoundsHandling) {
            default -> throw new IncompatibleClassChangeError();
            case OutOfBoundsHandling.ZERO -> {
                Object object = objects[0];
                yield index -> index >= 0 && index < i ? objects[index] : object;
            }
            case OutOfBoundsHandling.WRAP -> index -> objects[MathHelper.floorMod(index, i)];
            case OutOfBoundsHandling.CLAMP -> index -> objects[MathHelper.clamp(index, 0, i - 1)];
        };
    }

    public static enum OutOfBoundsHandling {
        ZERO,
        WRAP,
        CLAMP;

    }
}

