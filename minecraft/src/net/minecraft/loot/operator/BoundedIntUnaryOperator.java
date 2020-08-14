package net.minecraft.loot.operator;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;

public class BoundedIntUnaryOperator implements IntUnaryOperator {
	private final Integer min;
	private final Integer max;
	private final IntUnaryOperator operator;

	private BoundedIntUnaryOperator(@Nullable Integer min, @Nullable Integer max) {
		this.min = min;
		this.max = max;
		if (min == null) {
			if (max == null) {
				this.operator = ix -> ix;
			} else {
				int i = max;
				this.operator = jx -> Math.min(i, jx);
			}
		} else {
			int i = min;
			if (max == null) {
				this.operator = jx -> Math.max(i, jx);
			} else {
				int j = max;
				this.operator = k -> MathHelper.clamp(k, i, j);
			}
		}
	}

	public static BoundedIntUnaryOperator create(int min, int max) {
		return new BoundedIntUnaryOperator(min, max);
	}

	public static BoundedIntUnaryOperator createMin(int min) {
		return new BoundedIntUnaryOperator(min, null);
	}

	public static BoundedIntUnaryOperator createMax(int max) {
		return new BoundedIntUnaryOperator(null, max);
	}

	public int applyAsInt(int value) {
		return this.operator.applyAsInt(value);
	}

	public static class Serializer implements JsonDeserializer<BoundedIntUnaryOperator>, JsonSerializer<BoundedIntUnaryOperator> {
		public BoundedIntUnaryOperator deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
			Integer integer = jsonObject.has("min") ? JsonHelper.getInt(jsonObject, "min") : null;
			Integer integer2 = jsonObject.has("max") ? JsonHelper.getInt(jsonObject, "max") : null;
			return new BoundedIntUnaryOperator(integer, integer2);
		}

		public JsonElement serialize(BoundedIntUnaryOperator boundedIntUnaryOperator, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			if (boundedIntUnaryOperator.max != null) {
				jsonObject.addProperty("max", boundedIntUnaryOperator.max);
			}

			if (boundedIntUnaryOperator.min != null) {
				jsonObject.addProperty("min", boundedIntUnaryOperator.min);
			}

			return jsonObject;
		}
	}
}
