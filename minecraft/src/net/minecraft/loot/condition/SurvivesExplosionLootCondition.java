package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.JsonSerializer;

public class SurvivesExplosionLootCondition implements LootCondition {
	static final SurvivesExplosionLootCondition INSTANCE = new SurvivesExplosionLootCondition();

	private SurvivesExplosionLootCondition() {
	}

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.SURVIVES_EXPLOSION;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.EXPLOSION_RADIUS);
	}

	public boolean test(LootContext lootContext) {
		Float float_ = lootContext.get(LootContextParameters.EXPLOSION_RADIUS);
		if (float_ != null) {
			Random random = lootContext.getRandom();
			float f = 1.0F / float_;
			return random.nextFloat() <= f;
		} else {
			return true;
		}
	}

	public static LootCondition.Builder builder() {
		return () -> INSTANCE;
	}

	public static class Serializer implements JsonSerializer<SurvivesExplosionLootCondition> {
		public void toJson(JsonObject jsonObject, SurvivesExplosionLootCondition survivesExplosionLootCondition, JsonSerializationContext jsonSerializationContext) {
		}

		public SurvivesExplosionLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return SurvivesExplosionLootCondition.INSTANCE;
		}
	}
}
