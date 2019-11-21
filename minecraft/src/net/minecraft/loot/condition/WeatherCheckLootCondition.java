package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class WeatherCheckLootCondition implements LootCondition {
	@Nullable
	private final Boolean raining;
	@Nullable
	private final Boolean thundering;

	private WeatherCheckLootCondition(@Nullable Boolean raining, @Nullable Boolean thundering) {
		this.raining = raining;
		this.thundering = thundering;
	}

	public boolean test(LootContext lootContext) {
		ServerWorld serverWorld = lootContext.getWorld();
		return this.raining != null && this.raining != serverWorld.isRaining() ? false : this.thundering == null || this.thundering == serverWorld.isThundering();
	}

	public static class Factory extends LootCondition.Factory<WeatherCheckLootCondition> {
		public Factory() {
			super(new Identifier("weather_check"), WeatherCheckLootCondition.class);
		}

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
