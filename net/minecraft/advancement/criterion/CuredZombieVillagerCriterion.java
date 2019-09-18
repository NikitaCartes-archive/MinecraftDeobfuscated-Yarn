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
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CuredZombieVillagerCriterion
extends class_4558<Conditions> {
    private static final Identifier ID = new Identifier("cured_zombie_villager");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_8830(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("zombie"));
        EntityPredicate entityPredicate2 = EntityPredicate.deserialize(jsonObject.get("villager"));
        return new Conditions(entityPredicate, entityPredicate2);
    }

    public void handle(ServerPlayerEntity serverPlayerEntity, ZombieEntity zombieEntity, VillagerEntity villagerEntity) {
        this.method_22510(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(serverPlayerEntity, zombieEntity, villagerEntity));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_8830(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate zombie;
        private final EntityPredicate villager;

        public Conditions(EntityPredicate entityPredicate, EntityPredicate entityPredicate2) {
            super(ID);
            this.zombie = entityPredicate;
            this.villager = entityPredicate2;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.ANY, EntityPredicate.ANY);
        }

        public boolean matches(ServerPlayerEntity serverPlayerEntity, ZombieEntity zombieEntity, VillagerEntity villagerEntity) {
            if (!this.zombie.test(serverPlayerEntity, zombieEntity)) {
                return false;
            }
            return this.villager.test(serverPlayerEntity, villagerEntity);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("zombie", this.zombie.serialize());
            jsonObject.add("villager", this.villager.serialize());
            return jsonObject;
        }
    }
}

