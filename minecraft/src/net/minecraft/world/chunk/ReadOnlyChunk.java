package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
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
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * Represents a read only view of a world chunk used in world generation.
 */
public class ReadOnlyChunk extends ProtoChunk {
	private final WorldChunk wrapped;

	public ReadOnlyChunk(WorldChunk wrapped) {
		super(wrapped.getPos(), UpgradeData.NO_UPGRADE_DATA, wrapped);
		this.wrapped = wrapped;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return this.wrapped.getBlockEntity(pos);
	}

	@Nullable
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

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
		return null;
	}

	@Override
	public void setBlockEntity(BlockEntity blockEntity) {
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
	public void setHeightmap(Heightmap.Type type, long[] heightmap) {
	}

	private Heightmap.Type transformHeightmapType(Heightmap.Type type) {
		if (type == Heightmap.Type.WORLD_SURFACE_WG) {
			return Heightmap.Type.WORLD_SURFACE;
		} else {
			return type == Heightmap.Type.OCEAN_FLOOR_WG ? Heightmap.Type.OCEAN_FLOOR : type;
		}
	}

	@Override
	public int sampleHeightmap(Heightmap.Type type, int x, int z) {
		return this.wrapped.sampleHeightmap(this.transformHeightmapType(type), x, z);
	}

	@Override
	public ChunkPos getPos() {
		return this.wrapped.getPos();
	}

	@Nullable
	@Override
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

	@Nullable
	@Override
	public CompoundTag getBlockEntityTag(BlockPos pos) {
		return this.wrapped.getBlockEntityTag(pos);
	}

	@Nullable
	@Override
	public CompoundTag getPackedBlockEntityTag(BlockPos pos) {
		return this.wrapped.getPackedBlockEntityTag(pos);
	}

	@Override
	public void setBiomes(BiomeArray biomes) {
	}

	@Override
	public Stream<BlockPos> getLightSourcesStream() {
		return this.wrapped.getLightSourcesStream();
	}

	@Override
	public ChunkTickScheduler<Block> getBlockTickScheduler() {
		return new ChunkTickScheduler<>(block -> block.getDefaultState().isAir(), this.getPos(), this);
	}

	@Override
	public ChunkTickScheduler<Fluid> getFluidTickScheduler() {
		return new ChunkTickScheduler<>(fluid -> fluid == Fluids.EMPTY, this.getPos(), this);
	}

	@Override
	public BitSet getCarvingMask(GenerationStep.Carver carver) {
		throw (UnsupportedOperationException)Util.throwOrPause(new UnsupportedOperationException("Meaningless in this context"));
	}

	@Override
	public BitSet getOrCreateCarvingMask(GenerationStep.Carver carver) {
		throw (UnsupportedOperationException)Util.throwOrPause(new UnsupportedOperationException("Meaningless in this context"));
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
}
