/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ThrownItemPickedUpByEntityCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("thrown_item_picked_up_by_entity");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        EntityPredicate.Extended extended2 = EntityPredicate.Extended.getInJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
        return new Conditions(extended, itemPredicate, extended2);
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack, Entity entity) {
        LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
        this.test(player, conditions -> conditions.test(player, stack, lootContext));
    }

    @Override
    protected /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final ItemPredicate item;
        private final EntityPredicate.Extended entity;

        public Conditions(EntityPredicate.Extended extended, ItemPredicate item, EntityPredicate.Extended entity) {
            super(ID, extended);
            this.item = item;
            this.entity = entity;
        }

        public static Conditions create(EntityPredicate.Extended extended, ItemPredicate.Builder builder, EntityPredicate.Extended extended2) {
            return new Conditions(extended, builder.build(), extended2);
        }

        public boolean test(ServerPlayerEntity player, ItemStack stack, LootContext lootContext) {
            if (!this.item.test(stack)) {
                return false;
            }
            return this.entity.test(lootContext);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("item", this.item.toJson());
            jsonObject.add("entity", this.entity.toJson(predicateSerializer));
            return jsonObject;
        }
    }
}

