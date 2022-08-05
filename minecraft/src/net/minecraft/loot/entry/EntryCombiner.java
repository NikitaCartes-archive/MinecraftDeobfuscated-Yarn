package net.minecraft.loot.entry;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.context.LootContext;

@FunctionalInterface
interface EntryCombiner {
	EntryCombiner ALWAYS_FALSE = (context, choiceConsumer) -> false;
	EntryCombiner ALWAYS_TRUE = (context, choiceConsumer) -> true;

	boolean expand(LootContext context, Consumer<LootChoice> choiceConsumer);

	default EntryCombiner and(EntryCombiner other) {
		Objects.requireNonNull(other);
		return (context, lootChoiceExpander) -> this.expand(context, lootChoiceExpander) && other.expand(context, lootChoiceExpander);
	}

	default EntryCombiner or(EntryCombiner other) {
		Objects.requireNonNull(other);
		return (context, lootChoiceExpander) -> this.expand(context, lootChoiceExpander) || other.expand(context, lootChoiceExpander);
	}
}
