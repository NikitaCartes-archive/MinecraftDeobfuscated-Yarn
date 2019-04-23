/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.loot.LootChoice;
import net.minecraft.world.loot.context.LootContext;

@FunctionalInterface
interface LootChoiceProvider {
    public static final LootChoiceProvider ALWAYS_FALSE = (lootContext, consumer) -> false;
    public static final LootChoiceProvider ALWAYS_TRUE = (lootContext, consumer) -> true;

    public boolean expand(LootContext var1, Consumer<LootChoice> var2);

    default public LootChoiceProvider and(LootChoiceProvider lootChoiceProvider) {
        Objects.requireNonNull(lootChoiceProvider);
        return (lootContext, consumer) -> this.expand(lootContext, consumer) && lootChoiceProvider.expand(lootContext, consumer);
    }

    default public LootChoiceProvider or(LootChoiceProvider lootChoiceProvider) {
        Objects.requireNonNull(lootChoiceProvider);
        return (lootContext, consumer) -> this.expand(lootContext, consumer) || lootChoiceProvider.expand(lootContext, consumer);
    }
}

