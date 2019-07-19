/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.condition.LootCondition;

public class ExplosionDecayLootFunction
extends ConditionalLootFunction {
    private ExplosionDecayLootFunction(LootCondition[] lootConditions) {
        super(lootConditions);
    }

    @Override
    public ItemStack process(ItemStack itemStack, LootContext lootContext) {
        Float float_ = lootContext.get(LootContextParameters.EXPLOSION_RADIUS);
        if (float_ != null) {
            Random random = lootContext.getRandom();
            float f = 1.0f / float_.floatValue();
            int i = itemStack.getCount();
            int j = 0;
            for (int k = 0; k < i; ++k) {
                if (!(random.nextFloat() <= f)) continue;
                ++j;
            }
            itemStack.setCount(j);
        }
        return itemStack;
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return ExplosionDecayLootFunction.builder(ExplosionDecayLootFunction::new);
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<ExplosionDecayLootFunction> {
        protected Factory() {
            super(new Identifier("explosion_decay"), ExplosionDecayLootFunction.class);
        }

        @Override
        public ExplosionDecayLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return new ExplosionDecayLootFunction(lootConditions);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return this.fromJson(jsonObject, jsonDeserializationContext, lootConditions);
        }
    }
}

