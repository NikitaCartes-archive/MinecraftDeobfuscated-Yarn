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

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("entity"));
        return new Conditions(entityPredicate);
    }

    public void trigger(ServerPlayerEntity player, Entity entity) {
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

        public static Conditions create(EntityPredicate.Builder builder) {
            return new Conditions(builder.build());
        }

        public boolean matches(ServerPlayerEntity player, Entity entity) {
            return this.entity.test(player, entity);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("entity", this.entity.toJson());
            return jsonObject;
        }
    }
}

