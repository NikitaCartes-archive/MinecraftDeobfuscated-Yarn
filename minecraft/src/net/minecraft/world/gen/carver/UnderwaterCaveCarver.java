package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.AquiferSampler;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class UnderwaterCaveCarver extends CaveCarver {
	public UnderwaterCaveCarver(Codec<CaveCarverConfig> codec) {
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
			Blocks.PACKED_ICE
		);
	}

	@Override
	protected boolean isRegionUncarvable(Chunk chunk, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return false;
	}

	protected boolean carveAtPoint(
		CarverContext carverContext,
		CaveCarverConfig caveCarverConfig,
		Chunk chunk,
		Function<BlockPos, Biome> function,
		BitSet bitSet,
		Random random,
		BlockPos.Mutable mutable,
		BlockPos.Mutable mutable2,
		AquiferSampler aquiferSampler,
		MutableBoolean mutableBoolean
	) {
		return carve(this, chunk, random, mutable, mutable2, aquiferSampler);
	}

	protected static boolean carve(Carver<?> carver, Chunk chunk, Random random, BlockPos.Mutable pos, BlockPos.Mutable downPos, AquiferSampler sampler) {
		BlockState blockState = sampler.apply(pos.getX(), pos.getY(), pos.getZ(), 0.0, Double.NEGATIVE_INFINITY);
		if (blockState != null && blockState.isAir()) {
			return false;
		} else {
			BlockState blockState2 = chunk.getBlockState(pos);
			if (!carver.canAlwaysCarveBlock(blockState2)) {
				return false;
			} else if (pos.getY() == 10) {
				float f = random.nextFloat();
				if ((double)f < 0.25) {
					chunk.setBlockState(pos, Blocks.MAGMA_BLOCK.getDefaultState(), false);
					chunk.getBlockTickScheduler().schedule(pos, Blocks.MAGMA_BLOCK, 0);
				} else {
					chunk.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState(), false);
				}

				return true;
			} else if (pos.getY() < 10) {
				chunk.setBlockState(pos, Blocks.LAVA.getDefaultState(), false);
				return false;
			} else {
				chunk.setBlockState(pos, WATER.getBlockState(), false);
				int i = chunk.getPos().x;
				int j = chunk.getPos().z;

				for (Direction direction : FluidBlock.field_34006) {
					downPos.set(pos, direction);
					if (ChunkSectionPos.getSectionCoord(downPos.getX()) != i || ChunkSectionPos.getSectionCoord(downPos.getZ()) != j || chunk.getBlockState(downPos).isAir()) {
						chunk.getFluidTickScheduler().schedule(pos, WATER.getFluid(), 0);
						break;
					}
				}

				return true;
			}
		}
	}
}
