package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;

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

	public ImpossibleCriterion.Conditions conditionsFromJson(JsonObject jsonObject, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
		return new ImpossibleCriterion.Conditions();
	}

	public static class Conditions implements CriterionConditions {
		@Override
		public JsonObject toJson() {
			return new JsonObject();
		}
	}
}
