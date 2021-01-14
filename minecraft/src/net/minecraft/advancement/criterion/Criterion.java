package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.util.Identifier;

public interface Criterion<T extends CriterionConditions> {
	Identifier getId();

	void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<T> conditions);

	void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<T> conditions);

	void endTracking(PlayerAdvancementTracker tracker);

	T conditionsFromJson(JsonObject obj, AdvancementEntityPredicateDeserializer predicateDeserializer);

	public static class ConditionsContainer<T extends CriterionConditions> {
		private final T conditions;
		private final Advancement advancement;
		private final String id;

		public ConditionsContainer(T conditions, Advancement advancement, String id) {
			this.conditions = conditions;
			this.advancement = advancement;
			this.id = id;
		}

		public T getConditions() {
			return this.conditions;
		}

		public void grant(PlayerAdvancementTracker tracker) {
			tracker.grantCriterion(this.advancement, this.id);
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				Criterion.ConditionsContainer<?> conditionsContainer = (Criterion.ConditionsContainer<?>)o;
				if (!this.conditions.equals(conditionsContainer.conditions)) {
					return false;
				} else {
					return !this.advancement.equals(conditionsContainer.advancement) ? false : this.id.equals(conditionsContainer.id);
				}
			} else {
				return false;
			}
		}

		public int hashCode() {
			int i = this.conditions.hashCode();
			i = 31 * i + this.advancement.hashCode();
			return 31 * i + this.id.hashCode();
		}
	}
}
