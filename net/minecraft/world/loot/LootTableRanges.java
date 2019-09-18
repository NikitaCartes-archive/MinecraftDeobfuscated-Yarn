/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.BinomialLootTableRange;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootTableRange;
import net.minecraft.world.loot.UniformLootTableRange;

public class LootTableRanges {
    private static final Map<Identifier, Class<? extends LootTableRange>> types = Maps.newHashMap();

    public static LootTableRange fromJson(JsonElement jsonElement, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.isJsonPrimitive()) {
            return (LootTableRange)jsonDeserializationContext.deserialize(jsonElement, (Type)((Object)ConstantLootTableRange.class));
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String string = JsonHelper.getString(jsonObject, "type", LootTableRange.UNIFORM.toString());
        Class<? extends LootTableRange> class_ = types.get(new Identifier(string));
        if (class_ == null) {
            throw new JsonParseException("Unknown generator: " + string);
        }
        return (LootTableRange)jsonDeserializationContext.deserialize(jsonObject, class_);
    }

    public static JsonElement toJson(LootTableRange lootTableRange, JsonSerializationContext jsonSerializationContext) {
        JsonElement jsonElement = jsonSerializationContext.serialize(lootTableRange);
        if (jsonElement.isJsonObject()) {
            jsonElement.getAsJsonObject().addProperty("type", lootTableRange.getType().toString());
        }
        return jsonElement;
    }

    static {
        types.put(LootTableRange.UNIFORM, UniformLootTableRange.class);
        types.put(LootTableRange.BINOMIAL, BinomialLootTableRange.class);
        types.put(LootTableRange.CONSTANT, ConstantLootTableRange.class);
    }
}

