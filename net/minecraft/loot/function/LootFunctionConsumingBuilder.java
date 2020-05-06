/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import net.minecraft.loot.function.LootFunction;

public interface LootFunctionConsumingBuilder<T> {
    public T apply(LootFunction.Builder var1);

    public T getThis();
}

