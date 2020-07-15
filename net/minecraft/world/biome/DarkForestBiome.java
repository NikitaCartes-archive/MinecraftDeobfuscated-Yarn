/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.Biome;

public final class DarkForestBiome
extends Biome {
    public DarkForestBiome(Biome.Settings settings) {
        super(settings);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getGrassColorAt(double x, double z) {
        int i = super.getGrassColorAt(x, z);
        return (i & 0xFEFEFE) + 2634762 >> 1;
    }
}

