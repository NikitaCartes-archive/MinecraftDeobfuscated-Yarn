package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class WeatherCheckLootCondition implements LootCondition {
	@Nullable
	final Boolean raining;
	@Nullable
	final Boolean thundering;

	WeatherCheckLootCondition(@Nullable Boolean raining, @Nullable Boolean thundering) {
		this.raining = raining;
		this.thundering = thundering;
	}

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.WEATHER_CHECK;
	}

	public boolean test(LootContext lootContext) {
		ServerWorld serverWorld = lootContext.getWorld();
		return this.raining != null && this.raining != serverWorld.isRaining() ? false : this.thundering == null || this.thundering == serverWorld.isThundering();
	}

	public static WeatherCheckLootCondition.Builder create() {
		return new WeatherCheckLootCondition.Builder();
	}

	public static class Builder implements LootCondition.Builder {
		@Nullable
		private Boolean raining;
		@Nullable
		private Boolean thundering;

		public WeatherCheckLootCondition.Builder raining(@Nullable Boolean raining) {
			this.raining = raining;
			return this;
		}

		public WeatherCheckLootCondition.Builder thundering(@Nullable Boolean thundering) {
			this.thundering = thundering;
			return this;
		}

		public WeatherCheckLootCondition build() {
			return new WeatherCheckLootCondition(this.raining, this.thundering);
		}
	}

	public static class Serializer implements JsonSerializer<WeatherCheckLootCondition> {
		public void toJson(JsonObject jsonObject, WeatherCheckLootCondition weatherCheckLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("raining", weatherCheckLootCondition.raining);
			jsonObject.addProperty("thundering", weatherCheckLootCondition.thundering);
		}

		public WeatherCheckLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Boolean boolean_ = jsonObject.has("raining") ? JsonHelper.getBoolean(jsonObject, "raining") : null;
			Boolean boolean2 = jsonObject.has("thundering") ? JsonHelper.getBoolean(jsonObject, "thundering") : null;
			return new WeatherCheckLootCondition(boolean_, boolean2);
		}
	}
}
