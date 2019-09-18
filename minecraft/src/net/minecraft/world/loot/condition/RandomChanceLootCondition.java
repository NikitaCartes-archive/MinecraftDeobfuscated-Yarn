package net.minecraft.world.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.class_4570;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.context.LootContext;

public class RandomChanceLootCondition implements class_4570 {
	private final float chance;

	private RandomChanceLootCondition(float f) {
		this.chance = f;
	}

	public boolean method_934(LootContext lootContext) {
		return lootContext.getRandom().nextFloat() < this.chance;
	}

	public static class_4570.Builder builder(float f) {
		return () -> new RandomChanceLootCondition(f);
	}

	public static class Factory extends class_4570.Factory<RandomChanceLootCondition> {
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
