package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class TimeCheckLootCondition implements LootCondition {
	@Nullable
	final Long period;
	final BoundedIntUnaryOperator value;

	TimeCheckLootCondition(@Nullable Long period, BoundedIntUnaryOperator value) {
		this.period = period;
		this.value = value;
	}

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.TIME_CHECK;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.value.getRequiredParameters();
	}

	public boolean test(LootContext lootContext) {
		ServerWorld serverWorld = lootContext.getWorld();
		long l = serverWorld.getTimeOfDay();
		if (this.period != null) {
			l %= this.period;
		}

		return this.value.test(lootContext, (int)l);
	}

	public static TimeCheckLootCondition.Builder create(BoundedIntUnaryOperator value) {
		return new TimeCheckLootCondition.Builder(value);
	}

	public static class Builder implements LootCondition.Builder {
		@Nullable
		private Long period;
		private final BoundedIntUnaryOperator value;

		public Builder(BoundedIntUnaryOperator value) {
			this.value = value;
		}

		public TimeCheckLootCondition.Builder period(long period) {
			this.period = period;
			return this;
		}

		public TimeCheckLootCondition build() {
			return new TimeCheckLootCondition(this.period, this.value);
		}
	}

	public static class Serializer implements JsonSerializer<TimeCheckLootCondition> {
		public void toJson(JsonObject jsonObject, TimeCheckLootCondition timeCheckLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("period", timeCheckLootCondition.period);
			jsonObject.add("value", jsonSerializationContext.serialize(timeCheckLootCondition.value));
		}

		public TimeCheckLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Long long_ = jsonObject.has("period") ? JsonHelper.getLong(jsonObject, "period") : null;
			BoundedIntUnaryOperator boundedIntUnaryOperator = JsonHelper.deserialize(jsonObject, "value", jsonDeserializationContext, BoundedIntUnaryOperator.class);
			return new TimeCheckLootCondition(long_, boundedIntUnaryOperator);
		}
	}
}
