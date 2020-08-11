/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;

public class EmptyChunk
extends WorldChunk {
    private static final Biome[] BIOMES = Util.make(new Biome[BiomeArray.DEFAULT_LENGTH], biomes -> Arrays.fill(biomes, BuiltinBiomes.PLAINS));

    public EmptyChunk(World world, ChunkPos pos) {
        super(world, pos, new BiomeArray(world.getRegistryManager().get(Registry.BIOME_KEY), BIOMES));
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
    @Nullable
    public LightingProvider getLightingProvider() {
        return null;
    }

    @Override
    public int getLuminance(BlockPos pos) {
        return 0;
    }

    @Override
    public void addEntity(Entity entity) {
    }

    @Override
    public void remove(Entity entity) {
    }

    @Override
    public void remove(Entity entity, int section) {
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
    public void setBlockEntity(BlockPos pos, BlockEntity blockEntity) {
    }

    @Override
    public void removeBlockEntity(BlockPos pos) {
    }

    @Override
    public void markDirty() {
    }

    @Override
    public void collectOtherEntities(@Nullable Entity except, Box box, List<Entity> entityList, Predicate<? super Entity> predicate) {
    }

    @Override
    public <T extends Entity> void collectEntitiesByClass(Class<? extends T> entityClass, Box box, List<T> result, Predicate<? super T> predicate) {
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
}

