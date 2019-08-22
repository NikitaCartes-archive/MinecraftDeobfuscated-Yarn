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
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;

public class NetherCaveCarver extends CaveCarver {
	public NetherCaveCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> function) {
		super(function, 128);
		this.alwaysCarvableBlocks = ImmutableSet.of(
			Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK
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
		BitSet bitSet,
		Random random,
		BlockPos.Mutable mutable,
		BlockPos.Mutable mutable2,
		BlockPos.Mutable mutable3,
		int i,
		int j,
		int k,
		int l,
		int m,
		int n,
		int o,
		int p,
		AtomicBoolean atomicBoolean
	) {
		int q = n | p << 4 | o << 8;
		if (bitSet.get(q)) {
			return false;
		} else {
			bitSet.set(q);
			mutable.set(l, o, m);
			if (this.canAlwaysCarveBlock(chunk.getBlockState(mutable))) {
				BlockState blockState;
				if (o <= 31) {
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
