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
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class VillagerTradeCriterion
extends class_4558<Conditions> {
    private static final Identifier ID = new Identifier("villager_trade");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_9148(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("villager"));
        ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
        return new Conditions(entityPredicate, itemPredicate);
    }

    public void handle(ServerPlayerEntity serverPlayerEntity, AbstractTraderEntity abstractTraderEntity, ItemStack itemStack) {
        this.method_22510(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(serverPlayerEntity, abstractTraderEntity, itemStack));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_9148(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate item;
        private final ItemPredicate villager;

        public Conditions(EntityPredicate entityPredicate, ItemPredicate itemPredicate) {
            super(ID);
            this.item = entityPredicate;
            this.villager = itemPredicate;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.ANY, ItemPredicate.ANY);
        }

        public boolean matches(ServerPlayerEntity serverPlayerEntity, AbstractTraderEntity abstractTraderEntity, ItemStack itemStack) {
            if (!this.item.test(serverPlayerEntity, abstractTraderEntity)) {
                return false;
            }
            return this.villager.test(itemStack);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("item", this.villager.serialize());
            jsonObject.add("villager", this.item.serialize());
            return jsonObject;
        }
    }
}

