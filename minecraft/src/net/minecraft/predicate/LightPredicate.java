package net.minecraft.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;

public record LightPredicate(NumberRange.IntRange range) {
	public static final Codec<LightPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(NumberRange.IntRange.CODEC, "light", NumberRange.IntRange.ANY).forGetter(LightPredicate::range)
				)
				.apply(instance, LightPredicate::new)
	);

	static Optional<LightPredicate> create(NumberRange.IntRange range) {
		return range.isDummy() ? Optional.empty() : Optional.of(new LightPredicate(range));
	}

	public boolean test(ServerWorld world, BlockPos pos) {
		return !world.canSetBlock(pos) ? false : this.range.test(world.getLightLevel(pos));
	}

	public static class Builder {
		private NumberRange.IntRange light = NumberRange.IntRange.ANY;

		public static LightPredicate.Builder create() {
			return new LightPredicate.Builder();
		}

		public LightPredicate.Builder light(NumberRange.IntRange light) {
			this.light = light;
			return this;
		}

		public Optional<LightPredicate> build() {
			return LightPredicate.create(this.light);
		}
	}
}
