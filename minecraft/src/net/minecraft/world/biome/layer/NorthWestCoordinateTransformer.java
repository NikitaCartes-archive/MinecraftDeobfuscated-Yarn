package net.minecraft.world.biome.layer;

public interface NorthWestCoordinateTransformer extends CoordinateTransformer {
	@Override
	default int transformX(int x) {
		return x - 1;
	}

	@Override
	default int transformZ(int y) {
		return y - 1;
	}
}
