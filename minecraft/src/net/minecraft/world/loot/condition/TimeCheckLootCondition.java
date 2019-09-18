package net.minecraft.world.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.context.LootContext;

public class TimeCheckLootCondition implements LootCondition {
	@Nullable
	private final Long period;
	private final UniformLootTableRange value;

	private TimeCheckLootCondition(@Nullable Long long_, UniformLootTableRange uniformLootTableRange) {
		this.period = long_;
		this.value = uniformLootTableRange;
	}

	public boolean method_22587(LootContext lootContext) {
		ServerWorld serverWorld = lootContext.getWorld();
		long l = serverWorld.getTimeOfDay();
		if (this.period != null) {
			l %= this.period;
		}

		return this.value.contains((int)l);
	}

	public static class Factory extends LootCondition.Factory<TimeCheckLootCondition> {
		public Factory() {
			super(new Identifier("time_check"), TimeCheckLootCondition.class);
		}

		public void method_22591(JsonObject jsonObject, TimeCheckLootCondition timeCheckLootCondition, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("period", timeCheckLootCondition.period);
			jsonObject.add("value", jsonSerializationContext.serialize(timeCheckLootCondition.value));
		}

		public TimeCheckLootCondition method_22590(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Long long_ = jsonObject.has("period") ? JsonHelper.method_22449(jsonObject, "period") : null;
			UniformLootTableRange uniformLootTableRange = JsonHelper.deserialize(jsonObject, "value", jsonDeserializationContext, UniformLootTableRange.class);
			return new TimeCheckLootCondition(long_, uniformLootTableRange);
		}
	}
}
