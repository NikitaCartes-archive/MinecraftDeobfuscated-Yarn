package net.minecraft.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public final class BinomialLootTableRange implements LootTableRange {
	private final int n;
	private final float p;

	public BinomialLootTableRange(int i, float f) {
		this.n = i;
		this.p = f;
	}

	@Override
	public int next(Random random) {
		int i = 0;

		for (int j = 0; j < this.n; j++) {
			if (random.nextFloat() < this.p) {
				i++;
			}
		}

		return i;
	}

	public static BinomialLootTableRange create(int i, float f) {
		return new BinomialLootTableRange(i, f);
	}

	@Override
	public Identifier getType() {
		return BINOMIAL;
	}

	public static class Serializer implements JsonDeserializer<BinomialLootTableRange>, JsonSerializer<BinomialLootTableRange> {
		public BinomialLootTableRange method_275(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
			int i = JsonHelper.getInt(jsonObject, "n");
			float f = JsonHelper.getFloat(jsonObject, "p");
			return new BinomialLootTableRange(i, f);
		}

		public JsonElement method_276(BinomialLootTableRange binomialLootTableRange, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("n", binomialLootTableRange.n);
			jsonObject.addProperty("p", binomialLootTableRange.p);
			return jsonObject;
		}
	}
}
