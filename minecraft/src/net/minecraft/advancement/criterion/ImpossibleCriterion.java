package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.predicate.entity.LootContextPredicateValidator;

public class ImpossibleCriterion implements Criterion<ImpossibleCriterion.Conditions> {
	@Override
	public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<ImpossibleCriterion.Conditions> conditions) {
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<ImpossibleCriterion.Conditions> conditions) {
	}

	@Override
	public void endTracking(PlayerAdvancementTracker tracker) {
	}

	@Override
	public Codec<ImpossibleCriterion.Conditions> getConditionsCodec() {
		return ImpossibleCriterion.Conditions.CODEC;
	}

	public static record Conditions() implements CriterionConditions {
		public static final Codec<ImpossibleCriterion.Conditions> CODEC = Codec.unit(new ImpossibleCriterion.Conditions());

		@Override
		public void validate(LootContextPredicateValidator validator) {
		}
	}
}
