/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.class_4558;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TameAnimalCriterion
extends class_4558<Conditions> {
    private static final Identifier ID = new Identifier("tame_animal");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_9133(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("entity"));
        return new Conditions(entityPredicate);
    }

    public void handle(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity) {
        this.method_22510(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(serverPlayerEntity, animalEntity));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_9133(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate entity;

        public Conditions(EntityPredicate entityPredicate) {
            super(ID);
            this.entity = entityPredicate;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.ANY);
        }

        public static Conditions create(EntityPredicate entityPredicate) {
            return new Conditions(entityPredicate);
        }

        public boolean matches(ServerPlayerEntity serverPlayerEntity, AnimalEntity animalEntity) {
            return this.entity.test(serverPlayerEntity, animalEntity);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("entity", this.entity.serialize());
            return jsonObject;
        }
    }
}

