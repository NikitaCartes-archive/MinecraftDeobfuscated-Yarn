package net.minecraft.loot.provider.number;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;

public final class ConstantLootNumberProvider implements LootNumberProvider {
	final float value;

	ConstantLootNumberProvider(float value) {
		this.value = value;
	}

	@Override
	public LootNumberProviderType getType() {
		return LootNumberProviderTypes.CONSTANT;
	}

	@Override
	public float nextFloat(LootContext context) {
		return this.value;
	}

	public static ConstantLootNumberProvider create(float value) {
		return new ConstantLootNumberProvider(value);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return o != null && this.getClass() == o.getClass() ? Float.compare(((ConstantLootNumberProvider)o).value, this.value) == 0 : false;
		}
	}

	public int hashCode() {
		return this.value != 0.0F ? Float.floatToIntBits(this.value) : 0;
	}

	public static class CustomSerializer implements JsonSerializing.ElementSerializer<ConstantLootNumberProvider> {
		public JsonElement toJson(ConstantLootNumberProvider constantLootNumberProvider, JsonSerializationContext jsonSerializationContext) {
			return new JsonPrimitive(constantLootNumberProvider.value);
		}

		public ConstantLootNumberProvider fromJson(JsonElement jsonElement, JsonDeserializationContext jsonDeserializationContext) {
			return new ConstantLootNumberProvider(JsonHelper.asFloat(jsonElement, "value"));
		}
	}

	public static class Serializer implements JsonSerializer<ConstantLootNumberProvider> {
		public void toJson(JsonObject jsonObject, ConstantLootNumberProvider constantLootNumberProvider, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("value", constantLootNumberProvider.value);
		}

		public ConstantLootNumberProvider fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			float f = JsonHelper.getFloat(jsonObject, "value");
			return new ConstantLootNumberProvider(f);
		}
	}
}
