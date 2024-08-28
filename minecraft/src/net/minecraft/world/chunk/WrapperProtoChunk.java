package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.light.ChunkSkyLight;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.tick.BasicTickScheduler;
import net.minecraft.world.tick.EmptyTickSchedulers;

/**
 * Represents a chunk that wraps a world chunk, used in world generation.
 * This is usually read-only.
 */
public class WrapperProtoChunk extends ProtoChunk {
	private final WorldChunk wrapped;
	private final boolean propagateToWrapped;

	public WrapperProtoChunk(WorldChunk wrapped, boolean propagateToWrapped) {
		super(
			wrapped.getPos(),
			UpgradeData.NO_UPGRADE_DATA,
			wrapped.heightLimitView,
			wrapped.getWorld().getRegistryManager().getOrThrow(RegistryKeys.BIOME),
			wrapped.getBlendingData()
		);
		this.wrapped = wrapped;
		this.propagateToWrapped = propagateToWrapped;
	}

	@Nullable
	@Override
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
	public ChunkSection getSection(int yIndex) {
		return this.propagateToWrapped ? this.wrapped.getSection(yIndex) : super.getSection(yIndex);
	}

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
		return this.propagateToWrapped ? this.wrapped.setBlockState(pos, state, moved) : null;
	}

	@Override
	public void setBlockEntity(BlockEntity blockEntity) {
		if (this.propagateToWrapped) {
			this.wrapped.setBlockEntity(blockEntity);
		}
	}

	@Override
	public void addEntity(Entity entity) {
		if (this.propagateToWrapped) {
			this.wrapped.addEntity(entity);
		}
	}

	@Override
	public void setStatus(ChunkStatus status) {
		if (this.propagateToWrapped) {
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
		} else {
			return type == Heightmap.Type.OCEAN_FLOOR_WG ? Heightmap.Type.OCEAN_FLOOR : type;
		}
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
	public RegistryEntry<Biome> getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return this.wrapped.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
	}

	@Override
	public ChunkPos getPos() {
		return this.wrapped.getPos();
	}

	@Nullable
	@Override
	public StructureStart getStructureStart(Structure structure) {
		return this.wrapped.getStructureStart(structure);
	}

	@Override
	public void setStructureStart(Structure structure, StructureStart start) {
	}

	@Override
	public Map<Structure, StructureStart> getStructureStarts() {
		return this.wrapped.getStructureStarts();
	}

	@Override
	public void setStructureStarts(Map<Structure, StructureStart> structureStarts) {
	}

	@Override
	public LongSet getStructureReferences(Structure structure) {
		return this.wrapped.getStructureReferences(structure);
	}

	@Override
	public void addStructureReference(Structure structure, long reference) {
	}

	@Override
	public Map<Structure, LongSet> getStructureReferences() {
		return this.wrapped.getStructureReferences();
	}

	@Override
	public void setStructureReferences(Map<Structure, LongSet> structureReferences) {
	}

	@Override
	public void setNeedsSaving(boolean needsSaving) {
		this.wrapped.setNeedsSaving(needsSaving);
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

	@Nullable
	@Override
	public NbtCompound getBlockEntityNbt(BlockPos pos) {
		return this.wrapped.getBlockEntityNbt(pos);
	}

	@Nullable
	@Override
	public NbtCompound getPackedBlockEntityNbt(BlockPos pos, RegistryWrapper.WrapperLookup registries) {
		return this.wrapped.getPackedBlockEntityNbt(pos, registries);
	}

	@Override
	public void forEachBlockMatchingPredicate(Predicate<BlockState> predicate, BiConsumer<BlockPos, BlockState> consumer) {
		this.wrapped.forEachBlockMatchingPredicate(predicate, consumer);
	}

	@Override
	public BasicTickScheduler<Block> getBlockTickScheduler() {
		return this.propagateToWrapped ? this.wrapped.getBlockTickScheduler() : EmptyTickSchedulers.getReadOnlyTickScheduler();
	}

	@Override
	public BasicTickScheduler<Fluid> getFluidTickScheduler() {
		return this.propagateToWrapped ? this.wrapped.getFluidTickScheduler() : EmptyTickSchedulers.getReadOnlyTickScheduler();
	}

	@Override
	public Chunk.TickSchedulers getTickSchedulers(long time) {
		return this.wrapped.getTickSchedulers(time);
	}

	@Nullable
	@Override
	public BlendingData getBlendingData() {
		return this.wrapped.getBlendingData();
	}

	@Override
	public CarvingMask getCarvingMask() {
		if (this.propagateToWrapped) {
			return super.getCarvingMask();
		} else {
			throw (UnsupportedOperationException)Util.getFatalOrPause(new UnsupportedOperationException("Meaningless in this context"));
		}
	}

	@Override
	public CarvingMask getOrCreateCarvingMask() {
		if (this.propagateToWrapped) {
			return super.getOrCreateCarvingMask();
		} else {
			throw (UnsupportedOperationException)Util.getFatalOrPause(new UnsupportedOperationException("Meaningless in this context"));
		}
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
	public void populateBiomes(BiomeSupplier biomeSupplier, MultiNoiseUtil.MultiNoiseSampler sampler) {
		if (this.propagateToWrapped) {
			this.wrapped.populateBiomes(biomeSupplier, sampler);
		}
	}

	@Override
	public void refreshSurfaceY() {
		this.wrapped.refreshSurfaceY();
	}

	@Override
	public ChunkSkyLight getChunkSkyLight() {
		return this.wrapped.getChunkSkyLight();
	}
}
