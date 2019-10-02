/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAccess;

public interface BiomeAccessType {
    public Biome getBiome(long var1, int var3, int var4, int var5, BiomeAccess.Storage var6);
}

