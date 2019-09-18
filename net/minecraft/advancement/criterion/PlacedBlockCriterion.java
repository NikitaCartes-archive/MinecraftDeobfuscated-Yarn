/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.class_4558;
import net.minecraft.class_4559;
import net.minecraft.item.ItemStack;
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
extends class_4558<Conditions> {
    private static final Identifier ID = new Identifier("placed_block");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_9088(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        Block block = PlacedBlockCriterion.method_22492(jsonObject);
        class_4559 lv = class_4559.method_22519(jsonObject.get("state"));
        if (block != null) {
            lv.method_22516(block.getStateFactory(), string -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + string + ":");
            });
        }
        LocationPredicate locationPredicate = LocationPredicate.deserialize(jsonObject.get("location"));
        ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
        return new Conditions(block, lv, locationPredicate, itemPredicate);
    }

    @Nullable
    private static Block method_22492(JsonObject jsonObject) {
        if (jsonObject.has("block")) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
            return (Block)Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
        }
        return null;
    }

    public void handle(ServerPlayerEntity serverPlayerEntity, BlockPos blockPos, ItemStack itemStack) {
        BlockState blockState = serverPlayerEntity.getServerWorld().getBlockState(blockPos);
        this.method_22510(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(blockState, blockPos, serverPlayerEntity.getServerWorld(), itemStack));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_9088(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Block block;
        private final class_4559 state;
        private final LocationPredicate location;
        private final ItemPredicate item;

        public Conditions(@Nullable Block block, class_4559 arg, LocationPredicate locationPredicate, ItemPredicate itemPredicate) {
            super(ID);
            this.block = block;
            this.state = arg;
            this.location = locationPredicate;
            this.item = itemPredicate;
        }

        public static Conditions block(Block block) {
            return new Conditions(block, class_4559.field_20736, LocationPredicate.ANY, ItemPredicate.ANY);
        }

        public boolean matches(BlockState blockState, BlockPos blockPos, ServerWorld serverWorld, ItemStack itemStack) {
            if (this.block != null && blockState.getBlock() != this.block) {
                return false;
            }
            if (!this.state.method_22514(blockState)) {
                return false;
            }
            if (!this.location.test(serverWorld, blockPos.getX(), blockPos.getY(), blockPos.getZ())) {
                return false;
            }
            return this.item.test(itemStack);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            if (this.block != null) {
                jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
            }
            jsonObject.add("state", this.state.method_22513());
            jsonObject.add("location", this.location.serialize());
            jsonObject.add("item", this.item.serialize());
            return jsonObject;
        }
    }
}

