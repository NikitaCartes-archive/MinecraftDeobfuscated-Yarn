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
import net.minecraft.advancement.criterion.Criterions;
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

    public BlockUsedCriterion(Identifier identifier) {
        this.id = identifier;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    public Conditions method_23890(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        BlockPredicate blockPredicate = BlockPredicate.fromJson(jsonObject.get("block"));
        StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(this.id, blockPredicate, statePredicate, itemPredicate);
    }

    public void test(ServerPlayerEntity serverPlayerEntity, BlockPos blockPos, ItemStack itemStack) {
        BlockState blockState = serverPlayerEntity.getServerWorld().getBlockState(blockPos);
        this.test(serverPlayerEntity.getAdvancementTracker(), conditions -> conditions.test(blockState, serverPlayerEntity.getServerWorld(), blockPos, itemStack));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_23890(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final BlockPredicate block;
        private final StatePredicate state;
        private final ItemPredicate item;

        public Conditions(Identifier identifier, BlockPredicate blockPredicate, StatePredicate statePredicate, ItemPredicate itemPredicate) {
            super(identifier);
            this.block = blockPredicate;
            this.state = statePredicate;
            this.item = itemPredicate;
        }

        public static Conditions create(BlockPredicate.Builder builder, ItemPredicate.Builder builder2) {
            return new Conditions(Criterions.SAFELY_HARVEST_HONEY.id, builder.build(), StatePredicate.ANY, builder2.build());
        }

        public boolean test(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, ItemStack itemStack) {
            if (!this.block.test(serverWorld, blockPos)) {
                return false;
            }
            if (!this.state.test(blockState)) {
                return false;
            }
            return this.item.test(itemStack);
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

