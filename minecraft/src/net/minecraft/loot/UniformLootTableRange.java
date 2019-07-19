package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;

public class UniformLootTableRange implements LootTableRange {
	private final float min;
	private final float max;

	public UniformLootTableRange(float min, float max) {
		this.min = min;
		this.max = max;
	}

	public UniformLootTableRange(float value) {
		this.min = value;
		this.max = value;
	}

	public static UniformLootTableRange between(float min, float max) {
		return new UniformLootTableRange(min, max);
	}

	public float getMinValue() {
		return this.min;
	}

	public float getMaxValue() {
		return this.max;
	}

	@Override
	public int next(Random random) {
		return MathHelper.nextInt(random, MathHelper.floor(this.min), MathHelper.floor(this.max));
	}

	public float nextFloat(Random random) {
		return MathHelper.nextFloat(random, this.min, this.max);
	}

	public boolean contains(int value) {
		return (float)value <= this.max && (float)value >= this.min;
	}

	@Override
	public Identifier getType() {
		return UNIFORM;
	}

	public static class Serializer implements JsonDeserializer<UniformLootTableRange>, JsonSerializer<UniformLootTableRange> {
		public UniformLootTableRange deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (JsonHelper.isNumber(jsonElement)) {
				return new UniformLootTableRange(JsonHelper.asFloat(jsonElement, "value"));
			} else {
				JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
				float f = JsonHelper.getFloat(jsonObject, "min");
				float g = JsonHelper.getFloat(jsonObject, "max");
				return new UniformLootTableRange(f, g);
			}
		}

		public JsonElement serialize(UniformLootTableRange uniformLootTableRange, Type type, JsonSerializationContext jsonSerializationContext) {
			if (uniformLootTableRange.min == uniformLootTableRange.max) {
				return new JsonPrimitive(uniformLootTableRange.min);
			} else {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("min", uniformLootTableRange.min);
				jsonObject.addProperty("max", uniformLootTableRange.max);
				return jsonObject;
			}
		}
	}
}
