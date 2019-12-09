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
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CuredZombieVillagerCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("cured_zombie_villager");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("zombie"));
        EntityPredicate entityPredicate2 = EntityPredicate.fromJson(jsonObject.get("villager"));
        return new Conditions(entityPredicate, entityPredicate2);
    }

    public void trigger(ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager) {
        this.test(player.getAdvancementTracker(), conditions -> conditions.matches(player, zombie, villager));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate zombie;
        private final EntityPredicate villager;

        public Conditions(EntityPredicate zombie, EntityPredicate villager) {
            super(ID);
            this.zombie = zombie;
            this.villager = villager;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.ANY, EntityPredicate.ANY);
        }

        public boolean matches(ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager) {
            if (!this.zombie.test(player, zombie)) {
                return false;
            }
            return this.villager.test(player, villager);
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

