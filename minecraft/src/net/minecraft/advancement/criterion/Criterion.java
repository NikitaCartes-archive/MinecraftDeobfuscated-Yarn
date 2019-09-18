package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.util.Identifier;

public interface Criterion<T extends CriterionConditions> {
	Identifier getId();

	void beginTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<T> conditionsContainer);

	void endTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<T> conditionsContainer);

	void endTracking(PlayerAdvancementTracker playerAdvancementTracker);

	T conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext);

	public static class ConditionsContainer<T extends CriterionConditions> {
		private final T conditions;
		private final Advancement advancement;
		private final String id;

		public ConditionsContainer(T criterionConditions, Advancement advancement, String string) {
			this.conditions = criterionConditions;
			this.advancement = advancement;
			this.id = string;
		}

		public T getConditions() {
			return this.conditions;
		}

		public void grant(PlayerAdvancementTracker playerAdvancementTracker) {
			playerAdvancementTracker.grantCriterion(this.advancement, this.id);
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				Criterion.ConditionsContainer<?> conditionsContainer = (Criterion.ConditionsContainer<?>)object;
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
