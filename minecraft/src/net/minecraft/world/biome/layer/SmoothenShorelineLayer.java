package net.minecraft.world.biome.layer;

public enum SmoothenShorelineLayer implements CrossSamplingLayer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
		boolean bl = e == w;
		boolean bl2 = n == s;
		if (bl == bl2) {
			if (bl) {
				return context.nextInt(2) == 0 ? w : n;
			} else {
				return center;
			}
		} else {
			return bl ? w : n;
		}
	}
}
