/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import org.jetbrains.annotations.Nullable;

public class ReadOnlyChunk
extends ProtoChunk {
    private final WorldChunk wrapped;

    public ReadOnlyChunk(WorldChunk wrapped) {
        super(wrapped.getPos(), UpgradeData.NO_UPGRADE_DATA);
        this.wrapped = wrapped;
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        return this.wrapped.getBlockEntity(pos);
    }

    @Override
    @Nullable
    public BlockState getBlockState(BlockPos pos) {
        return this.wrapped.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.wrapped.getFluidState(pos);
    }

    @Override
    public int getMaxLightLevel() {
        return this.wrapped.getMaxLightLevel();
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
        return null;
    }

    @Override
    public void setBlockEntity(BlockPos pos, BlockEntity blockEntity) {
    }

    @Override
    public void addEntity(Entity entity) {
    }

    @Override
    public void setStatus(ChunkStatus status) {
    }

    @Override
    public ChunkSection[] getSectionArray() {
        return this.wrapped.getSectionArray();
    }

    @Override
    @Nullable
    public LightingProvider getLightingProvider() {
        return this.wrapped.getLightingProvider();
    }

    @Override
    public void setHeightmap(Heightmap.Type type, long[] heightmap) {
    }

    private Heightmap.Type transformHeightmapType(Heightmap.Type type) {
        if (type == Heightmap.Type.WORLD_SURFACE_WG) {
            return Heightmap.Type.WORLD_SURFACE;
        }
        if (type == Heightmap.Type.OCEAN_FLOOR_WG) {
            return Heightmap.Type.OCEAN_FLOOR;
        }
        return type;
    }

    @Override
    public int sampleHeightmap(Heightmap.Type type, int x, int z) {
        return this.wrapped.sampleHeightmap(this.transformHeightmapType(type), x, z);
    }

    @Override
    public ChunkPos getPos() {
        return this.wrapped.getPos();
    }

    @Override
    public void setLastSaveTime(long lastSaveTime) {
    }

    @Override
    @Nullable
    public StructureStart<?> getStructureStart(String structure) {
        return this.wrapped.getStructureStart(structure);
    }

    @Override
    public void setStructureStart(String structure, StructureStart<?> start) {
    }

    @Override
    public Map<String, StructureStart<?>> getStructureStarts() {
        return this.wrapped.getStructureStarts();
    }

    @Override
    public void setStructureStarts(Map<String, StructureStart<?>> map) {
    }

    @Override
    public LongSet getStructureReferences(String structure) {
        return this.wrapped.getStructureReferences(structure);
    }

    @Override
    public void addStructureReference(String structure, long reference) {
    }

    @Override
    public Map<String, LongSet> getStructureReferences() {
        return this.wrapped.getStructureReferences();
    }

    @Override
    public void setStructureReferences(Map<String, LongSet> structureReferences) {
    }

    @Override
    public BiomeArray getBiomeArray() {
        return this.wrapped.getBiomeArray();
    }

    @Override
    public void setShouldSave(boolean shouldSave) {
    }

    @Override
    public boolean needsSaving() {
        return false;
    }

    @Override
    public ChunkStatus getStatus() {
        return this.wrapped.getStatus();
    }

    @Override
    public void removeBlockEntity(BlockPos pos) {
    }

    @Override
    public void markBlockForPostProcessing(BlockPos pos) {
    }

    @Override
    public void addPendingBlockEntityTag(CompoundTag tag) {
    }

    @Override
    @Nullable
    public CompoundTag getBlockEntityTagAt(BlockPos pos) {
        return this.wrapped.getBlockEntityTagAt(pos);
    }

    @Override
    @Nullable
    public CompoundTag method_20598(BlockPos blockPos) {
        return this.wrapped.method_20598(blockPos);
    }

    @Override
    public void setBiomes(BiomeArray biomeArray) {
    }

    @Override
    public Stream<BlockPos> getLightSourcesStream() {
        return this.wrapped.getLightSourcesStream();
    }

    @Override
    public ChunkTickScheduler<Block> getBlockTickScheduler() {
        return new ChunkTickScheduler<Block>(block -> block.getDefaultState().isAir(), this.getPos());
    }

    @Override
    public ChunkTickScheduler<Fluid> getFluidTickScheduler() {
        return new ChunkTickScheduler<Fluid>(fluid -> fluid == Fluids.EMPTY, this.getPos());
    }

    @Override
    public BitSet getCarvingMask(GenerationStep.Carver carver) {
        throw Util.throwOrPause(new UnsupportedOperationException("Meaningless in this context"));
    }

    @Override
    public BitSet method_28510(GenerationStep.Carver carver) {
        throw Util.throwOrPause(new UnsupportedOperationException("Meaningless in this context"));
    }

    public WorldChunk getWrappedChunk() {
        return this.wrapped;
    }

    @Override
    public boolean isLightOn() {
        return this.wrapped.isLightOn();
    }

    @Override
    public void setLightOn(boolean lightOn) {
        this.wrapped.setLightOn(lightOn);
    }

    @Override
    public /* synthetic */ TickScheduler getFluidTickScheduler() {
        return this.getFluidTickScheduler();
    }

    @Override
    public /* synthetic */ TickScheduler getBlockTickScheduler() {
        return this.getBlockTickScheduler();
    }
}

