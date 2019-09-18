/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;

public class RandomChanceLootCondition
implements LootCondition {
    private final float chance;

    private RandomChanceLootCondition(float f) {
        this.chance = f;
    }

    public boolean method_934(LootContext lootContext) {
        return lootContext.getRandom().nextFloat() < this.chance;
    }

    public static LootCondition.Builder builder(float f) {
        return () -> new RandomChanceLootCondition(f);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.method_934((LootContext)object);
    }

    public static class Factory
    extends LootCondition.Factory<RandomChanceLootCondition> {
        protected Factory() {
            super(new Identifier("random_chance"), RandomChanceLootCondition.class);
        }

        public void method_936(JsonObject jsonObject, RandomChanceLootCondition randomChanceLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("chance", Float.valueOf(randomChanceLootCondition.chance));
        }

        public RandomChanceLootCondition method_937(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return new RandomChanceLootCondition(JsonHelper.getFloat(jsonObject, "chance"));
        }

        @Override
        public /* synthetic */ LootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.method_937(jsonObject, jsonDeserializationContext);
        }
    }
}

