package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.util.Identifier;

public class ImpossibleCriterion implements Criterion<ImpossibleCriterion.Conditions> {
	static final Identifier ID = new Identifier("impossible");

	@Override
	public Identifier getId() {
		return ID;
	}

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
		public Identifier getId() {
			return ImpossibleCriterion.ID;
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			return new JsonObject();
		}
	}
}
