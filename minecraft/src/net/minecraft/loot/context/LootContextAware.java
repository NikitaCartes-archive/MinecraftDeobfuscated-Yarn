package net.minecraft.loot.context;

import java.util.Set;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.util.context.ContextParameter;

public interface LootContextAware {
	default Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of();
	}

	default void validate(LootTableReporter reporter) {
		reporter.validateContext(this);
	}
}
