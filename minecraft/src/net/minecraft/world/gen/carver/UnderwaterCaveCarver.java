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
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;

public class UnderwaterCaveCarver extends CaveCarver {
	public UnderwaterCaveCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> function) {
		super(function, 256);
		this.field_13302 = ImmutableSet.of(
			Blocks.field_10340,
			Blocks.field_10474,
			Blocks.field_10508,
			Blocks.field_10115,
			Blocks.field_10566,
			Blocks.field_10253,
			Blocks.field_10520,
			Blocks.field_10219,
			Blocks.field_10415,
			Blocks.field_10611,
			Blocks.field_10184,
			Blocks.field_10015,
			Blocks.field_10325,
			Blocks.field_10143,
			Blocks.field_10014,
			Blocks.field_10444,
			Blocks.field_10349,
			Blocks.field_10590,
			Blocks.field_10235,
			Blocks.field_10570,
			Blocks.field_10409,
			Blocks.field_10123,
			Blocks.field_10526,
			Blocks.field_10328,
			Blocks.field_10626,
			Blocks.field_9979,
			Blocks.field_10344,
			Blocks.field_10402,
			Blocks.field_10477,
			Blocks.field_10102,
			Blocks.field_10255,
			Blocks.field_10382,
			Blocks.field_10164,
			Blocks.field_10540,
			Blocks.field_10124,
			Blocks.field_10543,
			Blocks.field_10225
		);
	}

	@Override
	protected boolean method_12711(Chunk chunk, int i, int j, int k, int l, int m, int n, int o, int p) {
		return false;
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
		return method_16138(this, chunk, bitSet, random, mutable, i, j, k, l, m, n, o, p);
	}

	protected static boolean method_16138(
		Carver<?> carver, Chunk chunk, BitSet bitSet, Random random, BlockPos.Mutable mutable, int i, int j, int k, int l, int m, int n, int o, int p
	) {
		if (o >= i) {
			return false;
		} else {
			int q = n | p << 4 | o << 8;
			if (bitSet.get(q)) {
				return false;
			} else {
				bitSet.set(q);
				mutable.set(l, o, m);
				BlockState blockState = chunk.getBlockState(mutable);
				if (!carver.method_12709(blockState)) {
					return false;
				} else if (o == 10) {
					float f = random.nextFloat();
					if ((double)f < 0.25) {
						chunk.setBlockState(mutable, Blocks.field_10092.getDefaultState(), false);
						chunk.getBlockTickScheduler().schedule(mutable, Blocks.field_10092, 0);
					} else {
						chunk.setBlockState(mutable, Blocks.field_10540.getDefaultState(), false);
					}

					return true;
				} else if (o < 10) {
					chunk.setBlockState(mutable, Blocks.field_10164.getDefaultState(), false);
					return false;
				} else {
					boolean bl = false;

					for (Direction direction : Direction.Type.HORIZONTAL) {
						int r = l + direction.getOffsetX();
						int s = m + direction.getOffsetZ();
						if (r >> 4 != j || s >> 4 != k || chunk.getBlockState(mutable.set(r, o, s)).isAir()) {
							chunk.setBlockState(mutable, WATER.getBlockState(), false);
							chunk.getFluidTickScheduler().schedule(mutable, WATER.getFluid(), 0);
							bl = true;
							break;
						}
					}

					mutable.set(l, o, m);
					if (!bl) {
						chunk.setBlockState(mutable, WATER.getBlockState(), false);
						return true;
					} else {
						return true;
					}
				}
			}
		}
	}
}
