package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;

public class UnderwaterCaveCarver extends CaveCarver {
	public UnderwaterCaveCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> configDeserializer) {
		super(configDeserializer, 256);
		this.alwaysCarvableBlocks = ImmutableSet.of(
			Blocks.STONE,
			Blocks.GRANITE,
			Blocks.DIORITE,
			Blocks.ANDESITE,
			Blocks.DIRT,
			Blocks.COARSE_DIRT,
			Blocks.PODZOL,
			Blocks.GRASS_BLOCK,
			Blocks.TERRACOTTA,
			Blocks.WHITE_TERRACOTTA,
			Blocks.ORANGE_TERRACOTTA,
			Blocks.MAGENTA_TERRACOTTA,
			Blocks.LIGHT_BLUE_TERRACOTTA,
			Blocks.YELLOW_TERRACOTTA,
			Blocks.LIME_TERRACOTTA,
			Blocks.PINK_TERRACOTTA,
			Blocks.GRAY_TERRACOTTA,
			Blocks.LIGHT_GRAY_TERRACOTTA,
			Blocks.CYAN_TERRACOTTA,
			Blocks.PURPLE_TERRACOTTA,
			Blocks.BLUE_TERRACOTTA,
			Blocks.BROWN_TERRACOTTA,
			Blocks.GREEN_TERRACOTTA,
			Blocks.RED_TERRACOTTA,
			Blocks.BLACK_TERRACOTTA,
			Blocks.SANDSTONE,
			Blocks.RED_SANDSTONE,
			Blocks.MYCELIUM,
			Blocks.SNOW,
			Blocks.SAND,
			Blocks.GRAVEL,
			Blocks.WATER,
			Blocks.LAVA,
			Blocks.OBSIDIAN,
			Blocks.AIR,
			Blocks.CAVE_AIR,
			Blocks.PACKED_ICE
		);
	}

	@Override
	protected boolean isRegionUncarvable(Chunk chunk, int mainChunkX, int mainChunkZ, int relMinX, int relMaxX, int minY, int maxY, int relMinZ, int relMaxZ) {
		return false;
	}

	@Override
	protected boolean carveAtPoint(
		Chunk chunk,
		Function<BlockPos, Biome> posToBiome,
		BitSet carvingMask,
		Random random,
		BlockPos.Mutable mutable,
		BlockPos.Mutable mutable2,
		BlockPos.Mutable mutable3,
		int seaLevel,
		int mainChunkX,
		int mainChunkZ,
		int x,
		int z,
		int relativeX,
		int y,
		int relativeZ,
		AtomicBoolean foundSurface
	) {
		return carveAtPoint(this, chunk, carvingMask, random, mutable, seaLevel, mainChunkX, mainChunkZ, x, z, relativeX, y, relativeZ);
	}

	protected static boolean carveAtPoint(
		Carver<?> carver,
		Chunk chunk,
		BitSet mask,
		Random random,
		BlockPos.Mutable pos,
		int seaLevel,
		int mainChunkX,
		int mainChunkZ,
		int x,
		int z,
		int relativeX,
		int y,
		int relativeZ
	) {
		if (y >= seaLevel) {
			return false;
		} else {
			int i = relativeX | relativeZ << 4 | y << 8;
			if (mask.get(i)) {
				return false;
			} else {
				mask.set(i);
				pos.set(x, y, z);
				BlockState blockState = chunk.getBlockState(pos);
				if (!carver.canAlwaysCarveBlock(blockState)) {
					return false;
				} else if (y == 10) {
					float f = random.nextFloat();
					if ((double)f < 0.25) {
						chunk.setBlockState(pos, Blocks.MAGMA_BLOCK.getDefaultState(), false);
						chunk.getBlockTickScheduler().schedule(pos, Blocks.MAGMA_BLOCK, 0);
					} else {
						chunk.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState(), false);
					}

					return true;
				} else if (y < 10) {
					chunk.setBlockState(pos, Blocks.LAVA.getDefaultState(), false);
					return false;
				} else {
					boolean bl = false;

					for (Direction direction : Direction.Type.HORIZONTAL) {
						int j = x + direction.getOffsetX();
						int k = z + direction.getOffsetZ();
						if (j >> 4 != mainChunkX || k >> 4 != mainChunkZ || chunk.getBlockState(pos.set(j, y, k)).isAir()) {
							chunk.setBlockState(pos, WATER.getBlockState(), false);
							chunk.getFluidTickScheduler().schedule(pos, WATER.getFluid(), 0);
							bl = true;
							break;
						}
					}

					pos.set(x, y, z);
					if (!bl) {
						chunk.setBlockState(pos, WATER.getBlockState(), false);
						return true;
					} else {
						return true;
					}
				}
			}
		}
	}
}
