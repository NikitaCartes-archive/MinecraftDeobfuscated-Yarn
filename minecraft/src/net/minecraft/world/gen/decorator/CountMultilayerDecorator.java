package net.minecraft.world.gen.decorator;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.CountConfig;

public class CountMultilayerDecorator extends Decorator<CountConfig> {
	public CountMultilayerDecorator(Codec<CountConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, CountConfig countConfig, BlockPos blockPos) {
		List<BlockPos> list = Lists.<BlockPos>newArrayList();
		int i = 0;

		boolean bl;
		do {
			bl = false;

			for (int j = 0; j < countConfig.method_30396().getValue(random); j++) {
				int k = random.nextInt(16) + blockPos.getX();
				int l = random.nextInt(16) + blockPos.getZ();
				int m = decoratorContext.getTopY(Heightmap.Type.MOTION_BLOCKING, k, l);
				int n = method_30473(decoratorContext, k, m, l, i);
				if (n != Integer.MAX_VALUE) {
					list.add(new BlockPos(k, n, l));
					bl = true;
				}
			}

			i++;
		} while (bl);

		return list.stream();
	}

	private static int method_30473(DecoratorContext decoratorContext, int i, int j, int k, int l) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(i, j, k);
		int m = 0;
		BlockState blockState = decoratorContext.getBlockState(mutable);

		for (int n = j; n >= 1; n--) {
			mutable.setY(n - 1);
			BlockState blockState2 = decoratorContext.getBlockState(mutable);
			if (!method_30472(blockState2) && method_30472(blockState) && !blockState2.isOf(Blocks.BEDROCK)) {
				if (m == l) {
					return mutable.getY() + 1;
				}

				m++;
			}

			blockState = blockState2;
		}

		return Integer.MAX_VALUE;
	}

	private static boolean method_30472(BlockState blockState) {
		return blockState.isAir() || blockState.isOf(Blocks.WATER) || blockState.isOf(Blocks.LAVA);
	}
}
