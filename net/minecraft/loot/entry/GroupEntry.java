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

public class GroupEntry
extends CombinedEntry {
    GroupEntry(LootPoolEntry[] lootPoolEntrys, LootCondition[] lootConditions) {
        super(lootPoolEntrys, lootConditions);
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntryTypes.SEQUENCE;
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
                return children[0].and(children[1]);
            }
        }
        return (context, lootChoiceExpander) -> {
            for (EntryCombiner entryCombiner : children) {
                if (entryCombiner.expand(context, lootChoiceExpander)) continue;
                return false;
            }
            return true;
        };
    }
}

