/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

public class EmptyChunk
extends WorldChunk {
    public EmptyChunk(World world, ChunkPos pos) {
        super(world, pos, new class_5706(world));
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return Blocks.VOID_AIR.getDefaultState();
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
        return null;
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return Fluids.EMPTY.getDefaultState();
    }

    @Override
    public int getLuminance(BlockPos pos) {
        return 0;
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos, WorldChunk.CreationType creationType) {
        return null;
    }

    @Override
    public void addBlockEntity(BlockEntity blockEntity) {
    }

    @Override
    public void setBlockEntity(BlockEntity blockEntity) {
    }

    @Override
    public void removeBlockEntity(BlockPos pos) {
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean areSectionsEmptyBetween(int lowerHeight, int upperHeight) {
        return true;
    }

    @Override
    public ChunkHolder.LevelType getLevelType() {
        return ChunkHolder.LevelType.BORDER;
    }

    static class class_5706
    extends BiomeArray {
        private static final Biome[] field_28128 = new Biome[0];

        public class_5706(World world) {
            super(world.getRegistryManager().get(Registry.BIOME_KEY), (HeightLimitView)world, field_28128);
        }

        @Override
        public int[] toIntArray() {
            throw new UnsupportedOperationException("Can not write biomes of an empty chunk");
        }

        @Override
        public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
            return BuiltinBiomes.PLAINS;
        }
    }
}

