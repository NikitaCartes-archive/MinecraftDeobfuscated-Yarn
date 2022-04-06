/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.random.AbstractRandom;

public class EnchantWithLevelsLootFunction
extends ConditionalLootFunction {
    final LootNumberProvider range;
    final boolean treasureEnchantmentsAllowed;

    EnchantWithLevelsLootFunction(LootCondition[] conditions, LootNumberProvider range, boolean treasureEnchantmentsAllowed) {
        super(conditions);
        this.range = range;
        this.treasureEnchantmentsAllowed = treasureEnchantmentsAllowed;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.ENCHANT_WITH_LEVELS;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return this.range.getRequiredParameters();
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        AbstractRandom abstractRandom = context.getRandom();
        return EnchantmentHelper.enchant(abstractRandom, stack, this.range.nextInt(context), this.treasureEnchantmentsAllowed);
    }

    public static Builder builder(LootNumberProvider range) {
        return new Builder(range);
    }

    public static class Builder
    extends ConditionalLootFunction.Builder<Builder> {
        private final LootNumberProvider range;
        private boolean treasureEnchantmentsAllowed;

        public Builder(LootNumberProvider range) {
            this.range = range;
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        public Builder allowTreasureEnchantments() {
            this.treasureEnchantmentsAllowed = true;
            return this;
        }

        @Override
        public LootFunction build() {
            return new EnchantWithLevelsLootFunction(this.getConditions(), this.range, this.treasureEnchantmentsAllowed);
        }

        @Override
        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<EnchantWithLevelsLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, EnchantWithLevelsLootFunction enchantWithLevelsLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, enchantWithLevelsLootFunction, jsonSerializationContext);
            jsonObject.add("levels", jsonSerializationContext.serialize(enchantWithLevelsLootFunction.range));
            jsonObject.addProperty("treasure", enchantWithLevelsLootFunction.treasureEnchantmentsAllowed);
        }

        @Override
        public EnchantWithLevelsLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            LootNumberProvider lootNumberProvider = JsonHelper.deserialize(jsonObject, "levels", jsonDeserializationContext, LootNumberProvider.class);
            boolean bl = JsonHelper.getBoolean(jsonObject, "treasure", false);
            return new EnchantWithLevelsLootFunction(lootConditions, lootNumberProvider, bl);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

