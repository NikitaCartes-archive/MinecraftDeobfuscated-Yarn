/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;

public class LootingEnchantLootFunction
extends ConditionalLootFunction {
    private final UniformLootTableRange countRange;
    private final int limit;

    private LootingEnchantLootFunction(LootCondition[] lootConditions, UniformLootTableRange uniformLootTableRange, int i) {
        super(lootConditions);
        this.countRange = uniformLootTableRange;
        this.limit = i;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.KILLER_ENTITY);
    }

    private boolean hasLimit() {
        return this.limit > 0;
    }

    @Override
    public ItemStack process(ItemStack itemStack, LootContext lootContext) {
        Entity entity = lootContext.get(LootContextParameters.KILLER_ENTITY);
        if (entity instanceof LivingEntity) {
            int i = EnchantmentHelper.getLooting((LivingEntity)entity);
            if (i == 0) {
                return itemStack;
            }
            float f = (float)i * this.countRange.nextFloat(lootContext.getRandom());
            itemStack.increment(Math.round(f));
            if (this.hasLimit() && itemStack.getCount() > this.limit) {
                itemStack.setCount(this.limit);
            }
        }
        return itemStack;
    }

    public static Builder builder(UniformLootTableRange uniformLootTableRange) {
        return new Builder(uniformLootTableRange);
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<LootingEnchantLootFunction> {
        protected Factory() {
            super(new Identifier("looting_enchant"), LootingEnchantLootFunction.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, LootingEnchantLootFunction lootingEnchantLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, lootingEnchantLootFunction, jsonSerializationContext);
            jsonObject.add("count", jsonSerializationContext.serialize(lootingEnchantLootFunction.countRange));
            if (lootingEnchantLootFunction.hasLimit()) {
                jsonObject.add("limit", jsonSerializationContext.serialize(lootingEnchantLootFunction.limit));
            }
        }

        @Override
        public LootingEnchantLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            int i = JsonHelper.getInt(jsonObject, "limit", 0);
            return new LootingEnchantLootFunction(lootConditions, JsonHelper.deserialize(jsonObject, "count", jsonDeserializationContext, UniformLootTableRange.class), i);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return this.fromJson(jsonObject, jsonDeserializationContext, lootConditions);
        }
    }

    public static class Builder
    extends ConditionalLootFunction.Builder<Builder> {
        private final UniformLootTableRange countRange;
        private int limit = 0;

        public Builder(UniformLootTableRange uniformLootTableRange) {
            this.countRange = uniformLootTableRange;
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        public Builder withLimit(int i) {
            this.limit = i;
            return this;
        }

        @Override
        public LootFunction build() {
            return new LootingEnchantLootFunction(this.getConditions(), this.countRange, this.limit);
        }

        @Override
        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }
}

