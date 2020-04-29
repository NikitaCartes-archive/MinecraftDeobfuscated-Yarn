/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.util.Identifier;

public interface Criterion<T extends CriterionConditions> {
    public Identifier getId();

    public void beginTrackingCondition(PlayerAdvancementTracker var1, ConditionsContainer<T> var2);

    public void endTrackingCondition(PlayerAdvancementTracker var1, ConditionsContainer<T> var2);

    public void endTracking(PlayerAdvancementTracker var1);

    public T conditionsFromJson(JsonObject var1, AdvancementEntityPredicateDeserializer var2);

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
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ConditionsContainer conditionsContainer = (ConditionsContainer)o;
            if (!this.conditions.equals(conditionsContainer.conditions)) {
                return false;
            }
            if (!this.advancement.equals(conditionsContainer.advancement)) {
                return false;
            }
            return this.id.equals(conditionsContainer.id);
        }

        public int hashCode() {
            int i = this.conditions.hashCode();
            i = 31 * i + this.advancement.hashCode();
            i = 31 * i + this.id.hashCode();
            return i;
        }
    }
}

