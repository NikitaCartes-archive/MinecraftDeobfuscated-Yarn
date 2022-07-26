/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.function.Supplier;
import net.minecraft.network.Packet;
import org.jetbrains.annotations.Nullable;

public interface class_7648 {
    public static class_7648 method_45084(final Runnable runnable) {
        return new class_7648(){

            @Override
            public void method_45083() {
                runnable.run();
            }

            @Override
            @Nullable
            public Packet<?> method_45086() {
                runnable.run();
                return null;
            }
        };
    }

    public static class_7648 method_45085(final Supplier<Packet<?>> supplier) {
        return new class_7648(){

            @Override
            @Nullable
            public Packet<?> method_45086() {
                return (Packet)supplier.get();
            }
        };
    }

    default public void method_45083() {
    }

    @Nullable
    default public Packet<?> method_45086() {
        return null;
    }
}

