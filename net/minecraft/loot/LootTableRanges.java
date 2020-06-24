/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import java.util.Map;
import net.minecraft.loot.BinomialLootTableRange;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LootTableRanges {
    private static final Map<Identifier, Class<? extends LootTableRange>> TYPES = Maps.newHashMap();

    public static LootTableRange fromJson(JsonElement json, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return (LootTableRange)context.deserialize(json, (Type)((Object)ConstantLootTableRange.class));
        }
        JsonObject jsonObject = json.getAsJsonObject();
        String string = JsonHelper.getString(jsonObject, "type", LootTableRange.UNIFORM.toString());
        Class<? extends LootTableRange> class_ = TYPES.get(new Identifier(string));
        if (class_ == null) {
            throw new JsonParseException("Unknown generator: " + string);
        }
        return (LootTableRange)context.deserialize(jsonObject, class_);
    }

    public static JsonElement toJson(LootTableRange range, JsonSerializationContext context) {
        JsonElement jsonElement = context.serialize(range);
        if (jsonElement.isJsonObject()) {
            jsonElement.getAsJsonObject().addProperty("type", range.getType().toString());
        }
        return jsonElement;
    }

    static {
        TYPES.put(LootTableRange.UNIFORM, UniformLootTableRange.class);
        TYPES.put(LootTableRange.BINOMIAL, BinomialLootTableRange.class);
        TYPES.put(LootTableRange.CONSTANT, ConstantLootTableRange.class);
    }
}

