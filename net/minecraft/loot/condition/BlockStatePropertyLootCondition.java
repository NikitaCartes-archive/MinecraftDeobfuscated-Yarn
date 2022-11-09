/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class BlockStatePropertyLootCondition
implements LootCondition {
    final Block block;
    final StatePredicate properties;

    BlockStatePropertyLootCondition(Block block, StatePredicate properties) {
        this.block = block;
        this.properties = properties;
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.BLOCK_STATE_PROPERTY;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.BLOCK_STATE);
    }

    @Override
    public boolean test(LootContext lootContext) {
        BlockState blockState = lootContext.get(LootContextParameters.BLOCK_STATE);
        return blockState != null && blockState.isOf(this.block) && this.properties.test(blockState);
    }

    public static Builder builder(Block block) {
        return new Builder(block);
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Builder
    implements LootCondition.Builder {
        private final Block block;
        private StatePredicate propertyValues = StatePredicate.ANY;

        public Builder(Block block) {
            this.block = block;
        }

        public Builder properties(StatePredicate.Builder builder) {
            this.propertyValues = builder.build();
            return this;
        }

        @Override
        public LootCondition build() {
            return new BlockStatePropertyLootCondition(this.block, this.propertyValues);
        }
    }

    public static class Serializer
    implements JsonSerializer<BlockStatePropertyLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, BlockStatePropertyLootCondition blockStatePropertyLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("block", Registries.BLOCK.getId(blockStatePropertyLootCondition.block).toString());
            jsonObject.add("properties", blockStatePropertyLootCondition.properties.toJson());
        }

        @Override
        public BlockStatePropertyLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
            Block block = (Block)Registries.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new IllegalArgumentException("Can't find block " + identifier));
            StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("properties"));
            statePredicate.check(block.getStateManager(), propertyName -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + propertyName);
            });
            return new BlockStatePropertyLootCondition(block, statePredicate);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}

