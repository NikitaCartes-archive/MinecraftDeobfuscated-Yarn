package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;

public interface Criterion<T extends CriterionConditions> {
	void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<T> conditions);

	void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<T> conditions);

	void endTracking(PlayerAdvancementTracker tracker);

	T conditionsFromJson(JsonObject obj, AdvancementEntityPredicateDeserializer predicateDeserializer);

	default AdvancementCriterion<T> create(T conditions) {
		return new AdvancementCriterion<>(this, conditions);
	}

	public static record ConditionsContainer<T extends CriterionConditions>(T conditions, AdvancementEntry advancement, String id) {
		public void grant(PlayerAdvancementTracker tracker) {
			tracker.grantCriterion(this.advancement, this.id);
		}
	}
}
