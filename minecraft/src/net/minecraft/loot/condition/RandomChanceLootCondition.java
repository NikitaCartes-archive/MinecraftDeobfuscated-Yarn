package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializable;

public class RandomChanceLootCondition implements LootCondition {
	private final float chance;

	private RandomChanceLootCondition(float chance) {
		this.chance = chance;
	}

	@Override
	public LootConditionType method_29325() {
		return LootConditionTypes.RANDOM_CHANCE;
	}

	public boolean test(LootContext lootContext) {
		return lootContext.getRandom().nextFloat() < this.chance;
	}

	public static LootCondition.Builder builder(float chance) {
		return () -> new RandomChanceLootCondition(chance);
	}

	public static class Factory implements JsonSerializable<RandomChanceLootCondition> {
		public void toJson(JsonObject jsonObject, RandomChanceLootCondition randomChanceLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("chance", randomChanceLootCondition.chance);
		}

		public RandomChanceLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return new RandomChanceLootCondition(JsonHelper.getFloat(jsonObject, "chance"));
		}
	}
}
