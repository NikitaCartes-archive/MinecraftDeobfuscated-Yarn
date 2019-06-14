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
	private final WorldChunk field_12866;

	public ReadOnlyChunk(WorldChunk worldChunk) {
		super(worldChunk.getPos(), UpgradeData.NO_UPGRADE_DATA);
		this.field_12866 = worldChunk;
	}

	@Nullable
	@Override
	public BlockEntity method_8321(BlockPos blockPos) {
		return this.field_12866.method_8321(blockPos);
	}

	@Nullable
	@Override
	public BlockState method_8320(BlockPos blockPos) {
		return this.field_12866.method_8320(blockPos);
	}

	@Override
	public FluidState method_8316(BlockPos blockPos) {
		return this.field_12866.method_8316(blockPos);
	}

	@Override
	public int getMaxLightLevel() {
		return this.field_12866.getMaxLightLevel();
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
	public ChunkSection[] method_12006() {
		return this.field_12866.method_12006();
	}

	@Nullable
	@Override
	public LightingProvider method_12023() {
		return this.field_12866.method_12023();
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
		return this.field_12866.sampleHeightmap(this.transformHeightmapType(type), i, j);
	}

	@Override
	public ChunkPos getPos() {
		return this.field_12866.getPos();
	}

	@Override
	public void setLastSaveTime(long l) {
	}

	@Nullable
	@Override
	public StructureStart method_12181(String string) {
		return this.field_12866.method_12181(string);
	}

	@Override
	public void method_12184(String string, StructureStart structureStart) {
	}

	@Override
	public Map<String, StructureStart> getStructureStarts() {
		return this.field_12866.getStructureStarts();
	}

	@Override
	public void setStructureStarts(Map<String, StructureStart> map) {
	}

	@Override
	public LongSet getStructureReferences(String string) {
		return this.field_12866.getStructureReferences(string);
	}

	@Override
	public void addStructureReference(String string, long l) {
	}

	@Override
	public Map<String, LongSet> getStructureReferences() {
		return this.field_12866.getStructureReferences();
	}

	@Override
	public void setStructureReferences(Map<String, LongSet> map) {
	}

	@Override
	public Biome[] getBiomeArray() {
		return this.field_12866.getBiomeArray();
	}

	@Override
	public void setShouldSave(boolean bl) {
	}

	@Override
	public boolean needsSaving() {
		return false;
	}

	@Override
	public ChunkStatus method_12009() {
		return this.field_12866.method_12009();
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
		return this.field_12866.getBlockEntityTagAt(blockPos);
	}

	@Nullable
	@Override
	public CompoundTag method_20598(BlockPos blockPos) {
		return this.field_12866.method_20598(blockPos);
	}

	@Override
	public void setBiomeArray(Biome[] biomes) {
	}

	@Override
	public Stream<BlockPos> getLightSourcesStream() {
		return this.field_12866.getLightSourcesStream();
	}

	@Override
	public ChunkTickScheduler<Block> method_12303() {
		return new ChunkTickScheduler<>(block -> block.method_9564().isAir(), this.getPos());
	}

	@Override
	public ChunkTickScheduler<Fluid> method_12313() {
		return new ChunkTickScheduler<>(fluid -> fluid == Fluids.field_15906, this.getPos());
	}

	@Override
	public BitSet method_12025(GenerationStep.Carver carver) {
		return this.field_12866.method_12025(carver);
	}

	public WorldChunk method_12240() {
		return this.field_12866;
	}

	@Override
	public boolean isLightOn() {
		return this.field_12866.isLightOn();
	}

	@Override
	public void setLightOn(boolean bl) {
		this.field_12866.setLightOn(bl);
	}
}
