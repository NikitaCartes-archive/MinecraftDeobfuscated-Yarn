package net.minecraft.util.math.intprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class ClampedIntProvider extends IntProvider {
	public static final Codec<ClampedIntProvider> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						IntProvider.VALUE_CODEC.fieldOf("source").forGetter(provider -> provider.source),
						Codec.INT.fieldOf("min_inclusive").forGetter(provider -> provider.min),
						Codec.INT.fieldOf("max_inclusive").forGetter(provider -> provider.max)
					)
					.apply(instance, ClampedIntProvider::new)
		)
		.comapFlatMap(
			provider -> provider.max < provider.min
					? DataResult.error(() -> "Max must be at least min, min_inclusive: " + provider.min + ", max_inclusive: " + provider.max)
					: DataResult.success(provider),
			Function.identity()
		);
	private final IntProvider source;
	private final int min;
	private final int max;

	public static ClampedIntProvider create(IntProvider source, int min, int max) {
		return new ClampedIntProvider(source, min, max);
	}

	public ClampedIntProvider(IntProvider source, int min, int max) {
		this.source = source;
		this.min = min;
		this.max = max;
	}

	@Override
	public int get(Random random) {
		return MathHelper.clamp(this.source.get(random), this.min, this.max);
	}

	@Override
	public int getMin() {
		return Math.max(this.min, this.source.getMin());
	}

	@Override
	public int getMax() {
		return Math.min(this.max, this.source.getMax());
	}

	@Override
	public IntProviderType<?> getType() {
		return IntProviderType.CLAMPED;
	}
}
