package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class TimeCheckLootCondition implements LootCondition {
	@Nullable
	private final Long period;
	private final UniformLootTableRange value;

	private TimeCheckLootCondition(@Nullable Long period, UniformLootTableRange value) {
		this.period = period;
		this.value = value;
	}

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.TIME_CHECK;
	}

	public boolean test(LootContext lootContext) {
		ServerWorld serverWorld = lootContext.getWorld();
		long l = serverWorld.getTimeOfDay();
		if (this.period != null) {
			l %= this.period;
		}

		return this.value.contains((int)l);
	}

	public static class Serializer implements JsonSerializer<TimeCheckLootCondition> {
		public void toJson(JsonObject jsonObject, TimeCheckLootCondition timeCheckLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("period", timeCheckLootCondition.period);
			jsonObject.add("value", jsonSerializationContext.serialize(timeCheckLootCondition.value));
		}

		public TimeCheckLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Long long_ = jsonObject.has("period") ? JsonHelper.getLong(jsonObject, "period") : null;
			UniformLootTableRange uniformLootTableRange = JsonHelper.deserialize(jsonObject, "value", jsonDeserializationContext, UniformLootTableRange.class);
			return new TimeCheckLootCondition(long_, uniformLootTableRange);
		}
	}
}
