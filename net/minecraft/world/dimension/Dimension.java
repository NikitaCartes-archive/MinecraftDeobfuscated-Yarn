/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.dimension;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelGeneratorType;
import org.jetbrains.annotations.Nullable;

public abstract class Dimension {
    public static final float[] MOON_PHASE_TO_SIZE = new float[]{1.0f, 0.75f, 0.5f, 0.25f, 0.0f, 0.25f, 0.5f, 0.75f};
    protected final World world;
    private final DimensionType type;
    protected boolean waterVaporizes;
    protected boolean isNether;
    protected final float[] lightLevelToBrightness = new float[16];
    private final float[] backgroundColor = new float[4];

    public Dimension(World world, DimensionType dimensionType, float f) {
        this.world = world;
        this.type = dimensionType;
        for (int i = 0; i <= 15; ++i) {
            float g = (float)i / 15.0f;
            float h = g / (4.0f - 3.0f * g);
            this.lightLevelToBrightness[i] = MathHelper.lerp(f, h, 1.0f);
        }
    }

    public int getMoonPhase(long l) {
        return (int)(l / 24000L % 8L + 8L) % 8;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public float[] getBackgroundColor(float f, float g) {
        float h = 0.4f;
        float i = MathHelper.cos(f * ((float)Math.PI * 2)) - 0.0f;
        float j = -0.0f;
        if (i >= -0.4f && i <= 0.4f) {
            float k = (i - -0.0f) / 0.4f * 0.5f + 0.5f;
            float l = 1.0f - (1.0f - MathHelper.sin(k * (float)Math.PI)) * 0.99f;
            l *= l;
            this.backgroundColor[0] = k * 0.3f + 0.7f;
            this.backgroundColor[1] = k * k * 0.7f + 0.2f;
            this.backgroundColor[2] = k * k * 0.0f + 0.2f;
            this.backgroundColor[3] = l;
            return this.backgroundColor;
        }
        return null;
    }

    @Environment(value=EnvType.CLIENT)
    public float getCloudHeight() {
        return 128.0f;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasGround() {
        return true;
    }

    @Nullable
    public BlockPos getForcedSpawnPoint() {
        return null;
    }

    @Environment(value=EnvType.CLIENT)
    public double getHorizonShadingRatio() {
        if (this.world.getLevelProperties().getGeneratorType() == LevelGeneratorType.FLAT) {
            return 1.0;
        }
        return 0.03125;
    }

    public boolean doesWaterVaporize() {
        return this.waterVaporizes;
    }

    public boolean hasSkyLight() {
        return this.type.hasSkyLight();
    }

    public boolean isNether() {
        return this.isNether;
    }

    public float getBrightness(int i) {
        return this.lightLevelToBrightness[i];
    }

    public WorldBorder createWorldBorder() {
        return new WorldBorder();
    }

    public void saveWorldData() {
    }

    public void update() {
    }

    public abstract ChunkGenerator<?> createChunkGenerator();

    @Nullable
    public abstract BlockPos getSpawningBlockInChunk(ChunkPos var1, boolean var2);

    @Nullable
    public abstract BlockPos getTopSpawningBlockPosition(int var1, int var2, boolean var3);

    public abstract float getSkyAngle(long var1, float var3);

    public abstract boolean hasVisibleSky();

    @Environment(value=EnvType.CLIENT)
    public abstract Vec3d getFogColor(float var1, float var2);

    public abstract boolean canPlayersSleep();

    @Environment(value=EnvType.CLIENT)
    public abstract boolean isFogThick(int var1, int var2);

    public abstract DimensionType getType();
}

