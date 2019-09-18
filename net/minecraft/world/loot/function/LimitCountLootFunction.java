/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.class_4570;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.function.ConditionalLootFunction;

public class LimitCountLootFunction
extends ConditionalLootFunction {
    private final BoundedIntUnaryOperator limit;

    private LimitCountLootFunction(class_4570[] args, BoundedIntUnaryOperator boundedIntUnaryOperator) {
        super(args);
        this.limit = boundedIntUnaryOperator;
    }

    @Override
    public ItemStack process(ItemStack itemStack, LootContext lootContext) {
        int i = this.limit.applyAsInt(itemStack.getCount());
        itemStack.setCount(i);
        return itemStack;
    }

    public static ConditionalLootFunction.Builder<?> builder(BoundedIntUnaryOperator boundedIntUnaryOperator) {
        return LimitCountLootFunction.builder((class_4570[] args) -> new LimitCountLootFunction((class_4570[])args, boundedIntUnaryOperator));
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<LimitCountLootFunction> {
        protected Factory() {
            super(new Identifier("limit_count"), LimitCountLootFunction.class);
        }

        public void method_510(JsonObject jsonObject, LimitCountLootFunction limitCountLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.method_529(jsonObject, limitCountLootFunction, jsonSerializationContext);
            jsonObject.add("limit", jsonSerializationContext.serialize(limitCountLootFunction.limit));
        }

        public LimitCountLootFunction method_509(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_4570[] args) {
            BoundedIntUnaryOperator boundedIntUnaryOperator = JsonHelper.deserialize(jsonObject, "limit", jsonDeserializationContext, BoundedIntUnaryOperator.class);
            return new LimitCountLootFunction(args, boundedIntUnaryOperator);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_4570[] args) {
            return this.method_509(jsonObject, jsonDeserializationContext, args);
        }
    }
}

