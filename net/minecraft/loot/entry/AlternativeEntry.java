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
import net.minecraft.loot.entry.LootEntry;
import org.apache.commons.lang3.ArrayUtils;

public class AlternativeEntry
extends CombinedEntry {
    AlternativeEntry(LootEntry[] lootEntrys, LootCondition[] lootConditions) {
        super(lootEntrys, lootConditions);
    }

    @Override
    protected EntryCombiner combine(EntryCombiner[] entryCombiners) {
        switch (entryCombiners.length) {
            case 0: {
                return ALWAYS_FALSE;
            }
            case 1: {
                return entryCombiners[0];
            }
            case 2: {
                return entryCombiners[0].or(entryCombiners[1]);
            }
        }
        return (lootContext, consumer) -> {
            for (EntryCombiner entryCombiner : entryCombiners) {
                if (!entryCombiner.expand(lootContext, consumer)) continue;
                return true;
            }
            return false;
        };
    }

    @Override
    public void check(LootTableReporter lootTableReporter) {
        super.check(lootTableReporter);
        for (int i = 0; i < this.children.length - 1; ++i) {
            if (!ArrayUtils.isEmpty(this.children[i].conditions)) continue;
            lootTableReporter.report("Unreachable entry!");
        }
    }

    public static Builder builder(LootEntry.Builder<?> ... builders) {
        return new Builder(builders);
    }

    public static class Builder
    extends LootEntry.Builder<Builder> {
        private final List<LootEntry> children = Lists.newArrayList();

        public Builder(LootEntry.Builder<?> ... builders) {
            for (LootEntry.Builder<?> builder : builders) {
                this.children.add(builder.build());
            }
        }

        protected Builder method_388() {
            return this;
        }

        @Override
        public Builder withChild(LootEntry.Builder<?> builder) {
            this.children.add(builder.build());
            return this;
        }

        @Override
        public LootEntry build() {
            return new AlternativeEntry(this.children.toArray(new LootEntry[0]), this.getConditions());
        }

        @Override
        protected /* synthetic */ LootEntry.Builder getThisBuilder() {
            return this.method_388();
        }
    }
}

