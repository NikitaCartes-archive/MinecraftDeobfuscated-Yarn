package net.minecraft.util.math.intprovider;

import com.mojang.serialization.Codec;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;

public class ConstantIntProvider extends IntProvider {
	public static final ConstantIntProvider ZERO = new ConstantIntProvider(0);
	public static final Codec<ConstantIntProvider> CODEC = Codecs.either(Codec.INT, Codec.INT.fieldOf("value").codec())
		.xmap(ConstantIntProvider::new, ConstantIntProvider::getValue);
	private final int value;

	public static ConstantIntProvider create(int value) {
		return value == 0 ? ZERO : new ConstantIntProvider(value);
	}

	private ConstantIntProvider(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	@Override
	public int get(Random random) {
		return this.value;
	}

	@Override
	public int getMin() {
		return this.value;
	}

	@Override
	public int getMax() {
		return this.value;
	}

	@Override
	public IntProviderType<?> getType() {
		return IntProviderType.CONSTANT;
	}

	public String toString() {
		return Integer.toString(this.value);
	}
}
