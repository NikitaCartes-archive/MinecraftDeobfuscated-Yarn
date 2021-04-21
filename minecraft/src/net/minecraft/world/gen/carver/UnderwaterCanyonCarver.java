package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_6350;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class UnderwaterCanyonCarver extends RavineCarver {
	public UnderwaterCanyonCarver(Codec<RavineCarverConfig> codec) {
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
	protected boolean isRegionUncarvable(Chunk chunk, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return false;
	}

	protected boolean carveAtPoint(
		CarverContext carverContext,
		RavineCarverConfig ravineCarverConfig,
		Chunk chunk,
		Function<BlockPos, Biome> function,
		BitSet bitSet,
		Random random,
		BlockPos.Mutable mutable,
		BlockPos.Mutable mutable2,
		class_6350 arg,
		MutableBoolean mutableBoolean
	) {
		return UnderwaterCaveCarver.method_36215(this, chunk, random, mutable, mutable2, arg);
	}
}
