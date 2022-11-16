/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.function;

import java.util.function.Consumer;

/**
 * A consumer used in an iteration that can be aborted early.
 */
@FunctionalInterface
public interface LazyIterationConsumer<T> {
    public NextIteration accept(T var1);

    public static <T> LazyIterationConsumer<T> forConsumer(Consumer<T> consumer) {
        return value -> {
            consumer.accept(value);
            return NextIteration.CONTINUE;
        };
    }

    public static enum NextIteration {
        CONTINUE,
        ABORT;


        public boolean shouldAbort() {
            return this == ABORT;
        }
    }
}

