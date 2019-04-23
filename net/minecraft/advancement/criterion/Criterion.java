/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.util.Identifier;

public interface Criterion<T extends CriterionConditions> {
    public Identifier getId();

    public void beginTrackingCondition(PlayerAdvancementTracker var1, ConditionsContainer<T> var2);

    public void endTrackingCondition(PlayerAdvancementTracker var1, ConditionsContainer<T> var2);

    public void endTracking(PlayerAdvancementTracker var1);

    public T conditionsFromJson(JsonObject var1, JsonDeserializationContext var2);

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

        public void apply(PlayerAdvancementTracker playerAdvancementTracker) {
            playerAdvancementTracker.grantCriterion(this.advancement, this.id);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            ConditionsContainer conditionsContainer = (ConditionsContainer)object;
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

