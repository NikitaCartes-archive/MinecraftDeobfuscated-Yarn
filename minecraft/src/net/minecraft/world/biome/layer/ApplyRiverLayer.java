package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

public enum ApplyRiverLayer implements MergingLayer, IdentityCoordinateTransformer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
		int i = sampler1.sample(this.transformX(x), this.transformZ(z));
		int j = sampler2.sample(this.transformX(x), this.transformZ(z));
		if (BiomeLayers.isOcean(i)) {
			return i;
		} else if (j == 7) {
			if (i == 12) {
				return 11;
			} else {
				return i != 14 && i != 15 ? j & 0xFF : 15;
			}
		} else {
			return i;
		}
	}
}
