package net.minecraft.loot.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.server.world.ServerWorld;

public record TimeCheckLootCondition(Optional<Long> period, BoundedIntUnaryOperator value) implements LootCondition {
	public static final MapCodec<TimeCheckLootCondition> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.LONG.optionalFieldOf("period").forGetter(TimeCheckLootCondition::period),
					BoundedIntUnaryOperator.CODEC.fieldOf("value").forGetter(TimeCheckLootCondition::value)
				)
				.apply(instance, TimeCheckLootCondition::new)
	);

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
		if (this.period.isPresent()) {
			l %= this.period.get();
		}

		return this.value.test(lootContext, (int)l);
	}

	public static TimeCheckLootCondition.Builder create(BoundedIntUnaryOperator value) {
		return new TimeCheckLootCondition.Builder(value);
	}

	public static class Builder implements LootCondition.Builder {
		private Optional<Long> period = Optional.empty();
		private final BoundedIntUnaryOperator value;

		public Builder(BoundedIntUnaryOperator value) {
			this.value = value;
		}

		public TimeCheckLootCondition.Builder period(long period) {
			this.period = Optional.of(period);
			return this;
		}

		public TimeCheckLootCondition build() {
			return new TimeCheckLootCondition(this.period, this.value);
		}
	}
}
