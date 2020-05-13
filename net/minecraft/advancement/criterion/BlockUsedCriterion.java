/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class BlockUsedCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier id = new Identifier("item_used_on_block");

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("location"));
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(extended, locationPredicate, itemPredicate);
    }

    public void test(ServerPlayerEntity player, BlockPos pos, ItemStack stack) {
        BlockState blockState = player.getServerWorld().getBlockState(pos);
        this.test(player, conditions -> conditions.test(blockState, player.getServerWorld(), pos, stack));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final LocationPredicate field_24495;
        private final ItemPredicate item;

        public Conditions(EntityPredicate.Extended extended, LocationPredicate locationPredicate, ItemPredicate itemPredicate) {
            super(id, extended);
            this.field_24495 = locationPredicate;
            this.item = itemPredicate;
        }

        public static Conditions method_27981(LocationPredicate.Builder builder, ItemPredicate.Builder builder2) {
            return new Conditions(EntityPredicate.Extended.EMPTY, builder.build(), builder2.build());
        }

        public boolean test(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
            if (!this.field_24495.test(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5)) {
                return false;
            }
            return this.item.test(stack);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("location", this.field_24495.toJson());
            jsonObject.add("item", this.item.toJson());
            return jsonObject;
        }
    }
}

