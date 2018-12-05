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
import net.minecraft.world.gen.config.ProbabilityConfig;

public class NetherCaveCarver extends CaveCarver {
	public NetherCaveCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> function) {
		super(function, 128);
		this.field_13302 = ImmutableSet.of(
			Blocks.field_10340,
			Blocks.field_10474,
			Blocks.field_10508,
			Blocks.field_10115,
			Blocks.field_10566,
			Blocks.field_10253,
			Blocks.field_10520,
			Blocks.field_10219,
			Blocks.field_10515
		);
		this.field_13298 = ImmutableSet.of(Fluids.LAVA, Fluids.WATER);
	}

	@Override
	protected int method_16577() {
		return 10;
	}

	@Override
	protected float method_16576(Random random) {
		return (random.nextFloat() * 2.0F + random.nextFloat()) * 2.0F;
	}

	@Override
	protected double method_16578() {
		return 5.0;
	}

	@Override
	protected int method_16579(Random random) {
		return random.nextInt(this.field_16653);
	}

	@Override
	protected boolean method_16581(
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
			if (this.method_12709(chunk.getBlockState(mutable))) {
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
