/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.condition;

import java.util.function.Predicate;
import net.minecraft.loot.condition.AlternativeLootCondition;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextAware;

public interface LootCondition
extends LootContextAware,
Predicate<LootContext> {
    public LootConditionType getType();

    @FunctionalInterface
    public static interface Builder {
        public LootCondition build();

        default public Builder invert() {
            return InvertedLootCondition.builder(this);
        }

        default public AlternativeLootCondition.Builder or(Builder condition) {
            return AlternativeLootCondition.builder(this, condition);
        }
    }
}

