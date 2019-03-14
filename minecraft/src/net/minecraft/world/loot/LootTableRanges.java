package net.minecraft.world.loot;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LootTableRanges {
	private static final Map<Identifier, Class<? extends LootTableRange>> types = Maps.<Identifier, Class<? extends LootTableRange>>newHashMap();

	public static LootTableRange deserialize(JsonElement jsonElement, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		if (jsonElement.isJsonPrimitive()) {
			return jsonDeserializationContext.deserialize(jsonElement, ConstantLootTableRange.class);
		} else {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			String string = JsonHelper.getString(jsonObject, "type", LootTableRange.UNIFORM.toString());
			Class<? extends LootTableRange> class_ = (Class<? extends LootTableRange>)types.get(new Identifier(string));
			if (class_ == null) {
				throw new JsonParseException("Unknown generator: " + string);
			} else {
				return jsonDeserializationContext.deserialize(jsonObject, class_);
			}
		}
	}

	public static JsonElement serialize(LootTableRange lootTableRange, JsonSerializationContext jsonSerializationContext) {
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
