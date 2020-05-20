package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;

public class UnderwaterRavineCarver extends RavineCarver {
	public UnderwaterRavineCarver(Codec<ProbabilityConfig> codec) {
		super(codec);
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
			Blocks.CAVE_AIR
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
		return UnderwaterCaveCarver.carveAtPoint(this, chunk, carvingMask, random, mutable, seaLevel, mainChunkX, mainChunkZ, x, z, relativeX, y, relativeZ);
	}
}
