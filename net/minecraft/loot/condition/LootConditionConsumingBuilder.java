/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.condition;

import java.util.function.Function;
import net.minecraft.loot.condition.LootCondition;

public interface LootConditionConsumingBuilder<T extends LootConditionConsumingBuilder<T>> {
    public T conditionally(LootCondition.Builder var1);

    default public <E> T conditionally(Iterable<E> conditions, Function<E, LootCondition.Builder> toBuilderFunction) {
        T lootConditionConsumingBuilder = this.getThisConditionConsumingBuilder();
        for (E object : conditions) {
            lootConditionConsumingBuilder = lootConditionConsumingBuilder.conditionally(toBuilderFunction.apply(object));
        }
        return lootConditionConsumingBuilder;
    }

    public T getThisConditionConsumingBuilder();
}

