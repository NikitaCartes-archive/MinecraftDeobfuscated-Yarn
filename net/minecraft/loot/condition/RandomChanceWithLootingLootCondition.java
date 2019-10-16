/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class RandomChanceWithLootingLootCondition
implements LootCondition {
    private final float chance;
    private final float lootingMultiplier;

    private RandomChanceWithLootingLootCondition(float f, float g) {
        this.chance = f;
        this.lootingMultiplier = g;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.KILLER_ENTITY);
    }

    public boolean method_950(LootContext lootContext) {
        Entity entity = lootContext.get(LootContextParameters.KILLER_ENTITY);
        int i = 0;
        if (entity instanceof LivingEntity) {
            i = EnchantmentHelper.getLooting((LivingEntity)entity);
        }
        return lootContext.getRandom().nextFloat() < this.chance + (float)i * this.lootingMultiplier;
    }

    public static LootCondition.Builder builder(float f, float g) {
        return () -> new RandomChanceWithLootingLootCondition(f, g);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.method_950((LootContext)object);
    }

    public static class Factory
    extends LootCondition.Factory<RandomChanceWithLootingLootCondition> {
        protected Factory() {
            super(new Identifier("random_chance_with_looting"), RandomChanceWithLootingLootCondition.class);
        }

        public void method_955(JsonObject jsonObject, RandomChanceWithLootingLootCondition randomChanceWithLootingLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("chance", Float.valueOf(randomChanceWithLootingLootCondition.chance));
            jsonObject.addProperty("looting_multiplier", Float.valueOf(randomChanceWithLootingLootCondition.lootingMultiplier));
        }

        public RandomChanceWithLootingLootCondition method_956(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return new RandomChanceWithLootingLootCondition(JsonHelper.getFloat(jsonObject, "chance"), JsonHelper.getFloat(jsonObject, "looting_multiplier"));
        }

        @Override
        public /* synthetic */ LootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.method_956(jsonObject, jsonDeserializationContext);
        }
    }
}

