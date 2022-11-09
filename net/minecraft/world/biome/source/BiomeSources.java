/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registry;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.CheckerboardBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;

public class BiomeSources {
    public static Codec<? extends BiomeSource> registerAndGetDefault(Registry<Codec<? extends BiomeSource>> registry) {
        Registry.register(registry, "fixed", FixedBiomeSource.CODEC);
        Registry.register(registry, "multi_noise", MultiNoiseBiomeSource.CODEC);
        Registry.register(registry, "checkerboard", CheckerboardBiomeSource.CODEC);
        return Registry.register(registry, "the_end", TheEndBiomeSource.CODEC);
    }
}

