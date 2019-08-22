package net.minecraft.world.biome.layer;

public interface NorthWestCoordinateTransformer extends CoordinateTransformer {
	@Override
	default int transformX(int i) {
		return i - 1;
	}

	@Override
	default int transformZ(int i) {
		return i - 1;
	}
}
