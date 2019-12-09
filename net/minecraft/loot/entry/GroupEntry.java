/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.entry;

import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.loot.entry.LootEntry;

public class GroupEntry
extends CombinedEntry {
    GroupEntry(LootEntry[] lootEntrys, LootCondition[] lootConditions) {
        super(lootEntrys, lootConditions);
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
                return (context, lootChoiceExpander) -> {
                    entryCombiner.expand(context, lootChoiceExpander);
                    entryCombiner2.expand(context, lootChoiceExpander);
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

