/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import org.jetbrains.annotations.Nullable;

public class WeatherCheckLootCondition
implements LootCondition {
    @Nullable
    private final Boolean raining;
    @Nullable
    private final Boolean thundering;

    private WeatherCheckLootCondition(@Nullable Boolean raining, @Nullable Boolean thundering) {
        this.raining = raining;
        this.thundering = thundering;
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.WEATHER_CHECK;
    }

    @Override
    public boolean test(LootContext lootContext) {
        ServerWorld serverWorld = lootContext.getWorld();
        if (this.raining != null && this.raining.booleanValue() != serverWorld.isRaining()) {
            return false;
        }
        return this.thundering == null || this.thundering.booleanValue() == serverWorld.isThundering();
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Serializer
    implements JsonSerializer<WeatherCheckLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, WeatherCheckLootCondition weatherCheckLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("raining", weatherCheckLootCondition.raining);
            jsonObject.addProperty("thundering", weatherCheckLootCondition.thundering);
        }

        @Override
        public WeatherCheckLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Boolean boolean_ = jsonObject.has("raining") ? Boolean.valueOf(JsonHelper.getBoolean(jsonObject, "raining")) : null;
            Boolean boolean2 = jsonObject.has("thundering") ? Boolean.valueOf(JsonHelper.getBoolean(jsonObject, "thundering")) : null;
            return new WeatherCheckLootCondition(boolean_, boolean2);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}

