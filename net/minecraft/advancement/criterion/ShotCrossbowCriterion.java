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
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ShotCrossbowCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("shot_crossbow");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_9114(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
        return new Conditions(itemPredicate);
    }

    public void trigger(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
        this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(itemStack));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_9114(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final ItemPredicate item;

        public Conditions(ItemPredicate itemPredicate) {
            super(ID);
            this.item = itemPredicate;
        }

        public static Conditions create(ItemConvertible itemConvertible) {
            return new Conditions(ItemPredicate.Builder.create().item(itemConvertible).build());
        }

        public boolean matches(ItemStack itemStack) {
            return this.item.test(itemStack);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("item", this.item.serialize());
            return jsonObject;
        }
    }
}

