package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancement.ServerAdvancementManager;
import net.minecraft.util.Identifier;

public class ImpossibleCriterion implements Criterion<ImpossibleCriterion.Conditions> {
	private static final Identifier ID = new Identifier("impossible");

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void addCondition(ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<ImpossibleCriterion.Conditions> conditionsContainer) {
	}

	@Override
	public void removeCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<ImpossibleCriterion.Conditions> conditionsContainer
	) {
	}

	@Override
	public void removePlayer(ServerAdvancementManager serverAdvancementManager) {
	}

	public ImpossibleCriterion.Conditions deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new ImpossibleCriterion.Conditions();
	}

	public static class Conditions extends AbstractCriterionConditions {
		public Conditions() {
			super(ImpossibleCriterion.ID);
		}
	}
}
