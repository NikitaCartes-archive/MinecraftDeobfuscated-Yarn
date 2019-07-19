/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import net.minecraft.world.biome.source.BiomeSourceConfig;

public class TheEndBiomeSourceConfig
implements BiomeSourceConfig {
    private long seed;

    public TheEndBiomeSourceConfig setSeed(long l) {
        this.seed = l;
        return this;
    }

    public long getSeed() {
        return this.seed;
    }
}

