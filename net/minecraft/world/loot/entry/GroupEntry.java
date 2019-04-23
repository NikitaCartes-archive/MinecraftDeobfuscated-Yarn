/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.entry;

import net.minecraft.world.loot.LootChoiceProvider;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.entry.CombinedEntry;
import net.minecraft.world.loot.entry.LootEntry;

public class GroupEntry
extends CombinedEntry {
    GroupEntry(LootEntry[] lootEntrys, LootCondition[] lootConditions) {
        super(lootEntrys, lootConditions);
    }

    @Override
    protected LootChoiceProvider combine(LootChoiceProvider[] lootChoiceProviders) {
        switch (lootChoiceProviders.length) {
            case 0: {
                return ALWAYS_TRUE;
            }
            case 1: {
                return lootChoiceProviders[0];
            }
            case 2: {
                LootChoiceProvider lootChoiceProvider = lootChoiceProviders[0];
                LootChoiceProvider lootChoiceProvider2 = lootChoiceProviders[1];
                return (lootContext, consumer) -> {
                    lootChoiceProvider.expand(lootContext, consumer);
                    lootChoiceProvider2.expand(lootContext, consumer);
                    return true;
                };
            }
        }
        return (lootContext, consumer) -> {
            for (LootChoiceProvider lootChoiceProvider : lootChoiceProviders) {
                lootChoiceProvider.expand(lootContext, consumer);
            }
            return true;
        };
    }
}

