/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.util.JsonHelper;

public class LimitCountLootFunction
extends ConditionalLootFunction {
    final BoundedIntUnaryOperator limit;

    LimitCountLootFunction(LootCondition[] lootConditions, BoundedIntUnaryOperator boundedIntUnaryOperator) {
        super(lootConditions);
        this.limit = boundedIntUnaryOperator;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.LIMIT_COUNT;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return this.limit.getRequiredParameters();
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        int i = this.limit.apply(context, stack.getCount());
        stack.setCount(i);
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder(BoundedIntUnaryOperator limit) {
        return LimitCountLootFunction.builder((LootCondition[] conditions) -> new LimitCountLootFunction((LootCondition[])conditions, limit));
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<LimitCountLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, LimitCountLootFunction limitCountLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, limitCountLootFunction, jsonSerializationContext);
            jsonObject.add("limit", jsonSerializationContext.serialize(limitCountLootFunction.limit));
        }

        @Override
        public LimitCountLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            BoundedIntUnaryOperator boundedIntUnaryOperator = JsonHelper.deserialize(jsonObject, "limit", jsonDeserializationContext, BoundedIntUnaryOperator.class);
            return new LimitCountLootFunction(lootConditions, boundedIntUnaryOperator);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

