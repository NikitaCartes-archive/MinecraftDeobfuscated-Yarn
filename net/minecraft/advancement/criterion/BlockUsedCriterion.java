/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class BlockUsedCriterion
extends AbstractCriterion<Conditions> {
    private final Identifier id;

    public BlockUsedCriterion(Identifier id) {
        this.id = id;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        BlockPredicate blockPredicate = BlockPredicate.fromJson(jsonObject.get("block"));
        StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(this.id, blockPredicate, statePredicate, itemPredicate);
    }

    public void test(ServerPlayerEntity player, BlockPos pos, ItemStack stack) {
        BlockState blockState = player.getServerWorld().getBlockState(pos);
        this.test(player.getAdvancementTracker(), conditions -> conditions.test(blockState, player.getServerWorld(), pos, stack));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return this.conditionsFromJson(obj, context);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final BlockPredicate block;
        private final StatePredicate state;
        private final ItemPredicate item;

        public Conditions(Identifier id, BlockPredicate block, StatePredicate state, ItemPredicate item) {
            super(id);
            this.block = block;
            this.state = state;
            this.item = item;
        }

        public static Conditions create(BlockPredicate.Builder blockPredicateBuilder, ItemPredicate.Builder itemPredicateBuilder) {
            return new Conditions(Criteria.SAFELY_HARVEST_HONEY.id, blockPredicateBuilder.build(), StatePredicate.ANY, itemPredicateBuilder.build());
        }

        public boolean test(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
            if (!this.block.test(world, pos)) {
                return false;
            }
            if (!this.state.test(state)) {
                return false;
            }
            return this.item.test(stack);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("block", this.block.toJson());
            jsonObject.add("state", this.state.toJson());
            jsonObject.add("item", this.item.toJson());
            return jsonObject;
        }
    }
}

