package net.minecraft.util.math;

public interface NorthWestCoordinateTransformer extends CoordinateTransformer {
	@Override
	default int transformX(int i) {
		return i - 1;
	}

	@Override
	default int transformY(int i) {
		return i - 1;
	}
}
