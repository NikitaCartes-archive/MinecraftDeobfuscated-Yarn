package net.minecraft.util.math.floatprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Function;

public class TrapezoidFloatProvider extends FloatProvider {
	public static final Codec<TrapezoidFloatProvider> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.FLOAT.fieldOf("min").forGetter(trapezoidFloatProvider -> trapezoidFloatProvider.min),
						Codec.FLOAT.fieldOf("max").forGetter(trapezoidFloatProvider -> trapezoidFloatProvider.max),
						Codec.FLOAT.fieldOf("plateau").forGetter(trapezoidFloatProvider -> trapezoidFloatProvider.plateau)
					)
					.apply(instance, TrapezoidFloatProvider::new)
		)
		.comapFlatMap(
			trapezoidFloatProvider -> {
				if (trapezoidFloatProvider.max < trapezoidFloatProvider.min) {
					return DataResult.error("Max must be larger than min: [" + trapezoidFloatProvider.min + ", " + trapezoidFloatProvider.max + "]");
				} else {
					return trapezoidFloatProvider.plateau > trapezoidFloatProvider.max - trapezoidFloatProvider.min
						? DataResult.error("Plateau can at most be the full span: [" + trapezoidFloatProvider.min + ", " + trapezoidFloatProvider.max + "]")
						: DataResult.success(trapezoidFloatProvider);
				}
			},
			Function.identity()
		);
	private final float min;
	private final float max;
	private final float plateau;

	public static TrapezoidFloatProvider create(float min, float max, float plateau) {
		return new TrapezoidFloatProvider(min, max, plateau);
	}

	private TrapezoidFloatProvider(float min, float max, float plateau) {
		this.min = min;
		this.max = max;
		this.plateau = plateau;
	}

	@Override
	public float get(Random random) {
		float f = this.max - this.min;
		float g = (f - this.plateau) / 2.0F;
		float h = f - g;
		return this.min + random.nextFloat() * h + random.nextFloat() * g;
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
		return FloatProviderType.TRAPEZOID;
	}

	public String toString() {
		return "trapezoid(" + this.plateau + ") in [" + this.min + "-" + this.max + "]";
	}
}
