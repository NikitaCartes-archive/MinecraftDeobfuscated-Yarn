package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.BiomeIds;
import net.minecraft.world.biome.layer.type.DiagonalCrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum IncreaseEdgeCurvatureLayer implements DiagonalCrossSamplingLayer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, int sw, int se, int ne, int nw, int center) {
		if (!BiomeLayers.isShallowOcean(center)
			|| BiomeLayers.isShallowOcean(nw) && BiomeLayers.isShallowOcean(ne) && BiomeLayers.isShallowOcean(sw) && BiomeLayers.isShallowOcean(se)) {
			if (!BiomeLayers.isShallowOcean(center)
				&& (BiomeLayers.isShallowOcean(nw) || BiomeLayers.isShallowOcean(sw) || BiomeLayers.isShallowOcean(ne) || BiomeLayers.isShallowOcean(se))
				&& context.nextInt(5) == 0) {
				if (BiomeLayers.isShallowOcean(nw)) {
					return center == BiomeIds.FOREST ? BiomeIds.FOREST : nw;
				}

				if (BiomeLayers.isShallowOcean(sw)) {
					return center == BiomeIds.FOREST ? BiomeIds.FOREST : sw;
				}

				if (BiomeLayers.isShallowOcean(ne)) {
					return center == BiomeIds.FOREST ? BiomeIds.FOREST : ne;
				}

				if (BiomeLayers.isShallowOcean(se)) {
					return center == BiomeIds.FOREST ? BiomeIds.FOREST : se;
				}
			}

			return center;
		} else {
			int i = 1;
			int j = BiomeIds.PLAINS;
			if (!BiomeLayers.isShallowOcean(nw) && context.nextInt(i++) == 0) {
				j = nw;
			}

			if (!BiomeLayers.isShallowOcean(ne) && context.nextInt(i++) == 0) {
				j = ne;
			}

			if (!BiomeLayers.isShallowOcean(sw) && context.nextInt(i++) == 0) {
				j = sw;
			}

			if (!BiomeLayers.isShallowOcean(se) && context.nextInt(i++) == 0) {
				j = se;
			}

			if (context.nextInt(3) == 0) {
				return j;
			} else {
				return j == BiomeIds.FOREST ? BiomeIds.FOREST : center;
			}
		}
	}
}
