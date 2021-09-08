package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * A regenerating world chunk represented as a proto chunk. Necessary since
 * in {@link ChunkStatus}, the generation tasks assume the provided chunks
 * are proto chunks and perform unchecked casts. Used by {@link
 * net.minecraft.server.command.ResetChunksCommand /resetchunks} command.
 */
public class RegeneratingChunk extends ProtoChunk {
	private final WorldChunk delegate;

	public RegeneratingChunk(WorldChunk delegate) {
		super(delegate.getPos(), UpgradeData.NO_UPGRADE_DATA, delegate.getWorld());
		this.delegate = delegate;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return this.delegate.getBlockEntity(pos);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return this.delegate.getBlockState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.delegate.getFluidState(pos);
	}

	@Override
	public int getMaxLightLevel() {
		return this.delegate.getMaxLightLevel();
	}

	@Override
	public ChunkSection getSection(int yIndex) {
		return this.delegate.getSection(yIndex);
	}

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
		return this.delegate.setBlockState(pos, state, moved);
	}

	@Override
	public void setBlockEntity(BlockEntity blockEntity) {
		this.delegate.setBlockEntity(blockEntity);
	}

	@Override
	public void addEntity(Entity entity) {
		this.delegate.addEntity(entity);
	}

	@Override
	public ChunkSection[] getSectionArray() {
		return this.delegate.getSectionArray();
	}

	@Override
	public void setHeightmap(Heightmap.Type type, long[] heightmap) {
		super.setHeightmap(type, heightmap);
	}

	private Heightmap.Type convertHeightmapType(Heightmap.Type desiredType) {
		if (desiredType == Heightmap.Type.WORLD_SURFACE_WG) {
			return Heightmap.Type.WORLD_SURFACE;
		} else {
			return desiredType == Heightmap.Type.OCEAN_FLOOR_WG ? Heightmap.Type.OCEAN_FLOOR : desiredType;
		}
	}

	@Override
	public int sampleHeightmap(Heightmap.Type type, int x, int z) {
		return super.sampleHeightmap(type, x, z);
	}

	@Override
	public BlockPos sampleMaxHeightMap(Heightmap.Type type) {
		return super.sampleMaxHeightMap(type);
	}

	@Override
	public ChunkPos getPos() {
		return this.delegate.getPos();
	}

	@Nullable
	@Override
	public StructureStart<?> getStructureStart(StructureFeature<?> structure) {
		return this.delegate.getStructureStart(structure);
	}

	@Override
	public void setStructureStart(StructureFeature<?> structure, StructureStart<?> start) {
	}

	@Override
	public Map<StructureFeature<?>, StructureStart<?>> getStructureStarts() {
		return this.delegate.getStructureStarts();
	}

	@Override
	public void setStructureStarts(Map<StructureFeature<?>, StructureStart<?>> structureStarts) {
	}

	@Override
	public LongSet getStructureReferences(StructureFeature<?> structure) {
		return this.delegate.getStructureReferences(structure);
	}

	@Override
	public void addStructureReference(StructureFeature<?> structure, long reference) {
	}

	@Override
	public Map<StructureFeature<?>, LongSet> getStructureReferences() {
		return this.delegate.getStructureReferences();
	}

	@Override
	public void setStructureReferences(Map<StructureFeature<?>, LongSet> structureReferences) {
	}

	@Override
	public BiomeArray getBiomeArray() {
		return this.delegate.getBiomeArray();
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
		return this.delegate.getStatus();
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
		return this.delegate.getBlockEntityNbt(pos);
	}

	@Nullable
	@Override
	public NbtCompound getPackedBlockEntityNbt(BlockPos pos) {
		return this.delegate.getPackedBlockEntityNbt(pos);
	}

	@Override
	public Stream<BlockPos> getLightSourcesStream() {
		return this.delegate.getLightSourcesStream();
	}

	@Override
	public ChunkTickScheduler<Block> getBlockTickScheduler() {
		return new ChunkTickScheduler<>(block -> block.getDefaultState().isAir(), this.getPos(), this.delegate.getWorld());
	}

	@Override
	public ChunkTickScheduler<Fluid> getFluidTickScheduler() {
		return new ChunkTickScheduler<>(fluid -> fluid == Fluids.EMPTY, this.getPos(), this.delegate.getWorld());
	}

	@Override
	public boolean isLightOn() {
		return this.delegate.isLightOn();
	}

	@Override
	public void setLightOn(boolean lightOn) {
		this.delegate.setLightOn(lightOn);
	}
}
