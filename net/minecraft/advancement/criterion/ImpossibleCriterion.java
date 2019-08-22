/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.util.Identifier;

public class ImpossibleCriterion
implements Criterion<Conditions> {
    private static final Identifier ID = new Identifier("impossible");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void beginTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<Conditions> conditionsContainer) {
    }

    @Override
    public void endTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<Conditions> conditionsContainer) {
    }

    @Override
    public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
    }

    public Conditions method_8949(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return new Conditions();
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_8949(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        public Conditions() {
            super(ID);
        }
    }
}

