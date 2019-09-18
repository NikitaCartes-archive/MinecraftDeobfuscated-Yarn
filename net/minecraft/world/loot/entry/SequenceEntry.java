/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.entry;

import net.minecraft.class_4570;
import net.minecraft.world.loot.entry.CombinedEntry;
import net.minecraft.world.loot.entry.EntryCombiner;
import net.minecraft.world.loot.entry.LootEntry;

public class SequenceEntry
extends CombinedEntry {
    SequenceEntry(LootEntry[] lootEntrys, class_4570[] args) {
        super(lootEntrys, args);
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

