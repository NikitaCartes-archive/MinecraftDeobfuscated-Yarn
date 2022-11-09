/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public interface BiomeSupplier {
    public RegistryEntry<Biome> getBiome(int var1, int var2, int var3, MultiNoiseUtil.MultiNoiseSampler var4);
}

