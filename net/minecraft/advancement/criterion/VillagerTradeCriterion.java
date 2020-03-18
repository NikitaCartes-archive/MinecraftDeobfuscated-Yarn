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

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("villager"));
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(entityPredicate, itemPredicate);
    }

    public void handle(ServerPlayerEntity player, AbstractTraderEntity trader, ItemStack stack) {
        this.test(player.getAdvancementTracker(), conditions -> conditions.matches(player, trader, stack));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate villager;
        private final ItemPredicate item;

        public Conditions(EntityPredicate entity, ItemPredicate item) {
            super(ID);
            this.villager = entity;
            this.item = item;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.ANY, ItemPredicate.ANY);
        }

        public boolean matches(ServerPlayerEntity player, AbstractTraderEntity trader, ItemStack stack) {
            if (!this.villager.test(player, trader)) {
                return false;
            }
            return this.item.test(stack);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("item", this.item.toJson());
            jsonObject.add("villager", this.villager.toJson());
            return jsonObject;
        }
    }
}

