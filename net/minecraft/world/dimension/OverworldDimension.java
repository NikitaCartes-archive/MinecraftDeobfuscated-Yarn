/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.dimension;

import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
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

    @Override
    @Nullable
    public BlockPos getSpawningBlockInChunk(long l, ChunkPos chunkPos, boolean bl) {
        for (int i = chunkPos.getStartX(); i <= chunkPos.getEndX(); ++i) {
            for (int j = chunkPos.getStartZ(); j <= chunkPos.getEndZ(); ++j) {
                BlockPos blockPos = this.getTopSpawningBlockPosition(l, i, j, bl);
                if (blockPos == null) continue;
                return blockPos;
            }
        }
        return null;
    }

    @Override
    @Nullable
    public BlockPos getTopSpawningBlockPosition(long l, int i, int j, boolean bl) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(i, 0, j);
        Biome biome = this.world.getBiome(mutable);
        BlockState blockState = biome.getSurfaceConfig().getTopMaterial();
        if (bl && !blockState.getBlock().isIn(BlockTags.VALID_SPAWN)) {
            return null;
        }
        WorldChunk worldChunk = this.world.getChunk(i >> 4, j >> 4);
        int k = worldChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, i & 0xF, j & 0xF);
        if (k < 0) {
            return null;
        }
        if (worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i & 0xF, j & 0xF) > worldChunk.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, i & 0xF, j & 0xF)) {
            return null;
        }
        for (int m = k + 1; m >= 0; --m) {
            mutable.set(i, m, j);
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
    public boolean canPlayersSleep() {
        return true;
    }
}

