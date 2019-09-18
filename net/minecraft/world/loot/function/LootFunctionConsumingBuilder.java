/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.function;

import net.minecraft.world.loot.function.LootFunction;

public interface LootFunctionConsumingBuilder<T> {
    public T withFunction(LootFunction.Builder var1);

    public T getThis();
}

