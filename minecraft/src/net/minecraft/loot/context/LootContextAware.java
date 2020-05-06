package net.minecraft.loot.context;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.loot.LootTableReporter;

public interface LootContextAware {
	default Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of();
	}

	default void validate(LootTableReporter reporter) {
		reporter.validateContext(this);
	}
}
