package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

public enum ApplyOceanTemperatureLayer implements MergingLayer, IdentityCoordinateTransformer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
		int i = sampler1.sample(this.transformX(x), this.transformZ(z));
		int j = sampler2.sample(this.transformX(x), this.transformZ(z));
		if (!BiomeLayers.isOcean(i)) {
			return i;
		} else {
			int k = 8;
			int l = 4;

			for (int m = -8; m <= 8; m += 4) {
				for (int n = -8; n <= 8; n += 4) {
					int o = sampler1.sample(this.transformX(x + m), this.transformZ(z + n));
					if (!BiomeLayers.isOcean(o)) {
						if (j == 44) {
							return 45;
						}

						if (j == 10) {
							return 46;
						}
					}
				}
			}

			if (i == 24) {
				if (j == 45) {
					return 48;
				}

				if (j == 0) {
					return 24;
				}

				if (j == 46) {
					return 49;
				}

				if (j == 10) {
					return 50;
				}
			}

			return j;
		}
	}
}
