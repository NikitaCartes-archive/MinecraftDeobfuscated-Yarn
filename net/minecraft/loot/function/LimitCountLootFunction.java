/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.util.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LimitCountLootFunction
extends ConditionalLootFunction {
    private final BoundedIntUnaryOperator limit;

    private LimitCountLootFunction(LootCondition[] lootConditions, BoundedIntUnaryOperator boundedIntUnaryOperator) {
        super(lootConditions);
        this.limit = boundedIntUnaryOperator;
    }

    @Override
    public ItemStack process(ItemStack itemStack, LootContext lootContext) {
        int i = this.limit.applyAsInt(itemStack.getCount());
        itemStack.setCount(i);
        return itemStack;
    }

    public static ConditionalLootFunction.Builder<?> builder(BoundedIntUnaryOperator boundedIntUnaryOperator) {
        return LimitCountLootFunction.builder((LootCondition[] lootConditions) -> new LimitCountLootFunction((LootCondition[])lootConditions, boundedIntUnaryOperator));
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<LimitCountLootFunction> {
        protected Factory() {
            super(new Identifier("limit_count"), LimitCountLootFunction.class);
        }

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
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return this.fromJson(jsonObject, jsonDeserializationContext, lootConditions);
        }
    }
}

