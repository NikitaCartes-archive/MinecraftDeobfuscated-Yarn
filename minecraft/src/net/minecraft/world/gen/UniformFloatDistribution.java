package net.minecraft.world.gen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;

public class UniformFloatDistribution {
	public static final Codec<UniformFloatDistribution> CODEC = Codec.either(
			Codec.FLOAT,
			RecordCodecBuilder.create(
					instance -> instance.group(
								Codec.FLOAT.fieldOf("base").forGetter(uniformFloatDistribution -> uniformFloatDistribution.base),
								Codec.FLOAT.fieldOf("spread").forGetter(uniformFloatDistribution -> uniformFloatDistribution.spread)
							)
							.apply(instance, UniformFloatDistribution::new)
				)
				.comapFlatMap(
					uniformFloatDistribution -> uniformFloatDistribution.spread < 0.0F
							? DataResult.error("Spread must be non-negative, got: " + uniformFloatDistribution.spread)
							: DataResult.success(uniformFloatDistribution),
					Function.identity()
				)
		)
		.xmap(
			either -> either.map(UniformFloatDistribution::of, uniformFloatDistribution -> uniformFloatDistribution),
			uniformFloatDistribution -> uniformFloatDistribution.spread == 0.0F ? Either.left(uniformFloatDistribution.base) : Either.right(uniformFloatDistribution)
		);
	private final float base;
	private final float spread;

	public static Codec<UniformFloatDistribution> createValidatedCodec(float minBase, float maxBase, float maxSpread) {
		Function<UniformFloatDistribution, DataResult<UniformFloatDistribution>> function = uniformFloatDistribution -> {
			if (!(uniformFloatDistribution.base >= minBase) || !(uniformFloatDistribution.base <= maxBase)) {
				return DataResult.error("Base value out of range: " + uniformFloatDistribution.base + " [" + minBase + "-" + maxBase + "]");
			} else {
				return uniformFloatDistribution.spread <= maxSpread
					? DataResult.success(uniformFloatDistribution)
					: DataResult.error("Spread too big: " + uniformFloatDistribution.spread + " > " + maxSpread);
			}
		};
		return CODEC.flatXmap(function, function);
	}

	private UniformFloatDistribution(float base, float spread) {
		this.base = base;
		this.spread = spread;
	}

	/**
	 * Creates a distribution with a constant value.
	 * 
	 * @param value the constant value
	 */
	public static UniformFloatDistribution of(float value) {
		return new UniformFloatDistribution(value, 0.0F);
	}

	public static UniformFloatDistribution of(float base, float spread) {
		return new UniformFloatDistribution(base, spread);
	}

	public float getValue(Random random) {
		return this.spread == 0.0F ? this.base : MathHelper.nextBetween(random, this.base, this.base + this.spread);
	}

	public float minValue() {
		return this.base;
	}

	public float maxValue() {
		return this.base + this.spread;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			UniformFloatDistribution uniformFloatDistribution = (UniformFloatDistribution)object;
			return this.base == uniformFloatDistribution.base && this.spread == uniformFloatDistribution.spread;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.base, this.spread});
	}

	public String toString() {
		return "[" + this.base + '-' + (this.base + this.spread) + ']';
	}
}
