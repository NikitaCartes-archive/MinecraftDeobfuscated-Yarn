package net.minecraft.util;

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
import net.minecraft.util.math.MathHelper;

public class BoundedIntUnaryOperator implements IntUnaryOperator {
	private final Integer min;
	private final Integer max;
	private final IntUnaryOperator operator;

	private BoundedIntUnaryOperator(@Nullable Integer integer, @Nullable Integer integer2) {
		this.min = integer;
		this.max = integer2;
		if (integer == null) {
			if (integer2 == null) {
				this.operator = ix -> ix;
			} else {
				int i = integer2;
				this.operator = jx -> Math.min(i, jx);
			}
		} else {
			int i = integer;
			if (integer2 == null) {
				this.operator = jx -> Math.max(i, jx);
			} else {
				int j = integer2;
				this.operator = k -> MathHelper.clamp(k, i, j);
			}
		}
	}

	public static BoundedIntUnaryOperator create(int i, int j) {
		return new BoundedIntUnaryOperator(i, j);
	}

	public static BoundedIntUnaryOperator createMin(int i) {
		return new BoundedIntUnaryOperator(i, null);
	}

	public static BoundedIntUnaryOperator createMax(int i) {
		return new BoundedIntUnaryOperator(null, i);
	}

	public int applyAsInt(int i) {
		return this.operator.applyAsInt(i);
	}

	public static class Serializer implements JsonDeserializer<BoundedIntUnaryOperator>, JsonSerializer<BoundedIntUnaryOperator> {
		public BoundedIntUnaryOperator method_286(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
			Integer integer = jsonObject.has("min") ? JsonHelper.getInt(jsonObject, "min") : null;
			Integer integer2 = jsonObject.has("max") ? JsonHelper.getInt(jsonObject, "max") : null;
			return new BoundedIntUnaryOperator(integer, integer2);
		}

		public JsonElement method_287(BoundedIntUnaryOperator boundedIntUnaryOperator, Type type, JsonSerializationContext jsonSerializationContext) {
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
