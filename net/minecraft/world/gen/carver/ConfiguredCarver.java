/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;

public class ConfiguredCarver<WC extends CarverConfig> {
    public final Carver<WC> carver;
    public final WC config;

    public ConfiguredCarver(Carver<WC> carver, WC carverConfig) {
        this.carver = carver;
        this.config = carverConfig;
    }

    public boolean shouldCarve(Random random, int i, int j) {
        return this.carver.shouldCarve(random, i, j, this.config);
    }

    public boolean carve(Chunk chunk, Random random, int i, int j, int k, int l, int m, BitSet bitSet) {
        return this.carver.carve(chunk, random, i, j, k, l, m, bitSet, this.config);
    }
}

