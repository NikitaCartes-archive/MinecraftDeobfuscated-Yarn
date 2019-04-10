package net.minecraft.world.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextParameters;

public class RandomChanceWithLootingLootCondition implements LootCondition {
	private final float chance;
	private final float lootingMultiplier;

	private RandomChanceWithLootingLootCondition(float f, float g) {
		this.chance = f;
		this.lootingMultiplier = g;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.field_1230);
	}

	public boolean method_950(LootContext lootContext) {
		Entity entity = lootContext.get(LootContextParameters.field_1230);
		int i = 0;
		if (entity instanceof LivingEntity) {
			i = EnchantmentHelper.getLooting((LivingEntity)entity);
		}

		return lootContext.getRandom().nextFloat() < this.chance + (float)i * this.lootingMultiplier;
	}

	public static LootCondition.Builder builder(float f, float g) {
		return () -> new RandomChanceWithLootingLootCondition(f, g);
	}

	public static class Factory extends LootCondition.Factory<RandomChanceWithLootingLootCondition> {
		protected Factory() {
			super(new Identifier("random_chance_with_looting"), RandomChanceWithLootingLootCondition.class);
		}

		public void method_955(
			JsonObject jsonObject, RandomChanceWithLootingLootCondition randomChanceWithLootingLootCondition, JsonSerializationContext jsonSerializationContext
		) {
			jsonObject.addProperty("chance", randomChanceWithLootingLootCondition.chance);
			jsonObject.addProperty("looting_multiplier", randomChanceWithLootingLootCondition.lootingMultiplier);
		}

		public RandomChanceWithLootingLootCondition method_956(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return new RandomChanceWithLootingLootCondition(JsonHelper.getFloat(jsonObject, "chance"), JsonHelper.getFloat(jsonObject, "looting_multiplier"));
		}
	}
}
