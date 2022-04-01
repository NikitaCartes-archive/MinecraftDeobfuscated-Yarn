package net.minecraft;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.World;

public class class_7332 extends class_7321 implements class_7331 {
	private static final int field_38572 = 400;

	public class_7332(AbstractBlock.Settings settings) {
		super(settings, ConstantIntProvider.create(1));
	}

	@Override
	public int method_42920(class_7334.class_7335 arg, World world, BlockPos blockPos, Random random) {
		int i = arg.method_42945();
		if (i != 0 && random.nextInt(10) == 0) {
			BlockPos blockPos2 = arg.method_42934();
			boolean bl = blockPos2.isWithinDistance(blockPos, 4.0);
			if (!bl && method_42923(world, blockPos2)) {
				if (random.nextInt(10) < i) {
					world.setBlockState(blockPos2.up(), Blocks.SCULK_SENSOR.getDefaultState(), Block.NOTIFY_ALL);
					world.playSound(null, blockPos2, SoundEvents.BLOCK_SCULK_SENSOR_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
				}

				return Math.max(0, i - 10);
			} else {
				return random.nextInt(5) != 0 ? i : i - (bl ? 1 : method_42924(blockPos2, blockPos, i));
			}
		} else {
			return i;
		}
	}

	private static int method_42924(BlockPos blockPos, BlockPos blockPos2, int i) {
		float f = (float)Math.sqrt(blockPos.getSquaredDistance(blockPos2)) - 4.0F;
		float g = Math.min(1.0F, f * f / 400.0F);
		return Math.max(1, (int)((float)i * g * 0.5F));
	}

	private static boolean method_42923(World world, BlockPos blockPos) {
		BlockState blockState = world.getBlockState(blockPos.up());
		if (blockState.isAir() || blockState.isOf(Blocks.WATER) && blockState.getFluidState().isOf(Fluids.WATER)) {
			int i = 0;

			for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-4, 0, -4), blockPos.add(4, 2, 4))) {
				BlockState blockState2 = world.getBlockState(blockPos2);
				if (blockState2.isOf(Blocks.SCULK_SENSOR)) {
					i++;
				}

				if (i > 2) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_42921() {
		return false;
	}
}
