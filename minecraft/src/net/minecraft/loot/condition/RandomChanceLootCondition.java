package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class RandomChanceLootCondition implements LootCondition {
	private final float chance;

	private RandomChanceLootCondition(float chance) {
		this.chance = chance;
	}

	public boolean method_934(LootContext lootContext) {
		return lootContext.getRandom().nextFloat() < this.chance;
	}

	public static LootCondition.Builder builder(float chance) {
		return () -> new RandomChanceLootCondition(chance);
	}

	public static class Factory extends LootCondition.Factory<RandomChanceLootCondition> {
		protected Factory() {
			super(new Identifier("random_chance"), RandomChanceLootCondition.class);
		}

		public void method_936(JsonObject jsonObject, RandomChanceLootCondition randomChanceLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("chance", randomChanceLootCondition.chance);
		}

		public RandomChanceLootCondition method_937(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return new RandomChanceLootCondition(JsonHelper.getFloat(jsonObject, "chance"));
		}
	}
}
