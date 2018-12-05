package net.minecraft.world.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.context.LootContext;

public class RandomChanceLootCondition implements LootCondition {
	private final float chance;

	private RandomChanceLootCondition(float f) {
		this.chance = f;
	}

	public boolean method_934(LootContext lootContext) {
		return lootContext.getRandom().nextFloat() < this.chance;
	}

	public static LootCondition.Builder method_932(float f) {
		return () -> new RandomChanceLootCondition(f);
	}

	public static class Factory extends LootCondition.Factory<RandomChanceLootCondition> {
		protected Factory() {
			super(new Identifier("random_chance"), RandomChanceLootCondition.class);
		}

		public void serialize(JsonObject jsonObject, RandomChanceLootCondition randomChanceLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("chance", randomChanceLootCondition.chance);
		}

		public RandomChanceLootCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return new RandomChanceLootCondition(JsonHelper.getFloat(jsonObject, "chance"));
		}
	}
}
