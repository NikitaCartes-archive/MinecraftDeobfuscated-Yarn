package net.minecraft.world.biome.layer;

public enum ContinentLayer implements InitLayer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j) {
		if (i == 0 && j == 0) {
			return 1;
		} else {
			return layerRandomnessSource.nextInt(10) == 0 ? 1 : BiomeLayers.OCEAN_ID;
		}
	}
}
