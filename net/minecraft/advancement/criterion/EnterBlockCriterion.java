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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class EnterBlockCriterion
extends class_4558<Conditions> {
    private static final Identifier ID = new Identifier("enter_block");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_8883(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        Block block = EnterBlockCriterion.method_22466(jsonObject);
        class_4559 lv = class_4559.method_22519(jsonObject.get("state"));
        if (block != null) {
            lv.method_22516(block.getStateFactory(), string -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + string);
            });
        }
        return new Conditions(block, lv);
    }

    @Nullable
    private static Block method_22466(JsonObject jsonObject) {
        if (jsonObject.has("block")) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
            return (Block)Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
        }
        return null;
    }

    public void handle(ServerPlayerEntity serverPlayerEntity, BlockState blockState) {
        this.method_22510(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(blockState));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_8883(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Block block;
        private final class_4559 state;

        public Conditions(@Nullable Block block, class_4559 arg) {
            super(ID);
            this.block = block;
            this.state = arg;
        }

        public static Conditions block(Block block) {
            return new Conditions(block, class_4559.field_20736);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            if (this.block != null) {
                jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
            }
            jsonObject.add("state", this.state.method_22513());
            return jsonObject;
        }

        public boolean matches(BlockState blockState) {
            if (this.block != null && blockState.getBlock() != this.block) {
                return false;
            }
            return this.state.method_22514(blockState);
        }
    }
}

