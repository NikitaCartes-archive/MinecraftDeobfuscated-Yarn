package net.minecraft.world.biome.layer;

public enum AddColdClimatesLayer implements SouthEastSamplingLayer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, int se) {
		if (BiomeLayers.isShallowOcean(se)) {
			return se;
		} else {
			int i = context.nextInt(6);
			if (i == 0) {
				return 4;
			} else {
				return i == 1 ? 3 : 1;
			}
		}
	}
}
