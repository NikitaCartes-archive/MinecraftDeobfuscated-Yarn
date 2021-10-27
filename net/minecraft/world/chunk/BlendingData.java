/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public final class BlendingData {
    private final boolean oldBiome;
    private final boolean oldNoise;

    public BlendingData(boolean oldBiome, boolean oldNoise) {
        this.oldBiome = oldBiome;
        this.oldNoise = oldNoise;
    }

    @Nullable
    public static BlendingData fromNbt(NbtCompound nbt) {
        if (nbt.isEmpty()) {
            return null;
        }
        return new BlendingData(nbt.getBoolean("old_biome"), nbt.getBoolean("old_noise"));
    }

    public NbtCompound toNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putBoolean("old_biome", this.oldBiome);
        nbtCompound.putBoolean("old_noise", this.oldNoise);
        return nbtCompound;
    }

    public boolean isOldBiome() {
        return this.oldBiome;
    }

    public boolean isOldNoise() {
        return this.oldNoise;
    }
}

