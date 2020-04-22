/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.dimension;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import org.jetbrains.annotations.Nullable;

public class OverworldDimension
extends Dimension {
    public OverworldDimension(World world, DimensionType type) {
        super(world, type, 0.0f);
    }

    @Override
    public DimensionType getType() {
        return DimensionType.OVERWORLD;
    }

    public ChunkGenerator<? extends ChunkGeneratorConfig> createChunkGenerator() {
        return this.world.getLevelProperties().method_27421().createChunkGenerator(this.world);
    }

    @Override
    @Nullable
    public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean checkMobSpawnValidity) {
        for (int i = chunkPos.getStartX(); i <= chunkPos.getEndX(); ++i) {
            for (int j = chunkPos.getStartZ(); j <= chunkPos.getEndZ(); ++j) {
                BlockPos blockPos = this.getTopSpawningBlockPosition(i, j, checkMobSpawnValidity);
                if (blockPos == null) continue;
                return blockPos;
            }
        }
        return null;
    }

    @Override
    @Nullable
    public BlockPos getTopSpawningBlockPosition(int x, int z, boolean checkMobSpawnValidity) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, 0, z);
        Biome biome = this.world.getBiome(mutable);
        BlockState blockState = biome.getSurfaceConfig().getTopMaterial();
        if (checkMobSpawnValidity && !blockState.getBlock().isIn(BlockTags.VALID_SPAWN)) {
            return null;
        }
        WorldChunk worldChunk = this.world.getChunk(x >> 4, z >> 4);
        int i = worldChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, x & 0xF, z & 0xF);
        if (i < 0) {
            return null;
        }
        if (worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x & 0xF, z & 0xF) > worldChunk.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, x & 0xF, z & 0xF)) {
            return null;
        }
        for (int j = i + 1; j >= 0; --j) {
            mutable.set(x, j, z);
            BlockState blockState2 = this.world.getBlockState(mutable);
            if (!blockState2.getFluidState().isEmpty()) break;
            if (!blockState2.equals(blockState)) continue;
            return mutable.up().toImmutable();
        }
        return null;
    }

    @Override
    public float getSkyAngle(long timeOfDay, float tickDelta) {
        double d = MathHelper.fractionalPart((double)timeOfDay / 24000.0 - 0.25);
        double e = 0.5 - Math.cos(d * Math.PI) / 2.0;
        return (float)(d * 2.0 + e) / 3.0f;
    }

    @Override
    public boolean hasVisibleSky() {
        return true;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Vec3d modifyFogColor(Vec3d vec3d, float tickDelta) {
        return vec3d.multiply(tickDelta * 0.94f + 0.06f, tickDelta * 0.94f + 0.06f, tickDelta * 0.91f + 0.09f);
    }

    @Override
    public boolean canPlayersSleep() {
        return true;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isFogThick(int x, int z) {
        return false;
    }
}

