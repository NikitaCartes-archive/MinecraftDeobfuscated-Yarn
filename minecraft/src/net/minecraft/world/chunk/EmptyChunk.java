package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class EmptyChunk extends WorldChunk {
	private final RegistryEntry<Biome> biomeEntry;

	public EmptyChunk(World world, ChunkPos pos, RegistryEntry<Biome> biomeEntry) {
		super(world, pos);
		this.biomeEntry = biomeEntry;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return Blocks.VOID_AIR.getDefaultState();
	}

	@Nullable
	@Override
	public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
		return null;
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return Fluids.EMPTY.getDefaultState();
	}

	@Override
	public int getLuminance(BlockPos pos) {
		return 0;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos, WorldChunk.CreationType creationType) {
		return null;
	}

	@Override
	public void addBlockEntity(BlockEntity blockEntity) {
	}

	@Override
	public void setBlockEntity(BlockEntity blockEntity) {
	}

	@Override
	public void removeBlockEntity(BlockPos pos) {
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
	public boolean method_60791(int i) {
		return true;
	}

	@Override
	public ChunkLevelType getLevelType() {
		return ChunkLevelType.FULL;
	}

	@Override
	public RegistryEntry<Biome> getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return this.biomeEntry;
	}
}
