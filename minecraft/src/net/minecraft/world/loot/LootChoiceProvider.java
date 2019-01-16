package net.minecraft.world.loot;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.loot.context.LootContext;

@FunctionalInterface
public interface LootChoiceProvider {
	LootChoiceProvider ALWAYS_FALSE = (lootContext, consumer) -> false;
	LootChoiceProvider ALWAYS_TRUE = (lootContext, consumer) -> true;

	boolean expand(LootContext lootContext, Consumer<LootChoice> consumer);

	default LootChoiceProvider and(LootChoiceProvider lootChoiceProvider) {
		Objects.requireNonNull(lootChoiceProvider);
		return (lootContext, consumer) -> this.expand(lootContext, consumer) && lootChoiceProvider.expand(lootContext, consumer);
	}

	default LootChoiceProvider or(LootChoiceProvider lootChoiceProvider) {
		Objects.requireNonNull(lootChoiceProvider);
		return (lootContext, consumer) -> this.expand(lootContext, consumer) || lootChoiceProvider.expand(lootContext, consumer);
	}
}
