/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.minecraft.world.biome.Biome;

public final class SnowyTundraBiome
extends Biome {
    public SnowyTundraBiome(Biome.Settings settings) {
        super(settings);
    }

    @Override
    public float getMaxSpawnChance() {
        return 0.07f;
    }
}

