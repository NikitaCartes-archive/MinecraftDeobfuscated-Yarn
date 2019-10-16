/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EffectsChangedCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("effects_changed");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_8862(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        EntityEffectPredicate entityEffectPredicate = EntityEffectPredicate.deserialize(jsonObject.get("effects"));
        return new Conditions(entityEffectPredicate);
    }

    public void trigger(ServerPlayerEntity serverPlayerEntity) {
        this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(serverPlayerEntity));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_8862(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityEffectPredicate effects;

        public Conditions(EntityEffectPredicate entityEffectPredicate) {
            super(ID);
            this.effects = entityEffectPredicate;
        }

        public static Conditions create(EntityEffectPredicate entityEffectPredicate) {
            return new Conditions(entityEffectPredicate);
        }

        public boolean matches(ServerPlayerEntity serverPlayerEntity) {
            return this.effects.test(serverPlayerEntity);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("effects", this.effects.serialize());
            return jsonObject;
        }
    }
}

