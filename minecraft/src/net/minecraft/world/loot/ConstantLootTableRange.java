package net.minecraft.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public final class ConstantLootTableRange implements LootTableRange {
	private final int value;

	public ConstantLootTableRange(int i) {
		this.value = i;
	}

	@Override
	public int next(Random random) {
		return this.value;
	}

	@Override
	public Identifier getType() {
		return CONSTANT;
	}

	public static ConstantLootTableRange create(int i) {
		return new ConstantLootTableRange(i);
	}

	public static class Serializer implements JsonDeserializer<ConstantLootTableRange>, JsonSerializer<ConstantLootTableRange> {
		public ConstantLootTableRange method_291(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return new ConstantLootTableRange(JsonHelper.asInt(jsonElement, "value"));
		}

		public JsonElement method_290(ConstantLootTableRange constantLootTableRange, Type type, JsonSerializationContext jsonSerializationContext) {
			return new JsonPrimitive(constantLootTableRange.value);
		}
	}
}
