package net.minecraft.loot.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;

public record WeatherCheckLootCondition(Optional<Boolean> raining, Optional<Boolean> thundering) implements LootCondition {
	public static final Codec<WeatherCheckLootCondition> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "raining").forGetter(WeatherCheckLootCondition::raining),
					Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "thundering").forGetter(WeatherCheckLootCondition::thundering)
				)
				.apply(instance, WeatherCheckLootCondition::new)
	);

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.WEATHER_CHECK;
	}

	public boolean test(LootContext lootContext) {
		ServerWorld serverWorld = lootContext.getWorld();
		if (this.raining.isPresent() && this.raining.get() != serverWorld.isRaining()) {
			return false;
		} else {
			return !this.thundering.isPresent() || this.thundering.get() == serverWorld.isThundering();
		}
	}

	public static WeatherCheckLootCondition.Builder create() {
		return new WeatherCheckLootCondition.Builder();
	}

	public static class Builder implements LootCondition.Builder {
		private Optional<Boolean> raining = Optional.empty();
		private Optional<Boolean> thundering = Optional.empty();

		public WeatherCheckLootCondition.Builder raining(boolean raining) {
			this.raining = Optional.of(raining);
			return this;
		}

		public WeatherCheckLootCondition.Builder thundering(boolean thundering) {
			this.thundering = Optional.of(thundering);
			return this;
		}

		public WeatherCheckLootCondition build() {
			return new WeatherCheckLootCondition(this.raining, this.thundering);
		}
	}
}
