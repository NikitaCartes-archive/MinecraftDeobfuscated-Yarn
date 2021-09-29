/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class VillagerTradeCriterion
extends AbstractCriterion<Conditions> {
    static final Identifier ID = new Identifier("villager_trade");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        EntityPredicate.Extended extended2 = EntityPredicate.Extended.getInJson(jsonObject, "villager", advancementEntityPredicateDeserializer);
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(extended, extended2, itemPredicate);
    }

    public void trigger(ServerPlayerEntity player, MerchantEntity merchant, ItemStack stack) {
        LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, merchant);
        this.trigger(player, conditions -> conditions.matches(lootContext, stack));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate.Extended villager;
        private final ItemPredicate item;

        public Conditions(EntityPredicate.Extended player, EntityPredicate.Extended villager, ItemPredicate item) {
            super(ID, player);
            this.villager = villager;
            this.item = item;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, ItemPredicate.ANY);
        }

        public static Conditions method_38914(EntityPredicate.Builder builder) {
            return new Conditions(EntityPredicate.Extended.ofLegacy(builder.build()), EntityPredicate.Extended.EMPTY, ItemPredicate.ANY);
        }

        public boolean matches(LootContext merchantContext, ItemStack stack) {
            if (!this.villager.test(merchantContext)) {
                return false;
            }
            return this.item.test(stack);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("item", this.item.toJson());
            jsonObject.add("villager", this.villager.toJson(predicateSerializer));
            return jsonObject;
        }
    }
}

