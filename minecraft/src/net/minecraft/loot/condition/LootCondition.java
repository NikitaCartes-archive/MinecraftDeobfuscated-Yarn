package net.minecraft.loot.condition;

import java.util.function.Predicate;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextAware;

public interface LootCondition extends LootContextAware, Predicate<LootContext> {
	LootConditionType getType();

	@FunctionalInterface
	public interface Builder {
		LootCondition build();

		default LootCondition.Builder invert() {
			return InvertedLootCondition.builder(this);
		}

		default AlternativeLootCondition.Builder or(LootCondition.Builder condition) {
			return AlternativeLootCondition.builder(this, condition);
		}
	}
}
