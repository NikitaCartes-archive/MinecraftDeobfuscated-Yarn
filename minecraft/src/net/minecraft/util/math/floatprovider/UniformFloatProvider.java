package net.minecraft.util.math.floatprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;

public class UniformFloatProvider extends FloatProvider {
	public static final Codec<UniformFloatProvider> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.FLOAT.fieldOf("base").forGetter(uniformFloatProvider -> uniformFloatProvider.base),
						Codec.FLOAT.fieldOf("spread").forGetter(uniformFloatProvider -> uniformFloatProvider.spread)
					)
					.apply(instance, UniformFloatProvider::new)
		)
		.comapFlatMap(
			uniformFloatProvider -> uniformFloatProvider.spread < 0.0F
					? DataResult.error("Spread must be non-negative, got: " + uniformFloatProvider.spread)
					: DataResult.success(uniformFloatProvider),
			Function.identity()
		);
	private final float base;
	private final float spread;

	private UniformFloatProvider(float base, float spread) {
		this.base = base;
		this.spread = spread;
	}

	public static UniformFloatProvider create(float base, float spread) {
		return new UniformFloatProvider(base, spread);
	}

	@Override
	public float get(Random random) {
		return this.spread == 0.0F ? this.base : MathHelper.nextBetween(random, this.base, this.base + this.spread);
	}

	@Override
	public float getMin() {
		return this.base;
	}

	@Override
	public float getMax() {
		return this.base + this.spread;
	}

	@Override
	public FloatProviderType<?> getType() {
		return FloatProviderType.UNIFORM;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			UniformFloatProvider uniformFloatProvider = (UniformFloatProvider)object;
			return this.base == uniformFloatProvider.base && this.spread == uniformFloatProvider.spread;
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
