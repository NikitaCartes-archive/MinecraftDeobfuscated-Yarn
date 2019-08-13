package net.minecraft.world.biome.layer;

public enum IncreaseEdgeCurvatureLayer implements DiagonalCrossSamplingLayer {
	field_16058;

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
		if (!BiomeLayers.isShallowOcean(m)
			|| BiomeLayers.isShallowOcean(l) && BiomeLayers.isShallowOcean(k) && BiomeLayers.isShallowOcean(i) && BiomeLayers.isShallowOcean(j)) {
			if (!BiomeLayers.isShallowOcean(m)
				&& (BiomeLayers.isShallowOcean(l) || BiomeLayers.isShallowOcean(i) || BiomeLayers.isShallowOcean(k) || BiomeLayers.isShallowOcean(j))
				&& layerRandomnessSource.nextInt(5) == 0) {
				if (BiomeLayers.isShallowOcean(l)) {
					return m == 4 ? 4 : l;
				}

				if (BiomeLayers.isShallowOcean(i)) {
					return m == 4 ? 4 : i;
				}

				if (BiomeLayers.isShallowOcean(k)) {
					return m == 4 ? 4 : k;
				}

				if (BiomeLayers.isShallowOcean(j)) {
					return m == 4 ? 4 : j;
				}
			}

			return m;
		} else {
			int n = 1;
			int o = 1;
			if (!BiomeLayers.isShallowOcean(l) && layerRandomnessSource.nextInt(n++) == 0) {
				o = l;
			}

			if (!BiomeLayers.isShallowOcean(k) && layerRandomnessSource.nextInt(n++) == 0) {
				o = k;
			}

			if (!BiomeLayers.isShallowOcean(i) && layerRandomnessSource.nextInt(n++) == 0) {
				o = i;
			}

			if (!BiomeLayers.isShallowOcean(j) && layerRandomnessSource.nextInt(n++) == 0) {
				o = j;
			}

			if (layerRandomnessSource.nextInt(3) == 0) {
				return o;
			} else {
				return o == 4 ? 4 : m;
			}
		}
	}
}
