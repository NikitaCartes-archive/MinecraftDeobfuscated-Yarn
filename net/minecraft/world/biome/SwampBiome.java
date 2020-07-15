/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.Biome;

public final class SwampBiome
extends Biome {
    public SwampBiome(Biome.Settings settings) {
        super(settings);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getGrassColorAt(double x, double z) {
        double d = FOLIAGE_NOISE.sample(x * 0.0225, z * 0.0225, false);
        if (d < -0.1) {
            return 5011004;
        }
        return 6975545;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getFoliageColor() {
        return 6975545;
    }
}

