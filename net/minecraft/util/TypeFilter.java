/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import org.jetbrains.annotations.Nullable;

/**
 * A filter that determines if an object of some supertype {@code B} can be
 * treated as an object of some subtype {@code T}.
 * 
 * @param <B> the base type that's the input to the filter
 * @param <T> the desired type of this filter
 */
public interface TypeFilter<B, T extends B> {
    /**
     * Creates a filter whose filtering condition is whether the object is an instance of the given class.
     */
    public static <B, T extends B> TypeFilter<B, T> instanceOf(final Class<T> cls) {
        return new TypeFilter<B, T>(){

            @Override
            @Nullable
            public T downcast(B obj) {
                return cls.isInstance(obj) ? obj : null;
            }

            @Override
            public Class<? extends B> getBaseClass() {
                return cls;
            }
        };
    }

    /**
     * Checks if the argument can be converted to the type {@code T} and returns the argument, or {@code null} otherwise.
     */
    @Nullable
    public T downcast(B var1);

    public Class<? extends B> getBaseClass();
}

