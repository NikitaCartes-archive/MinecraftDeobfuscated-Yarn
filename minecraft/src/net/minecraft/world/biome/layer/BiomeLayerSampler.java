package net.minecraft.world.biome.layer;

import net.minecraft.SharedConstants;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeLayerSampler {
	private static final Logger LOGGER = LogManager.getLogger();
	private final CachingLayerSampler sampler;

	public BiomeLayerSampler(LayerFactory<CachingLayerSampler> layerFactory) {
		this.sampler = layerFactory.make();
	}

	public Biome[] sample(int i, int j, int k, int l) {
		Biome[] biomes = new Biome[k * l];

		for (int m = 0; m < l; m++) {
			for (int n = 0; n < k; n++) {
				int o = this.sampler.sample(i + n, j + m);
				Biome biome = this.getBiome(o);
				biomes[n + m * k] = biome;
			}
		}

		return biomes;
	}

	private Biome getBiome(int i) {
		Biome biome = Registry.BIOME.get(i);
		if (biome == null) {
			if (SharedConstants.isDevelopment) {
				throw new IllegalStateException("Unknown biome id: " + i);
			} else {
				LOGGER.warn("Unknown biome id: ", i);
				return Biomes.DEFAULT;
			}
		} else {
			return biome;
		}
	}

	public Biome sample(int i, int j) {
		return this.getBiome(this.sampler.sample(i, j));
	}
}
