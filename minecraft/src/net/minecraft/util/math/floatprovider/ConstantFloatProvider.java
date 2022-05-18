package net.minecraft.util.math.floatprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.random.Random;

public class ConstantFloatProvider extends FloatProvider {
	public static final ConstantFloatProvider ZERO = new ConstantFloatProvider(0.0F);
	public static final Codec<ConstantFloatProvider> CODEC = Codec.either(
			Codec.FLOAT,
			RecordCodecBuilder.create(
				instance -> instance.group(Codec.FLOAT.fieldOf("value").forGetter(provider -> provider.value)).apply(instance, ConstantFloatProvider::new)
			)
		)
		.xmap(either -> either.map(ConstantFloatProvider::create, provider -> provider), provider -> Either.left(provider.value));
	private final float value;

	public static ConstantFloatProvider create(float value) {
		return value == 0.0F ? ZERO : new ConstantFloatProvider(value);
	}

	private ConstantFloatProvider(float value) {
		this.value = value;
	}

	public float getValue() {
		return this.value;
	}

	@Override
	public float get(Random random) {
		return this.value;
	}

	@Override
	public float getMin() {
		return this.value;
	}

	@Override
	public float getMax() {
		return this.value + 1.0F;
	}

	@Override
	public FloatProviderType<?> getType() {
		return FloatProviderType.CONSTANT;
	}

	public String toString() {
		return Float.toString(this.value);
	}
}
