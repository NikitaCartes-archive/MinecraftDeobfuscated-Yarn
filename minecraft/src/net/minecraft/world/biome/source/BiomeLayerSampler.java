package net.minecraft.world.biome.source;

import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeLayerSampler {
	private static final Logger LOGGER = LogManager.getLogger();
	private final CachingLayerSampler sampler;

	public BiomeLayerSampler(LayerFactory<CachingLayerSampler> layerFactory) {
		this.sampler = layerFactory.make();
	}

	private Biome getBiome(int id) {
		Biome biome = Registry.BIOME.get(id);
		if (biome == null) {
			if (SharedConstants.isDevelopment) {
				throw (IllegalStateException)Util.throwOrPause(new IllegalStateException("Unknown biome id: " + id));
			} else {
				LOGGER.warn("Unknown biome id: ", id);
				return Biomes.DEFAULT;
			}
		} else {
			return biome;
		}
	}

	public Biome sample(int x, int y) {
		return this.getBiome(this.sampler.sample(x, y));
	}
}
