/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.dimension;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.CavesChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import org.jetbrains.annotations.Nullable;

public class TheNetherDimension
extends Dimension {
    private static final Vec3d field_21216 = new Vec3d(0.2f, 0.03f, 0.03f);

    public TheNetherDimension(World world, DimensionType dimensionType) {
        super(world, dimensionType);
        this.waterVaporizes = true;
        this.isNether = true;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Vec3d getFogColor(float f, float g) {
        return field_21216;
    }

    @Override
    protected void initializeLightLevelToBrightness() {
        float f = 0.1f;
        for (int i = 0; i <= 15; ++i) {
            float g = 1.0f - (float)i / 15.0f;
            this.lightLevelToBrightness[i] = (1.0f - g) / (g * 3.0f + 1.0f) * 0.9f + 0.1f;
        }
    }

    @Override
    public ChunkGenerator<?> createChunkGenerator() {
        CavesChunkGeneratorConfig cavesChunkGeneratorConfig = ChunkGeneratorType.CAVES.createSettings();
        cavesChunkGeneratorConfig.setDefaultBlock(Blocks.NETHERRACK.getDefaultState());
        cavesChunkGeneratorConfig.setDefaultFluid(Blocks.LAVA.getDefaultState());
        return ChunkGeneratorType.CAVES.create(this.world, BiomeSourceType.FIXED.applyConfig(BiomeSourceType.FIXED.getConfig(this.world.getLevelProperties()).setBiome(Biomes.NETHER)), cavesChunkGeneratorConfig);
    }

    @Override
    public boolean hasVisibleSky() {
        return false;
    }

    @Override
    @Nullable
    public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean bl) {
        return null;
    }

    @Override
    @Nullable
    public BlockPos getTopSpawningBlockPosition(int i, int j, boolean bl) {
        return null;
    }

    @Override
    public float getSkyAngle(long l, float f) {
        return 0.5f;
    }

    @Override
    public boolean canPlayersSleep() {
        return false;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderFog(int i, int j) {
        return true;
    }

    @Override
    public WorldBorder createWorldBorder() {
        return new WorldBorder(){

            @Override
            public double getCenterX() {
                return super.getCenterX() / 8.0;
            }

            @Override
            public double getCenterZ() {
                return super.getCenterZ() / 8.0;
            }
        };
    }

    @Override
    public DimensionType getType() {
        return DimensionType.THE_NETHER;
    }
}

