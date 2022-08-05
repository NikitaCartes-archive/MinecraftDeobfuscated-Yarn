/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;

/**
 * A class that lazily evaluates a value.
 * 
 * @deprecated Use {@link com.google.common.base.Suppliers#memoize} instead.
 */
@Deprecated
public class Lazy<T> {
    private final Supplier<T> supplier = Suppliers.memoize(delegate::get);

    public Lazy(Supplier<T> delegate) {
    }

    public T get() {
        return this.supplier.get();
    }
}

