/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.entry;

import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.world.loot.condition.LootCondition;

public class SequenceEntry
extends CombinedEntry {
    SequenceEntry(LootEntry[] lootEntrys, LootCondition[] lootConditions) {
        super(lootEntrys, lootConditions);
    }

    @Override
    protected EntryCombiner combine(EntryCombiner[] entryCombiners) {
        switch (entryCombiners.length) {
            case 0: {
                return ALWAYS_TRUE;
            }
            case 1: {
                return entryCombiners[0];
            }
            case 2: {
                return entryCombiners[0].and(entryCombiners[1]);
            }
        }
        return (lootContext, consumer) -> {
            for (EntryCombiner entryCombiner : entryCombiners) {
                if (entryCombiner.expand(lootContext, consumer)) continue;
                return false;
            }
            return true;
        };
    }
}

