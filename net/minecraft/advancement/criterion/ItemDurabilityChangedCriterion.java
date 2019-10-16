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
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ItemDurabilityChangedCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("item_durability_changed");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_8962(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("durability"));
        NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject.get("delta"));
        return new Conditions(itemPredicate, intRange, intRange2);
    }

    public void trigger(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, int i) {
        this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(itemStack, i));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_8962(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final ItemPredicate item;
        private final NumberRange.IntRange durability;
        private final NumberRange.IntRange delta;

        public Conditions(ItemPredicate itemPredicate, NumberRange.IntRange intRange, NumberRange.IntRange intRange2) {
            super(ID);
            this.item = itemPredicate;
            this.durability = intRange;
            this.delta = intRange2;
        }

        public static Conditions create(ItemPredicate itemPredicate, NumberRange.IntRange intRange) {
            return new Conditions(itemPredicate, intRange, NumberRange.IntRange.ANY);
        }

        public boolean matches(ItemStack itemStack, int i) {
            if (!this.item.test(itemStack)) {
                return false;
            }
            if (!this.durability.test(itemStack.getMaxDamage() - i)) {
                return false;
            }
            return this.delta.test(itemStack.getDamage() - i);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("item", this.item.serialize());
            jsonObject.add("durability", this.durability.toJson());
            jsonObject.add("delta", this.delta.toJson());
            return jsonObject;
        }
    }
}

