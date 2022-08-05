package net.minecraft.data.client;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The supplier for a {@code ModelVariant}'s JSON representation.
 */
public class BlockStateVariant implements Supplier<JsonElement> {
	private final Map<VariantSetting<?>, VariantSetting<?>.Value> properties = Maps.<VariantSetting<?>, VariantSetting<?>.Value>newLinkedHashMap();

	public <T> BlockStateVariant put(VariantSetting<T> key, T value) {
		VariantSetting<?>.Value value2 = (VariantSetting.Value)this.properties.put(key, key.evaluate(value));
		if (value2 != null) {
			throw new IllegalStateException("Replacing value of " + value2 + " with " + value);
		} else {
			return this;
		}
	}

	public static BlockStateVariant create() {
		return new BlockStateVariant();
	}

	public static BlockStateVariant union(BlockStateVariant first, BlockStateVariant second) {
		BlockStateVariant blockStateVariant = new BlockStateVariant();
		blockStateVariant.properties.putAll(first.properties);
		blockStateVariant.properties.putAll(second.properties);
		return blockStateVariant;
	}

	public JsonElement get() {
		JsonObject jsonObject = new JsonObject();
		this.properties.values().forEach(value -> value.writeTo(jsonObject));
		return jsonObject;
	}

	public static JsonElement toJson(List<BlockStateVariant> variants) {
		if (variants.size() == 1) {
			return ((BlockStateVariant)variants.get(0)).get();
		} else {
			JsonArray jsonArray = new JsonArray();
			variants.forEach(variant -> jsonArray.add(variant.get()));
			return jsonArray;
		}
	}
}
