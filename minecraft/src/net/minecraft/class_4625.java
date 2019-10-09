package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;

public abstract class class_4625 extends Feature<class_4635> {
	public class_4625(Function<Dynamic<?>, ? extends class_4635> function) {
		super(function);
	}

	protected void method_23376(IWorld iWorld, Random random, BlockPos blockPos, class_4635 arg, int i, BlockPos.Mutable mutable) {
		for (int j = 0; j < i; j++) {
			mutable.set(blockPos).setOffset(Direction.UP, j);
			if (!iWorld.getBlockState(mutable).isFullOpaque(iWorld, mutable)) {
				this.setBlockState(iWorld, mutable, arg.field_21231.method_23455(random, blockPos));
			}
		}
	}

	protected int method_23377(Random random) {
		int i = random.nextInt(3) + 4;
		if (random.nextInt(12) == 0) {
			i *= 2;
		}

		return i;
	}

	protected boolean method_23374(IWorld iWorld, BlockPos blockPos, int i, BlockPos.Mutable mutable, class_4635 arg) {
		int j = blockPos.getY();
		if (j >= 1 && j + i + 1 < 256) {
			Block block = iWorld.getBlockState(blockPos.method_10074()).getBlock();
			if (!method_23396(block)) {
				return false;
			} else {
				for (int k = 0; k <= i; k++) {
					int l = this.method_23372(-1, -1, arg.field_21232, k);

					for (int m = -l; m <= l; m++) {
						for (int n = -l; n <= l; n++) {
							BlockState blockState = iWorld.getBlockState(mutable.set(blockPos).setOffset(m, k, n));
							if (!blockState.isAir() && !blockState.matches(BlockTags.LEAVES)) {
								return false;
							}
						}
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	public boolean method_23373(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, class_4635 arg) {
		int i = this.method_23377(random);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		if (!this.method_23374(iWorld, blockPos, i, mutable, arg)) {
			return false;
		} else {
			this.method_23375(iWorld, random, blockPos, i, mutable, arg);
			this.method_23376(iWorld, random, blockPos, arg, i, mutable);
			return true;
		}
	}

	protected abstract int method_23372(int i, int j, int k, int l);

	protected abstract void method_23375(IWorld iWorld, Random random, BlockPos blockPos, int i, BlockPos.Mutable mutable, class_4635 arg);
}
