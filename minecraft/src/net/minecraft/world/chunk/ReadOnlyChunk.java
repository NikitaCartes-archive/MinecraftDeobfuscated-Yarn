package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.class_2839;
import net.minecraft.class_2843;
import net.minecraft.class_3449;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.Heightmap;

public class ReadOnlyChunk extends class_2839 {
	private final WorldChunk wrapped;

	public ReadOnlyChunk(WorldChunk worldChunk) {
		super(worldChunk.getPos(), class_2843.field_12950);
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
	public void method_12308(ChunkStatus chunkStatus) {
	}

	@Override
	public ChunkSection[] getSectionArray() {
		return this.wrapped.getSectionArray();
	}

	@Nullable
	@Override
	public LightingProvider method_12023() {
		return this.wrapped.method_12023();
	}

	@Override
	public void method_12037(Heightmap.Type type, long[] ls) {
	}

	private Heightmap.Type method_12239(Heightmap.Type type) {
		if (type == Heightmap.Type.WORLD_SURFACE_WG) {
			return Heightmap.Type.WORLD_SURFACE;
		} else {
			return type == Heightmap.Type.OCEAN_FLOOR_WG ? Heightmap.Type.OCEAN_FLOOR : type;
		}
	}

	@Override
	public int sampleHeightmap(Heightmap.Type type, int i, int j) {
		return this.wrapped.sampleHeightmap(this.method_12239(type), i, j);
	}

	@Override
	public ChunkPos getPos() {
		return this.wrapped.getPos();
	}

	@Override
	public void method_12043(long l) {
	}

	@Nullable
	@Override
	public class_3449 method_12181(String string) {
		return this.wrapped.method_12181(string);
	}

	@Override
	public void method_12184(String string, class_3449 arg) {
	}

	@Override
	public Map<String, class_3449> method_12016() {
		return this.wrapped.method_12016();
	}

	@Override
	public void method_12034(Map<String, class_3449> map) {
	}

	@Override
	public LongSet method_12180(String string) {
		return this.wrapped.method_12180(string);
	}

	@Override
	public void method_12182(String string, long l) {
	}

	@Override
	public Map<String, LongSet> method_12179() {
		return this.wrapped.method_12179();
	}

	@Override
	public void method_12183(Map<String, LongSet> map) {
	}

	@Override
	public Biome[] getBiomeArray() {
		return this.wrapped.getBiomeArray();
	}

	@Override
	public void method_12008(boolean bl) {
	}

	@Override
	public boolean method_12044() {
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
	public CompoundTag method_12024(BlockPos blockPos) {
		return this.wrapped.method_12024(blockPos);
	}

	@Override
	public void setBiomes(Biome[] biomes) {
	}

	@Override
	public Stream<BlockPos> method_12018() {
		return this.wrapped.method_12018();
	}

	@Override
	public ChunkTickScheduler<Block> method_12303() {
		return new ChunkTickScheduler<>(block -> block.getDefaultState().isAir(), Registry.BLOCK::getId, Registry.BLOCK::get, this.getPos());
	}

	@Override
	public ChunkTickScheduler<Fluid> method_12313() {
		return new ChunkTickScheduler<>(fluid -> fluid == Fluids.field_15906, Registry.FLUID::getId, Registry.FLUID::get, this.getPos());
	}

	@Override
	public BitSet method_12025(GenerationStep.Carver carver) {
		return this.wrapped.method_12025(carver);
	}

	public WorldChunk method_12240() {
		return this.wrapped;
	}

	@Override
	public boolean method_12038() {
		return this.wrapped.method_12038();
	}

	@Override
	public void method_12020(boolean bl) {
		this.wrapped.method_12020(bl);
	}
}
