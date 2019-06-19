package net.minecraft.world.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextParameters;

public class SurvivesExplosionLootCondition implements LootCondition {
	private static final SurvivesExplosionLootCondition INSTANCE = new SurvivesExplosionLootCondition();

	private SurvivesExplosionLootCondition() {
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.field_1225);
	}

	public boolean method_869(LootContext lootContext) {
		Float float_ = lootContext.get(LootContextParameters.field_1225);
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

	public static class Factory extends LootCondition.Factory<SurvivesExplosionLootCondition> {
		protected Factory() {
			super(new Identifier("survives_explosion"), SurvivesExplosionLootCondition.class);
		}

		public void method_874(
			JsonObject jsonObject, SurvivesExplosionLootCondition survivesExplosionLootCondition, JsonSerializationContext jsonSerializationContext
		) {
		}

		public SurvivesExplosionLootCondition method_873(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return SurvivesExplosionLootCondition.INSTANCE;
		}
	}
}
