/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.math.random.Random;

public class ExplosionDecayLootFunction
extends ConditionalLootFunction {
    ExplosionDecayLootFunction(LootCondition[] lootConditions) {
        super(lootConditions);
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.EXPLOSION_DECAY;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        Float float_ = context.get(LootContextParameters.EXPLOSION_RADIUS);
        if (float_ != null) {
            Random random = context.getRandom();
            float f = 1.0f / float_.floatValue();
            int i = stack.getCount();
            int j = 0;
            for (int k = 0; k < i; ++k) {
                if (!(random.nextFloat() <= f)) continue;
                ++j;
            }
            stack.setCount(j);
        }
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return ExplosionDecayLootFunction.builder(ExplosionDecayLootFunction::new);
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<ExplosionDecayLootFunction> {
        @Override
        public ExplosionDecayLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return new ExplosionDecayLootFunction(lootConditions);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

