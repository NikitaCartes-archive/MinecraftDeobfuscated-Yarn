package net.minecraft.util.math.intprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class ClampedNormalIntProvider extends IntProvider {
	public static final Codec<ClampedNormalIntProvider> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.FLOAT.fieldOf("mean").forGetter(provider -> provider.mean),
						Codec.FLOAT.fieldOf("deviation").forGetter(provider -> provider.deviation),
						Codec.INT.fieldOf("min_inclusive").forGetter(provider -> provider.min),
						Codec.INT.fieldOf("max_inclusive").forGetter(provider -> provider.max)
					)
					.apply(instance, ClampedNormalIntProvider::new)
		)
		.comapFlatMap(
			provider -> provider.max < provider.min
					? DataResult.error(() -> "Max must be larger than min: [" + provider.min + ", " + provider.max + "]")
					: DataResult.success(provider),
			Function.identity()
		);
	private final float mean;
	private final float deviation;
	private final int min;
	private final int max;

	public static ClampedNormalIntProvider of(float mean, float deviation, int min, int max) {
		return new ClampedNormalIntProvider(mean, deviation, min, max);
	}

	private ClampedNormalIntProvider(float mean, float deviation, int min, int max) {
		this.mean = mean;
		this.deviation = deviation;
		this.min = min;
		this.max = max;
	}

	@Override
	public int get(Random random) {
		return next(random, this.mean, this.deviation, (float)this.min, (float)this.max);
	}

	public static int next(Random random, float mean, float deviation, float min, float max) {
		return (int)MathHelper.clamp(MathHelper.nextGaussian(random, mean, deviation), min, max);
	}

	@Override
	public int getMin() {
		return this.min;
	}

	@Override
	public int getMax() {
		return this.max;
	}

	@Override
	public IntProviderType<?> getType() {
		return IntProviderType.CLAMPED_NORMAL;
	}

	public String toString() {
		return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.min + "-" + this.max + "]";
	}
}
