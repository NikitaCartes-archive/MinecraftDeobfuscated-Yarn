package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Util;

/**
 * A list of loot conditions applied to entities. All conditions must match for this
 * unified conditions to {@linkplain #test match}. Mainly used by advancements.
 */
public class LootContextPredicate {
	public static final Codec<LootContextPredicate> CODEC = LootCondition.CODEC
		.listOf()
		.xmap(LootContextPredicate::new, lootContextPredicate -> lootContextPredicate.conditions);
	private final List<LootCondition> conditions;
	private final Predicate<LootContext> combinedCondition;

	LootContextPredicate(List<LootCondition> conditions) {
		this.conditions = conditions;
		this.combinedCondition = Util.allOf(conditions);
	}

	public static LootContextPredicate create(LootCondition... conditions) {
		return new LootContextPredicate(List.of(conditions));
	}

	public boolean test(LootContext context) {
		return this.combinedCondition.test(context);
	}

	public void validateConditions(LootTableReporter reporter) {
		for (int i = 0; i < this.conditions.size(); i++) {
			LootCondition lootCondition = (LootCondition)this.conditions.get(i);
			lootCondition.validate(reporter.makeChild("[" + i + "]"));
		}
	}
}
