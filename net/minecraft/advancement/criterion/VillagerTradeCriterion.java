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
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class VillagerTradeCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("villager_trade");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_9148(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("villager"));
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(entityPredicate, itemPredicate);
    }

    public void handle(ServerPlayerEntity serverPlayerEntity, AbstractTraderEntity abstractTraderEntity, ItemStack itemStack) {
        this.test(serverPlayerEntity.getAdvancementTracker(), conditions -> conditions.matches(serverPlayerEntity, abstractTraderEntity, itemStack));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_9148(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate villager;
        private final ItemPredicate item;

        public Conditions(EntityPredicate entityPredicate, ItemPredicate itemPredicate) {
            super(ID);
            this.villager = entityPredicate;
            this.item = itemPredicate;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.ANY, ItemPredicate.ANY);
        }

        public boolean matches(ServerPlayerEntity serverPlayerEntity, AbstractTraderEntity abstractTraderEntity, ItemStack itemStack) {
            if (!this.villager.test(serverPlayerEntity, abstractTraderEntity)) {
                return false;
            }
            return this.item.test(itemStack);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("item", this.item.toJson());
            jsonObject.add("villager", this.villager.serialize());
            return jsonObject;
        }
    }
}

