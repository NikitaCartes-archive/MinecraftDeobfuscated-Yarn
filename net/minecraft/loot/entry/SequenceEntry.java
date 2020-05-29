/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.entry;

import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.entry.LootPoolEntryTypes;

public class SequenceEntry
extends CombinedEntry {
    SequenceEntry(LootPoolEntry[] lootPoolEntrys, LootCondition[] lootConditions) {
        super(lootPoolEntrys, lootConditions);
    }

    @Override
    public LootPoolEntryType method_29318() {
        return LootPoolEntryTypes.GROUP;
    }

    @Override
    protected EntryCombiner combine(EntryCombiner[] children) {
        switch (children.length) {
            case 0: {
                return ALWAYS_TRUE;
            }
            case 1: {
                return children[0];
            }
            case 2: {
                EntryCombiner entryCombiner = children[0];
                EntryCombiner entryCombiner2 = children[1];
                return (lootContext, consumer) -> {
                    entryCombiner.expand(lootContext, consumer);
                    entryCombiner2.expand(lootContext, consumer);
                    return true;
                };
            }
        }
        return (context, lootChoiceExpander) -> {
            for (EntryCombiner entryCombiner : children) {
                entryCombiner.expand(context, lootChoiceExpander);
            }
            return true;
        };
    }
}

