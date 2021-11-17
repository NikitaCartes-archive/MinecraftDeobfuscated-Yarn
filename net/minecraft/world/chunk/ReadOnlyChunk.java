/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.tick.BasicTickScheduler;
import net.minecraft.world.tick.EmptyTickSchedulers;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a read only view of a world chunk used in world generation.
 */
public class ReadOnlyChunk
extends ProtoChunk {
    private final WorldChunk wrapped;
    private final boolean field_34554;

    public ReadOnlyChunk(WorldChunk wrapped, boolean bl) {
        super(wrapped.getPos(), UpgradeData.NO_UPGRADE_DATA, wrapped.heightLimitView, wrapped.getWorld().getRegistryManager().get(Registry.BIOME_KEY), wrapped.getBlender());
        this.wrapped = wrapped;
        this.field_34554 = bl;
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        return this.wrapped.getBlockEntity(pos);
    }

    @Override
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
    public ChunkSection getSection(int yIndex) {
        if (this.field_34554) {
            return this.wrapped.getSection(yIndex);
        }
        return super.getSection(yIndex);
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
        if (this.field_34554) {
            return this.wrapped.setBlockState(pos, state, moved);
        }
        return null;
    }

    @Override
    public void setBlockEntity(BlockEntity blockEntity) {
        if (this.field_34554) {
            this.wrapped.setBlockEntity(blockEntity);
        }
    }

    @Override
    public void addEntity(Entity entity) {
        if (this.field_34554) {
            this.wrapped.addEntity(entity);
        }
    }

    @Override
    public void setStatus(ChunkStatus status) {
        if (this.field_34554) {
            super.setStatus(status);
        }
    }

    @Override
    public ChunkSection[] getSectionArray() {
        return this.wrapped.getSectionArray();
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
    public Heightmap getHeightmap(Heightmap.Type type) {
        return this.wrapped.getHeightmap(type);
    }

    @Override
    public int sampleHeightmap(Heightmap.Type type, int x, int z) {
        return this.wrapped.sampleHeightmap(this.transformHeightmapType(type), x, z);
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.wrapped.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
    }

    @Override
    public ChunkPos getPos() {
        return this.wrapped.getPos();
    }

    @Override
    @Nullable
    public StructureStart<?> getStructureStart(StructureFeature<?> structure) {
        return this.wrapped.getStructureStart(structure);
    }

    @Override
    public void setStructureStart(StructureFeature<?> structure, StructureStart<?> start) {
    }

    @Override
    public Map<StructureFeature<?>, StructureStart<?>> getStructureStarts() {
        return this.wrapped.getStructureStarts();
    }

    @Override
    public void setStructureStarts(Map<StructureFeature<?>, StructureStart<?>> structureStarts) {
    }

    @Override
    public LongSet getStructureReferences(StructureFeature<?> structure) {
        return this.wrapped.getStructureReferences(structure);
    }

    @Override
    public void addStructureReference(StructureFeature<?> structure, long reference) {
    }

    @Override
    public Map<StructureFeature<?>, LongSet> getStructureReferences() {
        return this.wrapped.getStructureReferences();
    }

    @Override
    public void setStructureReferences(Map<StructureFeature<?>, LongSet> structureReferences) {
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
    public void addPendingBlockEntityNbt(NbtCompound nbt) {
    }

    @Override
    @Nullable
    public NbtCompound getBlockEntityNbt(BlockPos pos) {
        return this.wrapped.getBlockEntityNbt(pos);
    }

    @Override
    @Nullable
    public NbtCompound getPackedBlockEntityNbt(BlockPos pos) {
        return this.wrapped.getPackedBlockEntityNbt(pos);
    }

    @Override
    public Stream<BlockPos> getLightSourcesStream() {
        return this.wrapped.getLightSourcesStream();
    }

    @Override
    public BasicTickScheduler<Block> getBlockTickScheduler() {
        if (this.field_34554) {
            return this.wrapped.getBlockTickScheduler();
        }
        return EmptyTickSchedulers.getReadOnlyTickScheduler();
    }

    @Override
    public BasicTickScheduler<Fluid> getFluidTickScheduler() {
        if (this.field_34554) {
            return this.wrapped.getFluidTickScheduler();
        }
        return EmptyTickSchedulers.getReadOnlyTickScheduler();
    }

    @Override
    public Chunk.TickSchedulers getTickSchedulers() {
        return this.wrapped.getTickSchedulers();
    }

    @Override
    @Nullable
    public Blender getBlender() {
        return this.wrapped.getBlender();
    }

    @Override
    public void setBlender(Blender blender) {
        this.wrapped.setBlender(blender);
    }

    @Override
    public CarvingMask getCarvingMask(GenerationStep.Carver carver) {
        if (this.field_34554) {
            return super.getCarvingMask(carver);
        }
        throw Util.throwOrPause(new UnsupportedOperationException("Meaningless in this context"));
    }

    @Override
    public CarvingMask getOrCreateCarvingMask(GenerationStep.Carver carver) {
        if (this.field_34554) {
            return super.getOrCreateCarvingMask(carver);
        }
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
    public void method_38257(BiomeSupplier biomeSupplier, MultiNoiseUtil.MultiNoiseSampler sampler) {
        if (this.field_34554) {
            this.wrapped.method_38257(biomeSupplier, sampler);
        }
    }
}

