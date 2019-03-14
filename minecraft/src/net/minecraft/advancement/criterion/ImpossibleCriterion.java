package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.util.Identifier;

public class ImpossibleCriterion implements Criterion<ImpossibleCriterion.Conditions> {
	private static final Identifier ID = new Identifier("impossible");

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<ImpossibleCriterion.Conditions> conditionsContainer
	) {
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<ImpossibleCriterion.Conditions> conditionsContainer
	) {
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
	}

	public ImpossibleCriterion.Conditions method_8949(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new ImpossibleCriterion.Conditions();
	}

	public static class Conditions extends AbstractCriterionConditions {
		public Conditions() {
			super(ImpossibleCriterion.ID);
		}
	}
}
