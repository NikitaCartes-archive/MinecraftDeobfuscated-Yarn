/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot;

import net.minecraft.world.loot.condition.LootCondition;

public interface ConditionConsumerBuilder<T> {
    public T withCondition(LootCondition.Builder var1);

    public T getThis();
}

