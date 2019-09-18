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
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class OnKilledCriterion
extends AbstractCriterion<Conditions> {
    private final Identifier id;

    public OnKilledCriterion(Identifier identifier) {
        this.id = identifier;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    public Conditions method_8989(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return new Conditions(this.id, EntityPredicate.fromJson(jsonObject.get("entity")), DamageSourcePredicate.deserialize(jsonObject.get("killing_blow")));
    }

    public void handle(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource) {
        this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.test(serverPlayerEntity, entity, damageSource));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_8989(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate entity;
        private final DamageSourcePredicate killingBlow;

        public Conditions(Identifier identifier, EntityPredicate entityPredicate, DamageSourcePredicate damageSourcePredicate) {
            super(identifier);
            this.entity = entityPredicate;
            this.killingBlow = damageSourcePredicate;
        }

        public static Conditions createPlayerKilledEntity(EntityPredicate.Builder builder) {
            return new Conditions(Criterions.PLAYER_KILLED_ENTITY.id, builder.build(), DamageSourcePredicate.EMPTY);
        }

        public static Conditions createPlayerKilledEntity() {
            return new Conditions(Criterions.PLAYER_KILLED_ENTITY.id, EntityPredicate.ANY, DamageSourcePredicate.EMPTY);
        }

        public static Conditions createPlayerKilledEntity(EntityPredicate.Builder builder, DamageSourcePredicate.Builder builder2) {
            return new Conditions(Criterions.PLAYER_KILLED_ENTITY.id, builder.build(), builder2.build());
        }

        public static Conditions createEntityKilledPlayer() {
            return new Conditions(Criterions.ENTITY_KILLED_PLAYER.id, EntityPredicate.ANY, DamageSourcePredicate.EMPTY);
        }

        public boolean test(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource) {
            if (!this.killingBlow.test(serverPlayerEntity, damageSource)) {
                return false;
            }
            return this.entity.test(serverPlayerEntity, entity);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("entity", this.entity.serialize());
            jsonObject.add("killing_blow", this.killingBlow.serialize());
            return jsonObject;
        }
    }
}

