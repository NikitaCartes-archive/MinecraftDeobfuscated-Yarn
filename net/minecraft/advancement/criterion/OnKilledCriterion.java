/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class OnKilledCriterion
extends AbstractCriterion<Conditions> {
    final Identifier id;

    public OnKilledCriterion(Identifier id) {
        this.id = id;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        return new Conditions(this.id, extended, EntityPredicate.Extended.getInJson(jsonObject, "entity", advancementEntityPredicateDeserializer), DamageSourcePredicate.fromJson(jsonObject.get("killing_blow")));
    }

    public void trigger(ServerPlayerEntity player, Entity entity, DamageSource killingDamage) {
        LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
        this.test(player, conditions -> conditions.test(player, lootContext, killingDamage));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate.Extended entity;
        private final DamageSourcePredicate killingBlow;

        public Conditions(Identifier id, EntityPredicate.Extended player, EntityPredicate.Extended entity, DamageSourcePredicate killingBlow) {
            super(id, player);
            this.entity = entity;
            this.killingBlow = killingBlow;
        }

        public static Conditions createPlayerKilledEntity(EntityPredicate killedEntityPredicate) {
            return new Conditions(Criteria.PLAYER_KILLED_ENTITY.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(killedEntityPredicate), DamageSourcePredicate.EMPTY);
        }

        public static Conditions createPlayerKilledEntity(EntityPredicate.Builder killedEntityPredicateBuilder) {
            return new Conditions(Criteria.PLAYER_KILLED_ENTITY.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(killedEntityPredicateBuilder.build()), DamageSourcePredicate.EMPTY);
        }

        public static Conditions createPlayerKilledEntity() {
            return new Conditions(Criteria.PLAYER_KILLED_ENTITY.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, DamageSourcePredicate.EMPTY);
        }

        public static Conditions createPlayerKilledEntity(EntityPredicate killedEntityPredicate, DamageSourcePredicate damageSourcePredicate) {
            return new Conditions(Criteria.PLAYER_KILLED_ENTITY.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(killedEntityPredicate), damageSourcePredicate);
        }

        public static Conditions createPlayerKilledEntity(EntityPredicate.Builder killedEntityPredicateBuilder, DamageSourcePredicate damageSourcePredicate) {
            return new Conditions(Criteria.PLAYER_KILLED_ENTITY.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(killedEntityPredicateBuilder.build()), damageSourcePredicate);
        }

        public static Conditions createPlayerKilledEntity(EntityPredicate killedEntityPredicate, DamageSourcePredicate.Builder damageSourcePredicateBuilder) {
            return new Conditions(Criteria.PLAYER_KILLED_ENTITY.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(killedEntityPredicate), damageSourcePredicateBuilder.build());
        }

        public static Conditions createPlayerKilledEntity(EntityPredicate.Builder killedEntityPredicateBuilder, DamageSourcePredicate.Builder killingBlowBuilder) {
            return new Conditions(Criteria.PLAYER_KILLED_ENTITY.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(killedEntityPredicateBuilder.build()), killingBlowBuilder.build());
        }

        public static Conditions createEntityKilledPlayer(EntityPredicate killerEntityPredicate) {
            return new Conditions(Criteria.ENTITY_KILLED_PLAYER.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(killerEntityPredicate), DamageSourcePredicate.EMPTY);
        }

        public static Conditions createEntityKilledPlayer(EntityPredicate.Builder killerEntityPredicateBuilder) {
            return new Conditions(Criteria.ENTITY_KILLED_PLAYER.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(killerEntityPredicateBuilder.build()), DamageSourcePredicate.EMPTY);
        }

        public static Conditions createEntityKilledPlayer() {
            return new Conditions(Criteria.ENTITY_KILLED_PLAYER.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, DamageSourcePredicate.EMPTY);
        }

        public static Conditions createEntityKilledPlayer(EntityPredicate killerEntityPredicate, DamageSourcePredicate damageSourcePredicate) {
            return new Conditions(Criteria.ENTITY_KILLED_PLAYER.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(killerEntityPredicate), damageSourcePredicate);
        }

        public static Conditions createEntityKilledPlayer(EntityPredicate.Builder killerEntityPredicateBuilder, DamageSourcePredicate damageSourcePredicate) {
            return new Conditions(Criteria.ENTITY_KILLED_PLAYER.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(killerEntityPredicateBuilder.build()), damageSourcePredicate);
        }

        public static Conditions createEntityKilledPlayer(EntityPredicate killerEntityPredicate, DamageSourcePredicate.Builder damageSourcePredicateBuilder) {
            return new Conditions(Criteria.ENTITY_KILLED_PLAYER.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(killerEntityPredicate), damageSourcePredicateBuilder.build());
        }

        public static Conditions createEntityKilledPlayer(EntityPredicate.Builder killerEntityPredicateBuilder, DamageSourcePredicate.Builder damageSourcePredicateBuilder) {
            return new Conditions(Criteria.ENTITY_KILLED_PLAYER.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(killerEntityPredicateBuilder.build()), damageSourcePredicateBuilder.build());
        }

        public boolean test(ServerPlayerEntity player, LootContext killedEntityContext, DamageSource killingBlow) {
            if (!this.killingBlow.test(player, killingBlow)) {
                return false;
            }
            return this.entity.test(killedEntityContext);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("entity", this.entity.toJson(predicateSerializer));
            jsonObject.add("killing_blow", this.killingBlow.toJson());
            return jsonObject;
        }
    }
}

