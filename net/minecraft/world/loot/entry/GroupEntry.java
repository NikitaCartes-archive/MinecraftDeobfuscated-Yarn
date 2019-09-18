/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.entry;

import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.entry.CombinedEntry;
import net.minecraft.world.loot.entry.EntryCombiner;
import net.minecraft.world.loot.entry.LootEntry;

public class GroupEntry
extends CombinedEntry {
    GroupEntry(LootEntry[] lootEntrys, LootCondition[] lootConditions) {
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
                EntryCombiner entryCombiner = entryCombiners[0];
                EntryCombiner entryCombiner2 = entryCombiners[1];
                return (lootContext, consumer) -> {
                    entryCombiner.expand(lootContext, consumer);
                    entryCombiner2.expand(lootContext, consumer);
                    return true;
                };
            }
        }
        return (lootContext, consumer) -> {
            for (EntryCombiner entryCombiner : entryCombiners) {
                entryCombiner.expand(lootContext, consumer);
            }
            return true;
        };
    }
}

