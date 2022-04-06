package net.minecraft.util.math.floatprovider;

import net.minecraft.util.math.random.AbstractRandom;

public interface FloatSupplier {
	float get(AbstractRandom random);
}
