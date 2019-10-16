/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class EnterBlockCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("enter_block");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_8883(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        Block block = EnterBlockCriterion.getBlock(jsonObject);
        StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
        if (block != null) {
            statePredicate.check(block.getStateFactory(), string -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + string);
            });
        }
        return new Conditions(block, statePredicate);
    }

    @Nullable
    private static Block getBlock(JsonObject jsonObject) {
        if (jsonObject.has("block")) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
            return (Block)Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
        }
        return null;
    }

    public void trigger(ServerPlayerEntity serverPlayerEntity, BlockState blockState) {
        this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(blockState));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_8883(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Block block;
        private final StatePredicate state;

        public Conditions(@Nullable Block block, StatePredicate statePredicate) {
            super(ID);
            this.block = block;
            this.state = statePredicate;
        }

        public static Conditions block(Block block) {
            return new Conditions(block, StatePredicate.ANY);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            if (this.block != null) {
                jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
            }
            jsonObject.add("state", this.state.toJson());
            return jsonObject;
        }

        public boolean matches(BlockState blockState) {
            if (this.block != null && blockState.getBlock() != this.block) {
                return false;
            }
            return this.state.test(blockState);
        }
    }
}

