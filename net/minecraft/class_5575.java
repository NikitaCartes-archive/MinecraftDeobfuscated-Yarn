/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import org.jetbrains.annotations.Nullable;

public interface class_5575<B, T extends B> {
    public static <B, T extends B> class_5575<B, T> method_31795(final Class<T> class_) {
        return new class_5575<B, T>(){

            @Override
            @Nullable
            public T method_31796(B object) {
                return class_.isInstance(object) ? object : null;
            }

            @Override
            public Class<? extends B> method_31794() {
                return class_;
            }
        };
    }

    @Nullable
    public T method_31796(B var1);

    public Class<? extends B> method_31794();
}

