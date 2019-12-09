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

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("durability"));
        NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject.get("delta"));
        return new Conditions(itemPredicate, intRange, intRange2);
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack, int damage) {
        this.test(player.getAdvancementTracker(), conditions -> conditions.matches(stack, damage));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final ItemPredicate item;
        private final NumberRange.IntRange durability;
        private final NumberRange.IntRange delta;

        public Conditions(ItemPredicate item, NumberRange.IntRange intRange, NumberRange.IntRange intRange2) {
            super(ID);
            this.item = item;
            this.durability = intRange;
            this.delta = intRange2;
        }

        public static Conditions create(ItemPredicate item, NumberRange.IntRange intRange) {
            return new Conditions(item, intRange, NumberRange.IntRange.ANY);
        }

        public boolean matches(ItemStack stack, int damage) {
            if (!this.item.test(stack)) {
                return false;
            }
            if (!this.durability.test(stack.getMaxDamage() - damage)) {
                return false;
            }
            return this.delta.test(stack.getDamage() - damage);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("item", this.item.toJson());
            jsonObject.add("durability", this.durability.toJson());
            jsonObject.add("delta", this.delta.toJson());
            return jsonObject;
        }
    }
}

