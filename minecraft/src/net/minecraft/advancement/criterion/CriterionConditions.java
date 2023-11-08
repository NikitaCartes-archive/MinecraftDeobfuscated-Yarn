package net.minecraft.advancement.criterion;

import net.minecraft.predicate.entity.LootContextPredicateValidator;

public interface CriterionConditions {
	void validate(LootContextPredicateValidator validator);
}
