package net.minecraft.world.biome.layer;

public enum AddColdClimatesLayer implements SouthEastSamplingLayer {
	field_16059;

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, int i) {
		if (BiomeLayers.isShallowOcean(i)) {
			return i;
		} else {
			int j = layerRandomnessSource.nextInt(6);
			if (j == 0) {
				return 4;
			} else {
				return j == 1 ? 3 : 1;
			}
		}
	}
}
