/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.dimension;

import net.minecraft.class_5268;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

public abstract class Dimension {
    public static final float[] MOON_PHASE_TO_SIZE = new float[]{1.0f, 0.75f, 0.5f, 0.25f, 0.0f, 0.25f, 0.5f, 0.75f};
    protected final World world;
    private final DimensionType type;
    protected final float[] lightLevelToBrightness = new float[16];

    public Dimension(World world, DimensionType type, float f) {
        this.world = world;
        this.type = type;
        for (int i = 0; i <= 15; ++i) {
            float g = (float)i / 15.0f;
            float h = g / (4.0f - 3.0f * g);
            this.lightLevelToBrightness[i] = MathHelper.lerp(f, h, 1.0f);
        }
    }

    public int getMoonPhase(long time) {
        return (int)(time / 24000L % 8L + 8L) % 8;
    }

    public float getBrightness(int lightLevel) {
        return this.lightLevelToBrightness[lightLevel];
    }

    public abstract float getSkyAngle(long var1, float var3);

    public WorldBorder createWorldBorder() {
        return new WorldBorder();
    }

    public abstract DimensionType getType();

    @Nullable
    public BlockPos getForcedSpawnPoint() {
        return null;
    }

    public void saveWorldData(class_5268 arg) {
    }

    public void update() {
    }

    @Nullable
    public abstract BlockPos getSpawningBlockInChunk(long var1, ChunkPos var3, boolean var4);

    @Nullable
    public abstract BlockPos getTopSpawningBlockPosition(long var1, int var3, int var4, boolean var5);

    public abstract boolean hasVisibleSky();

    public abstract boolean canPlayersSleep();
}

