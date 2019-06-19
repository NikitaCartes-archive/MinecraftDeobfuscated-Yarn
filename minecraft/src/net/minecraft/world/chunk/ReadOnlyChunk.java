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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;

public class ReadOnlyChunk extends ProtoChunk {
	private final WorldChunk wrapped;

	public ReadOnlyChunk(WorldChunk worldChunk) {
		super(worldChunk.getPos(), UpgradeData.NO_UPGRADE_DATA);
		this.wrapped = worldChunk;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos blockPos) {
		return this.wrapped.getBlockEntity(blockPos);
	}

	@Nullable
	@Override
	public BlockState getBlockState(BlockPos blockPos) {
		return this.wrapped.getBlockState(blockPos);
	}

	@Override
	public FluidState getFluidState(BlockPos blockPos) {
		return this.wrapped.getFluidState(blockPos);
	}

	@Override
	public int getMaxLightLevel() {
		return this.wrapped.getMaxLightLevel();
	}

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos blockPos, BlockState blockState, boolean bl) {
		return null;
	}

	@Override
	public void setBlockEntity(BlockPos blockPos, BlockEntity blockEntity) {
	}

	@Override
	public void addEntity(Entity entity) {
	}

	@Override
	public void setStatus(ChunkStatus chunkStatus) {
	}

	@Override
	public ChunkSection[] getSectionArray() {
		return this.wrapped.getSectionArray();
	}

	@Nullable
	@Override
	public LightingProvider getLightingProvider() {
		return this.wrapped.getLightingProvider();
	}

	@Override
	public void setHeightmap(Heightmap.Type type, long[] ls) {
	}

	private Heightmap.Type transformHeightmapType(Heightmap.Type type) {
		if (type == Heightmap.Type.field_13194) {
			return Heightmap.Type.field_13202;
		} else {
			return type == Heightmap.Type.field_13195 ? Heightmap.Type.field_13200 : type;
		}
	}

	@Override
	public int sampleHeightmap(Heightmap.Type type, int i, int j) {
		return this.wrapped.sampleHeightmap(this.transformHeightmapType(type), i, j);
	}

	@Override
	public ChunkPos getPos() {
		return this.wrapped.getPos();
	}

	@Override
	public void setLastSaveTime(long l) {
	}

	@Nullable
	@Override
	public StructureStart getStructureStart(String string) {
		return this.wrapped.getStructureStart(string);
	}

	@Override
	public void setStructureStart(String string, StructureStart structureStart) {
	}

	@Override
	public Map<String, StructureStart> getStructureStarts() {
		return this.wrapped.getStructureStarts();
	}

	@Override
	public void setStructureStarts(Map<String, StructureStart> map) {
	}

	@Override
	public LongSet getStructureReferences(String string) {
		return this.wrapped.getStructureReferences(string);
	}

	@Override
	public void addStructureReference(String string, long l) {
	}

	@Override
	public Map<String, LongSet> getStructureReferences() {
		return this.wrapped.getStructureReferences();
	}

	@Override
	public void setStructureReferences(Map<String, LongSet> map) {
	}

	@Override
	public Biome[] getBiomeArray() {
		return this.wrapped.getBiomeArray();
	}

	@Override
	public void setShouldSave(boolean bl) {
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
	public void removeBlockEntity(BlockPos blockPos) {
	}

	@Override
	public void markBlockForPostProcessing(BlockPos blockPos) {
	}

	@Override
	public void addPendingBlockEntityTag(CompoundTag compoundTag) {
	}

	@Nullable
	@Override
	public CompoundTag getBlockEntityTagAt(BlockPos blockPos) {
		return this.wrapped.getBlockEntityTagAt(blockPos);
	}

	@Nullable
	@Override
	public CompoundTag method_20598(BlockPos blockPos) {
		return this.wrapped.method_20598(blockPos);
	}

	@Override
	public void setBiomeArray(Biome[] biomes) {
	}

	@Override
	public Stream<BlockPos> getLightSourcesStream() {
		return this.wrapped.getLightSourcesStream();
	}

	@Override
	public ChunkTickScheduler<Block> method_12303() {
		return new ChunkTickScheduler<>(block -> block.getDefaultState().isAir(), this.getPos());
	}

	@Override
	public ChunkTickScheduler<Fluid> method_12313() {
		return new ChunkTickScheduler<>(fluid -> fluid == Fluids.field_15906, this.getPos());
	}

	@Override
	public BitSet getCarvingMask(GenerationStep.Carver carver) {
		return this.wrapped.getCarvingMask(carver);
	}

	public WorldChunk getWrappedChunk() {
		return this.wrapped;
	}

	@Override
	public boolean isLightOn() {
		return this.wrapped.isLightOn();
	}

	@Override
	public void setLightOn(boolean bl) {
		this.wrapped.setLightOn(bl);
	}
}
