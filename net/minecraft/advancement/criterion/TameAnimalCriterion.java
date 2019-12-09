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
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TameAnimalCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("tame_animal");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("entity"));
        return new Conditions(entityPredicate);
    }

    public void trigger(ServerPlayerEntity player, AnimalEntity entity) {
        this.test(player.getAdvancementTracker(), conditions -> conditions.matches(player, entity));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate entity;

        public Conditions(EntityPredicate entity) {
            super(ID);
            this.entity = entity;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.ANY);
        }

        public static Conditions create(EntityPredicate entity) {
            return new Conditions(entity);
        }

        public boolean matches(ServerPlayerEntity player, AnimalEntity entity) {
            return this.entity.test(player, entity);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("entity", this.entity.serialize());
            return jsonObject;
        }
    }
}

