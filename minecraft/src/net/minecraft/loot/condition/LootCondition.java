package net.minecraft.loot.condition;

import java.util.function.Predicate;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextAware;

/**
 * Loot conditions, officially {@index predicate}s, are JSON-based conditions to test
 * against in world. It's used in loot tables, advancements, and commands, and can be
 * defined by data packs.
 */
public interface LootCondition extends LootContextAware, Predicate<LootContext> {
	LootConditionType getType();

	@FunctionalInterface
	public interface Builder {
		LootCondition build();

		default LootCondition.Builder invert() {
			return InvertedLootCondition.builder(this);
		}

		default AnyOfLootCondition.Builder or(LootCondition.Builder condition) {
			return AnyOfLootCondition.builder(this, condition);
		}

		default AllOfLootCondition.Builder and(LootCondition.Builder condition) {
			return AllOfLootCondition.builder(this, condition);
		}
	}
}
