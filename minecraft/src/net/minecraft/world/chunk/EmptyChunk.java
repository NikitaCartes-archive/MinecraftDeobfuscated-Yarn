package net.minecraft.world.chunk;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.light.LightingProvider;

public class EmptyChunk extends WorldChunk {
	private static final Biome[] BIOMES = Util.make(new Biome[BiomeArray.DEFAULT_LENGTH], biomes -> Arrays.fill(biomes, BuiltinBiomes.PLAINS));

	public EmptyChunk(World world, ChunkPos pos) {
		super(world, pos, new BiomeArray(world.getRegistryManager().get(Registry.BIOME_KEY), BIOMES));
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

	@Nullable
	@Override
	public LightingProvider getLightingProvider() {
		return null;
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
	public void markDirty() {
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
	public ChunkHolder.LevelType getLevelType() {
		return ChunkHolder.LevelType.BORDER;
	}
}
