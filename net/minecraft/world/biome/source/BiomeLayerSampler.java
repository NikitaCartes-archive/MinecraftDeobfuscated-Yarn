/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;
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

    public Biome sample(Registry<Biome> registry, int i, int j) {
        int k = this.sampler.sample(i, j);
        RegistryKey<Biome> registryKey = BuiltinBiomes.fromRawId(k);
        if (registryKey == null) {
            throw new IllegalStateException("Unknown biome id emitted by layers: " + k);
        }
        Biome biome = registry.get(registryKey);
        if (biome == null) {
            if (SharedConstants.isDevelopment) {
                throw Util.throwOrPause(new IllegalStateException("Unknown biome id: " + k));
            }
            LOGGER.warn("Unknown biome id: {}", (Object)k);
            return registry.get(BuiltinBiomes.fromRawId(0));
        }
        return biome;
    }
}

