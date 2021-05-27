/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ConsumeItemCriterion
extends AbstractCriterion<Conditions> {
    static final Identifier ID = new Identifier("consume_item");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        return new Conditions(extended, ItemPredicate.fromJson(jsonObject.get("item")));
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        this.test(player, conditions -> conditions.matches(stack));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final ItemPredicate item;

        public Conditions(EntityPredicate.Extended player, ItemPredicate item) {
            super(ID, player);
            this.item = item;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.Extended.EMPTY, ItemPredicate.ANY);
        }

        public static Conditions predicate(ItemPredicate predicate) {
            return new Conditions(EntityPredicate.Extended.EMPTY, predicate);
        }

        public static Conditions item(ItemConvertible item) {
            return new Conditions(EntityPredicate.Extended.EMPTY, new ItemPredicate(null, ImmutableSet.of(item.asItem()), NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, EnchantmentPredicate.ARRAY_OF_ANY, EnchantmentPredicate.ARRAY_OF_ANY, null, NbtPredicate.ANY));
        }

        public boolean matches(ItemStack stack) {
            return this.item.test(stack);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("item", this.item.toJson());
            return jsonObject;
        }
    }
}

