/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.entry;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import org.apache.commons.lang3.ArrayUtils;

public class AlternativeEntry
extends CombinedEntry {
    AlternativeEntry(LootPoolEntry[] lootPoolEntrys, LootCondition[] lootConditions) {
        super(lootPoolEntrys, lootConditions);
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntryTypes.ALTERNATIVES;
    }

    @Override
    protected EntryCombiner combine(EntryCombiner[] children) {
        switch (children.length) {
            case 0: {
                return ALWAYS_FALSE;
            }
            case 1: {
                return children[0];
            }
            case 2: {
                return children[0].or(children[1]);
            }
        }
        return (context, lootChoiceExpander) -> {
            for (EntryCombiner entryCombiner : children) {
                if (!entryCombiner.expand(context, lootChoiceExpander)) continue;
                return true;
            }
            return false;
        };
    }

    @Override
    public void validate(LootTableReporter reporter) {
        super.validate(reporter);
        for (int i = 0; i < this.children.length - 1; ++i) {
            if (!ArrayUtils.isEmpty(this.children[i].conditions)) continue;
            reporter.report("Unreachable entry!");
        }
    }

    public static Builder builder(LootPoolEntry.Builder<?> ... children) {
        return new Builder(children);
    }

    public static class Builder
    extends LootPoolEntry.Builder<Builder> {
        private final List<LootPoolEntry> children = Lists.newArrayList();

        public Builder(LootPoolEntry.Builder<?> ... children) {
            for (LootPoolEntry.Builder<?> builder : children) {
                this.children.add(builder.build());
            }
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        @Override
        public Builder alternatively(LootPoolEntry.Builder<?> builder) {
            this.children.add(builder.build());
            return this;
        }

        @Override
        public LootPoolEntry build() {
            return new AlternativeEntry(this.children.toArray(new LootPoolEntry[0]), this.getConditions());
        }

        @Override
        protected /* synthetic */ LootPoolEntry.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }
}

