/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.entry;

import net.minecraft.world.loot.LootChoiceProvider;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.entry.CombinedEntry;
import net.minecraft.world.loot.entry.LootEntry;

public class SequenceEntry
extends CombinedEntry {
    SequenceEntry(LootEntry[] lootEntrys, LootCondition[] lootConditions) {
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
                return lootChoiceProviders[0].and(lootChoiceProviders[1]);
            }
        }
        return (lootContext, consumer) -> {
            for (LootChoiceProvider lootChoiceProvider : lootChoiceProviders) {
                if (lootChoiceProvider.expand(lootContext, consumer)) continue;
                return false;
            }
            return true;
        };
    }
}

