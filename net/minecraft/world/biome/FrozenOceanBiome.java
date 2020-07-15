/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;

public final class FrozenOceanBiome
extends Biome {
    protected static final OctaveSimplexNoiseSampler field_26380 = new OctaveSimplexNoiseSampler(new ChunkRandom(3456L), ImmutableList.of(Integer.valueOf(-2), Integer.valueOf(-1), Integer.valueOf(0)));

    public FrozenOceanBiome(Biome.Settings settings) {
        super(settings);
    }

    @Override
    protected float computeTemperature(BlockPos blockPos) {
        double h;
        double e;
        float f = this.getTemperature();
        double d = field_26380.sample((double)blockPos.getX() * 0.05, (double)blockPos.getZ() * 0.05, false) * 7.0;
        double g = d + (e = FOLIAGE_NOISE.sample((double)blockPos.getX() * 0.2, (double)blockPos.getZ() * 0.2, false));
        if (g < 0.3 && (h = FOLIAGE_NOISE.sample((double)blockPos.getX() * 0.09, (double)blockPos.getZ() * 0.09, false)) < 0.8) {
            f = 0.2f;
        }
        if (blockPos.getY() > 64) {
            float i = (float)(TEMPERATURE_NOISE.sample((float)blockPos.getX() / 8.0f, (float)blockPos.getZ() / 8.0f, false) * 4.0);
            return f - (i + (float)blockPos.getY() - 64.0f) * 0.05f / 30.0f;
        }
        return f;
    }
}

