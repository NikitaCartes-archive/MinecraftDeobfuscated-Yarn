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
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SummonedEntityCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("summoned_entity");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_9123(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("entity"));
        return new Conditions(entityPredicate);
    }

    public void trigger(ServerPlayerEntity serverPlayerEntity, Entity entity) {
        this.test(serverPlayerEntity.getAdvancementTracker(), conditions -> conditions.matches(serverPlayerEntity, entity));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_9123(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate entity;

        public Conditions(EntityPredicate entityPredicate) {
            super(ID);
            this.entity = entityPredicate;
        }

        public static Conditions create(EntityPredicate.Builder builder) {
            return new Conditions(builder.build());
        }

        public boolean matches(ServerPlayerEntity serverPlayerEntity, Entity entity) {
            return this.entity.test(serverPlayerEntity, entity);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("entity", this.entity.serialize());
            return jsonObject;
        }
    }
}

