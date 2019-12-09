/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.loot.LootTableRange;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public final class ConstantLootTableRange
implements LootTableRange {
    private final int value;

    public ConstantLootTableRange(int value) {
        this.value = value;
    }

    @Override
    public int next(Random random) {
        return this.value;
    }

    @Override
    public Identifier getType() {
        return CONSTANT;
    }

    public static ConstantLootTableRange create(int value) {
        return new ConstantLootTableRange(value);
    }

    public static class Serializer
    implements JsonDeserializer<ConstantLootTableRange>,
    JsonSerializer<ConstantLootTableRange> {
        @Override
        public ConstantLootTableRange deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new ConstantLootTableRange(JsonHelper.asInt(jsonElement, "value"));
        }

        @Override
        public JsonElement serialize(ConstantLootTableRange constantLootTableRange, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(constantLootTableRange.value);
        }

        @Override
        public /* synthetic */ JsonElement serialize(Object range, Type unused, JsonSerializationContext context) {
            return this.serialize((ConstantLootTableRange)range, unused, context);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement json, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(json, unused, context);
        }
    }
}

