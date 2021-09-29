/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class PlacedBlockCriterion
extends AbstractCriterion<Conditions> {
    static final Identifier ID = new Identifier("placed_block");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        Block block = PlacedBlockCriterion.getBlock(jsonObject);
        StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
        if (block != null) {
            statePredicate.check(block.getStateManager(), name -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + name + ":");
            });
        }
        LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("location"));
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(extended, block, statePredicate, locationPredicate, itemPredicate);
    }

    @Nullable
    private static Block getBlock(JsonObject obj) {
        if (obj.has("block")) {
            Identifier identifier = new Identifier(JsonHelper.getString(obj, "block"));
            return Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
        }
        return null;
    }

    public void trigger(ServerPlayerEntity player, BlockPos blockPos, ItemStack stack) {
        BlockState blockState = player.getWorld().getBlockState(blockPos);
        this.trigger(player, conditions -> conditions.matches(blockState, blockPos, player.getWorld(), stack));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        @Nullable
        private final Block block;
        private final StatePredicate state;
        private final LocationPredicate location;
        private final ItemPredicate item;

        public Conditions(EntityPredicate.Extended player, @Nullable Block block, StatePredicate state, LocationPredicate location, ItemPredicate item) {
            super(ID, player);
            this.block = block;
            this.state = state;
            this.location = location;
            this.item = item;
        }

        public static Conditions block(Block block) {
            return new Conditions(EntityPredicate.Extended.EMPTY, block, StatePredicate.ANY, LocationPredicate.ANY, ItemPredicate.ANY);
        }

        public boolean matches(BlockState state, BlockPos pos, ServerWorld world, ItemStack stack) {
            if (this.block != null && !state.isOf(this.block)) {
                return false;
            }
            if (!this.state.test(state)) {
                return false;
            }
            if (!this.location.test(world, pos.getX(), pos.getY(), pos.getZ())) {
                return false;
            }
            return this.item.test(stack);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            if (this.block != null) {
                jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
            }
            jsonObject.add("state", this.state.toJson());
            jsonObject.add("location", this.location.toJson());
            jsonObject.add("item", this.item.toJson());
            return jsonObject;
        }
    }
}

