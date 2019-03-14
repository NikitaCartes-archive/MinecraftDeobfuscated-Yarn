package net.minecraft.world.loot.context;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTableReporter;

public interface ParameterConsumer {
	default Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of();
	}

	default void check(LootTableReporter lootTableReporter, Function<Identifier, LootSupplier> function, Set<Identifier> set, LootContextType lootContextType) {
		lootContextType.check(lootTableReporter, this);
	}
}
