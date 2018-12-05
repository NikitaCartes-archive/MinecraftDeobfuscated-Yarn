package net.minecraft;

import net.minecraft.util.math.CoordinateTransformer;

public interface class_3739 extends CoordinateTransformer {
	@Override
	default int transformX(int i) {
		return i - 1;
	}

	@Override
	default int transformY(int i) {
		return i - 1;
	}
}
