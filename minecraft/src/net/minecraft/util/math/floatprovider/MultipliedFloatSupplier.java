package net.minecraft.util.math.floatprovider;

import java.util.Arrays;
import net.minecraft.util.math.random.Random;

public class MultipliedFloatSupplier implements FloatSupplier {
	private final FloatSupplier[] multipliers;

	public MultipliedFloatSupplier(FloatSupplier... multipliers) {
		this.multipliers = multipliers;
	}

	@Override
	public float get(Random random) {
		float f = 1.0F;

		for (FloatSupplier floatSupplier : this.multipliers) {
			f *= floatSupplier.get(random);
		}

		return f;
	}

	public String toString() {
		return "MultipliedFloats" + Arrays.toString(this.multipliers);
	}
}
