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

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public float[] getBackgroundColor(float skyAngle, float tickDelta) {
        float f = 0.4f;
        float g = MathHelper.cos(skyAngle * ((float)Math.PI * 2)) - 0.0f;
        float h = -0.0f;
        if (g >= -0.4f && g <= 0.4f) {
            float i = (g - -0.0f) / 0.4f * 0.5f + 0.5f;
            float j = 1.0f - (1.0f - MathHelper.sin(i * (float)Math.PI)) * 0.99f;
            j *= j;
            this.backgroundColor[0] = i * 0.3f + 0.7f;
            this.backgroundColor[1] = i * i * 0.7f + 0.2f;
            this.backgroundColor[2] = i * i * 0.0f + 0.2f;
            this.backgroundColor[3] = j;
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

    public float getBrightness(int lightLevel) {
        return this.lightLevelToBrightness[lightLevel];
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

    /**
     * Modify the fog color offered (usually by the biome).
     * 
     * <p>The overworld slightly whiteshifts and blueshifts this color; the
     * nether doesn't touch it; the end significantly blackshifts this color.
     */
    @Environment(value=EnvType.CLIENT)
    public abstract Vec3d modifyFogColor(int var1, float var2);

    public abstract boolean canPlayersSleep();

    @Environment(value=EnvType.CLIENT)
    public abstract boolean isFogThick(int var1, int var2);

    public abstract DimensionType getType();
}

