package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_6108;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NetherCaveCarver extends CaveCarver {
	public NetherCaveCarver(Codec<class_6108> codec) {
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

	protected boolean carveAtPoint(
		CarverContext carverContext,
		class_6108 arg,
		Chunk chunk,
		Function<BlockPos, Biome> function,
		BitSet bitSet,
		Random random,
		BlockPos.Mutable mutable,
		BlockPos.Mutable mutable2,
		int i,
		MutableBoolean mutableBoolean
	) {
		if (this.canAlwaysCarveBlock(chunk.getBlockState(mutable))) {
			BlockState blockState;
			if (mutable.getY() <= carverContext.getMinY() + 31) {
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
