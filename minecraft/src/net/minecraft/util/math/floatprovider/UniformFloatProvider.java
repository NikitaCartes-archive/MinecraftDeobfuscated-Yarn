package net.minecraft.util.math.floatprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;

public class UniformFloatProvider extends FloatProvider {
	public static final Codec<UniformFloatProvider> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.FLOAT.fieldOf("min_inclusive").forGetter(uniformFloatProvider -> uniformFloatProvider.min),
						Codec.FLOAT.fieldOf("max_exclusive").forGetter(uniformFloatProvider -> uniformFloatProvider.max)
					)
					.apply(instance, UniformFloatProvider::new)
		)
		.comapFlatMap(
			uniformFloatProvider -> uniformFloatProvider.max <= uniformFloatProvider.min
					? DataResult.error("Max must be larger than min, min_inclusive: " + uniformFloatProvider.min + ", max_exclusive: " + uniformFloatProvider.max)
					: DataResult.success(uniformFloatProvider),
			Function.identity()
		);
	private final float min;
	private final float max;

	private UniformFloatProvider(float min, float max) {
		this.min = min;
		this.max = max;
	}

	public static UniformFloatProvider create(float min, float max) {
		if (max <= min) {
			throw new IllegalArgumentException("Max must exceed min");
		} else {
			return new UniformFloatProvider(min, max);
		}
	}

	@Override
	public float get(Random random) {
		return MathHelper.nextBetween(random, this.min, this.max);
	}

	@Override
	public float getMin() {
		return this.min;
	}

	@Override
	public float getMax() {
		return this.max;
	}

	@Override
	public FloatProviderType<?> getType() {
		return FloatProviderType.UNIFORM;
	}

	public String toString() {
		return "[" + this.min + "-" + this.max + "]";
	}
}
