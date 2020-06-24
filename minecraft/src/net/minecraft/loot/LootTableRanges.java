package net.minecraft.loot;

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
	private static final Map<Identifier, Class<? extends LootTableRange>> TYPES = Maps.<Identifier, Class<? extends LootTableRange>>newHashMap();

	public static LootTableRange fromJson(JsonElement json, JsonDeserializationContext context) throws JsonParseException {
		if (json.isJsonPrimitive()) {
			return context.deserialize(json, ConstantLootTableRange.class);
		} else {
			JsonObject jsonObject = json.getAsJsonObject();
			String string = JsonHelper.getString(jsonObject, "type", LootTableRange.UNIFORM.toString());
			Class<? extends LootTableRange> class_ = (Class<? extends LootTableRange>)TYPES.get(new Identifier(string));
			if (class_ == null) {
				throw new JsonParseException("Unknown generator: " + string);
			} else {
				return context.deserialize(jsonObject, class_);
			}
		}
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
