/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.SharedConstants;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.CachingLayerSampler;
import net.minecraft.world.biome.layer.LayerFactory;
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
        for (int m = 0; m < l; ++m) {
            for (int n = 0; n < k; ++n) {
                Biome biome;
                int o = this.sampler.sample(i + n, j + m);
                biomes[n + m * k] = biome = this.getBiome(o);
            }
        }
        return biomes;
    }

    private Biome getBiome(int i) {
        Biome biome = (Biome)Registry.BIOME.get(i);
        if (biome == null) {
            if (SharedConstants.isDevelopment) {
                throw SystemUtil.method_22320(new IllegalStateException("Unknown biome id: " + i));
            }
            LOGGER.warn("Unknown biome id: ", (Object)i);
            return Biomes.DEFAULT;
        }
        return biome;
    }

    public Biome sample(int i, int j) {
        return this.getBiome(this.sampler.sample(i, j));
    }
}

