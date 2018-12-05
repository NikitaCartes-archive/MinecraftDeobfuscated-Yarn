package net.minecraft.world.biome.layer;

import net.minecraft.util.math.CoordinateTransformer;

public interface VoidCoordinateTransformer extends CoordinateTransformer {
	@Override
	default int transformX(int i) {
		return i;
	}

	@Override
	default int transformY(int i) {
		return i;
	}
}
