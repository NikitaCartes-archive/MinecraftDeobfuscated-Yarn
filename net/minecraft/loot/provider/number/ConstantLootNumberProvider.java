/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.provider.number;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;

public final class ConstantLootNumberProvider
implements LootNumberProvider {
    final float value;

    ConstantLootNumberProvider(float f) {
        this.value = f;
    }

    @Override
    public LootNumberProviderType getType() {
        return LootNumberProviderTypes.CONSTANT;
    }

    @Override
    public float nextFloat(LootContext context) {
        return this.value;
    }

    public static ConstantLootNumberProvider create(float value) {
        return new ConstantLootNumberProvider(value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        return Float.compare(((ConstantLootNumberProvider)o).value, this.value) == 0;
    }

    public int hashCode() {
        return this.value != 0.0f ? Float.floatToIntBits(this.value) : 0;
    }

    public static class CustomSerializer
    implements JsonSerializing.CustomSerializer<ConstantLootNumberProvider> {
        @Override
        public JsonElement toJson(ConstantLootNumberProvider constantLootNumberProvider, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(Float.valueOf(constantLootNumberProvider.value));
        }

        @Override
        public ConstantLootNumberProvider fromJson(JsonElement jsonElement, JsonDeserializationContext jsonDeserializationContext) {
            return new ConstantLootNumberProvider(JsonHelper.asFloat(jsonElement, "value"));
        }

        @Override
        public /* synthetic */ Object fromJson(JsonElement json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }

    public static class Serializer
    implements JsonSerializer<ConstantLootNumberProvider> {
        @Override
        public void toJson(JsonObject jsonObject, ConstantLootNumberProvider constantLootNumberProvider, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("value", Float.valueOf(constantLootNumberProvider.value));
        }

        @Override
        public ConstantLootNumberProvider fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            float f = JsonHelper.getFloat(jsonObject, "value");
            return new ConstantLootNumberProvider(f);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}

