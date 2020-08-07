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

	public Stream<BlockPos> method_30474(DecoratorContext decoratorContext, Random random, CountConfig countConfig, BlockPos blockPos) {
		List<BlockPos> list = Lists.<BlockPos>newArrayList();
		int i = 0;

		boolean bl;
		do {
			bl = false;

			for (int j = 0; j < countConfig.getCount().getValue(random); j++) {
				int k = random.nextInt(16) + blockPos.getX();
				int l = random.nextInt(16) + blockPos.getZ();
				int m = decoratorContext.getTopY(Heightmap.Type.field_13197, k, l);
				int n = findPos(decoratorContext, k, m, l, i);
				if (n != Integer.MAX_VALUE) {
					list.add(new BlockPos(k, n, l));
					bl = true;
				}
			}

			i++;
		} while (bl);

		return list.stream();
	}

	private static int findPos(DecoratorContext context, int x, int y, int z, int targetY) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);
		int i = 0;
		BlockState blockState = context.getBlockState(mutable);

		for (int j = y; j >= 1; j--) {
			mutable.setY(j - 1);
			BlockState blockState2 = context.getBlockState(mutable);
			if (!blocksSpawn(blockState2) && blocksSpawn(blockState) && !blockState2.isOf(Blocks.field_9987)) {
				if (i == targetY) {
					return mutable.getY() + 1;
				}

				i++;
			}

			blockState = blockState2;
		}

		return Integer.MAX_VALUE;
	}

	private static boolean blocksSpawn(BlockState state) {
		return state.isAir() || state.isOf(Blocks.field_10382) || state.isOf(Blocks.field_10164);
	}
}
