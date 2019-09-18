/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.context;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.context.LootContextParameter;

public interface LootContextAware {
    default public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of();
    }

    default public void check(LootTableReporter lootTableReporter) {
        lootTableReporter.checkContext(this);
    }
}

