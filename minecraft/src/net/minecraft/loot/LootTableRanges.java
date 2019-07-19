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
	private static final Map<Identifier, Class<? extends LootTableRange>> types = Maps.<Identifier, Class<? extends LootTableRange>>newHashMap();

	public static LootTableRange fromJson(JsonElement json, JsonDeserializationContext context) throws JsonParseException {
		if (json.isJsonPrimitive()) {
			return context.deserialize(json, ConstantLootTableRange.class);
		} else {
			JsonObject jsonObject = json.getAsJsonObject();
			String string = JsonHelper.getString(jsonObject, "type", LootTableRange.UNIFORM.toString());
			Class<? extends LootTableRange> class_ = (Class<? extends LootTableRange>)types.get(new Identifier(string));
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
		types.put(LootTableRange.UNIFORM, UniformLootTableRange.class);
		types.put(LootTableRange.BINOMIAL, BinomialLootTableRange.class);
		types.put(LootTableRange.CONSTANT, ConstantLootTableRange.class);
	}
}
