package net.minecraft.util.math.floatprovider;

import com.mojang.serialization.Codec;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;

public class ConstantFloatProvider extends FloatProvider {
	public static final ConstantFloatProvider ZERO = new ConstantFloatProvider(0.0F);
	public static final Codec<ConstantFloatProvider> CODEC = Codecs.alternatively(Codec.FLOAT, Codec.FLOAT.fieldOf("value").codec())
		.xmap(ConstantFloatProvider::new, ConstantFloatProvider::getValue);
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
