package net.minecraft;

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
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.feature.StructureFeature;

public class class_6469 extends ProtoChunk {
	private final WorldChunk field_34234;

	public class_6469(WorldChunk worldChunk) {
		super(worldChunk.getPos(), UpgradeData.NO_UPGRADE_DATA, worldChunk.getWorld());
		this.field_34234 = worldChunk;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return this.field_34234.getBlockEntity(pos);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return this.field_34234.getBlockState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.field_34234.getFluidState(pos);
	}

	@Override
	public int getMaxLightLevel() {
		return this.field_34234.getMaxLightLevel();
	}

	@Override
	public ChunkSection getSection(int yIndex) {
		return this.field_34234.getSection(yIndex);
	}

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
		return this.field_34234.setBlockState(pos, state, moved);
	}

	@Override
	public void setBlockEntity(BlockEntity blockEntity) {
		this.field_34234.setBlockEntity(blockEntity);
	}

	@Override
	public void addEntity(Entity entity) {
		this.field_34234.addEntity(entity);
	}

	@Override
	public ChunkSection[] getSectionArray() {
		return this.field_34234.getSectionArray();
	}

	@Override
	public void setHeightmap(Heightmap.Type type, long[] heightmap) {
		super.setHeightmap(type, heightmap);
	}

	private Heightmap.Type method_37758(Heightmap.Type type) {
		if (type == Heightmap.Type.WORLD_SURFACE_WG) {
			return Heightmap.Type.WORLD_SURFACE;
		} else {
			return type == Heightmap.Type.OCEAN_FLOOR_WG ? Heightmap.Type.OCEAN_FLOOR : type;
		}
	}

	@Override
	public int sampleHeightmap(Heightmap.Type type, int x, int z) {
		return super.sampleHeightmap(type, x, z);
	}

	@Override
	public BlockPos method_35319(Heightmap.Type type) {
		return super.method_35319(type);
	}

	@Override
	public ChunkPos getPos() {
		return this.field_34234.getPos();
	}

	@Nullable
	@Override
	public StructureStart<?> getStructureStart(StructureFeature<?> structure) {
		return this.field_34234.getStructureStart(structure);
	}

	@Override
	public void setStructureStart(StructureFeature<?> structure, StructureStart<?> start) {
	}

	@Override
	public Map<StructureFeature<?>, StructureStart<?>> getStructureStarts() {
		return this.field_34234.getStructureStarts();
	}

	@Override
	public void setStructureStarts(Map<StructureFeature<?>, StructureStart<?>> structureStarts) {
	}

	@Override
	public LongSet getStructureReferences(StructureFeature<?> structure) {
		return this.field_34234.getStructureReferences(structure);
	}

	@Override
	public void addStructureReference(StructureFeature<?> structure, long reference) {
	}

	@Override
	public Map<StructureFeature<?>, LongSet> getStructureReferences() {
		return this.field_34234.getStructureReferences();
	}

	@Override
	public void setStructureReferences(Map<StructureFeature<?>, LongSet> structureReferences) {
	}

	@Override
	public BiomeArray getBiomeArray() {
		return this.field_34234.getBiomeArray();
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
		return this.field_34234.getStatus();
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
		return this.field_34234.getBlockEntityNbt(pos);
	}

	@Nullable
	@Override
	public NbtCompound getPackedBlockEntityNbt(BlockPos pos) {
		return this.field_34234.getPackedBlockEntityNbt(pos);
	}

	@Override
	public Stream<BlockPos> getLightSourcesStream() {
		return this.field_34234.getLightSourcesStream();
	}

	@Override
	public ChunkTickScheduler<Block> getBlockTickScheduler() {
		return new ChunkTickScheduler<>(block -> block.getDefaultState().isAir(), this.getPos(), this.field_34234.getWorld());
	}

	@Override
	public ChunkTickScheduler<Fluid> getFluidTickScheduler() {
		return new ChunkTickScheduler<>(fluid -> fluid == Fluids.EMPTY, this.getPos(), this.field_34234.getWorld());
	}

	@Override
	public boolean isLightOn() {
		return this.field_34234.isLightOn();
	}

	@Override
	public void setLightOn(boolean lightOn) {
		this.field_34234.setLightOn(lightOn);
	}
}
