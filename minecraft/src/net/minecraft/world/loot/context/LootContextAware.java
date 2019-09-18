package net.minecraft.world.loot.context;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.loot.LootTableReporter;

public interface LootContextAware {
	default Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of();
	}

	default void check(LootTableReporter lootTableReporter) {
		lootTableReporter.checkContext(this);
	}
}
