package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;

public class NetherCaveCarver extends CaveCarver {
	public NetherCaveCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> configDeserializer) {
		super(configDeserializer, 128);
		this.alwaysCarvableBlocks = ImmutableSet.of(
			Blocks.STONE,
			Blocks.GRANITE,
			Blocks.DIORITE,
			Blocks.ANDESITE,
			Blocks.DIRT,
			Blocks.COARSE_DIRT,
			Blocks.PODZOL,
			Blocks.GRASS_BLOCK,
			Blocks.NETHERRACK,
			Blocks.SOUL_SAND,
			Blocks.SOUL_SOIL,
			Blocks.CRIMSON_NYLIUM,
			Blocks.WARPED_NYLIUM,
			Blocks.NETHER_WART_BLOCK,
			Blocks.WARPED_WART_BLOCK,
			Blocks.BASALT,
			Blocks.BLACKSTONE
		);
		this.carvableFluids = ImmutableSet.of(Fluids.LAVA, Fluids.WATER);
	}

	@Override
	protected int getMaxCaveCount() {
		return 10;
	}

	@Override
	protected float getTunnelSystemWidth(Random random) {
		return (random.nextFloat() * 2.0F + random.nextFloat()) * 2.0F;
	}

	@Override
	protected double getTunnelSystemHeightWidthRatio() {
		return 5.0;
	}

	@Override
	protected int getCaveY(Random random) {
		return random.nextInt(this.heightLimit);
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
		int i = relativeX | relativeZ << 4 | y << 8;
		if (carvingMask.get(i)) {
			return false;
		} else {
			carvingMask.set(i);
			mutable.set(x, y, z);
			if (this.canAlwaysCarveBlock(chunk.getBlockState(mutable))) {
				BlockState blockState;
				if (y <= 31) {
					blockState = LAVA.getBlockState();
				} else {
					blockState = CAVE_AIR;
				}

				chunk.setBlockState(mutable, blockState, false);
				return true;
			} else {
				return false;
			}
		}
	}
}
