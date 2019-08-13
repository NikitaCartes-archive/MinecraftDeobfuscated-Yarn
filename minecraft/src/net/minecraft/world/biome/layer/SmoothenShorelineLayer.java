package net.minecraft.world.biome.layer;

public enum SmoothenShorelineLayer implements CrossSamplingLayer {
	field_16171;

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
		boolean bl = j == l;
		boolean bl2 = i == k;
		if (bl == bl2) {
			if (bl) {
				return layerRandomnessSource.nextInt(2) == 0 ? l : i;
			} else {
				return m;
			}
		} else {
			return bl ? l : i;
		}
	}
}
