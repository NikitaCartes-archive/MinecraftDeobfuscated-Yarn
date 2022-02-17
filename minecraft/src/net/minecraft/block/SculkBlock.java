package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_6989;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.World;

public class SculkBlock extends ExperienceDroppingBlock implements class_6989 {
	private static final int field_36846 = 400;

	public SculkBlock(AbstractBlock.Settings settings) {
		super(settings, ConstantIntProvider.create(1));
	}

	@Override
	public short method_40786(SculkSpreadManager.Cursor cursor, World world, BlockPos blockPos, Random random) {
		short s = cursor.getCharge();
		if (s != 0 && random.nextInt(10) == 0) {
			BlockPos blockPos2 = cursor.getPos();
			boolean bl = this.method_40789(blockPos2, blockPos);
			boolean bl2 = random.nextInt(10) == 0;
			if (!bl && this.method_40788(world, blockPos2)) {
				if (random.nextInt(10) < s) {
					world.setBlockState(blockPos2.up(), (bl2 ? Blocks.SCULK_SHRIEKER : Blocks.SCULK_SENSOR).getDefaultState(), Block.NOTIFY_ALL);
					world.playSound(null, blockPos2, bl2 ? SoundEvents.BLOCK_SCULK_SHRIEKER_PLACE : SoundEvents.BLOCK_SCULK_SENSOR_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
				}

				return (short)Math.max(0, s - 10);
			} else {
				return random.nextInt(5) != 0 ? s : (short)(s - (bl ? 1 : this.method_40790(blockPos2, blockPos, s)));
			}
		} else {
			return s;
		}
	}

	private boolean method_40789(BlockPos blockPos, BlockPos blockPos2) {
		return blockPos.getSquaredDistance(blockPos2) <= 16.0;
	}

	private int method_40790(BlockPos blockPos, BlockPos blockPos2, int i) {
		float f = (float)Math.sqrt(blockPos.getSquaredDistance(blockPos2)) - 4.0F;
		float g = Math.min(1.0F, f * f / 400.0F);
		return Math.max(1, (int)(g * (float)i * 0.5F));
	}

	private boolean method_40788(World world, BlockPos blockPos) {
		BlockState blockState = world.getBlockState(blockPos.up());
		if (blockState.isAir() || blockState.isOf(Blocks.WATER) && blockState.getFluidState().isStill()) {
			int i = 0;

			for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-4, 0, -4), blockPos.add(4, 2, 4))) {
				BlockState blockState2 = world.getBlockState(blockPos2);
				if (blockState2.isOf(Blocks.SCULK_SENSOR) || blockState2.isOf(Blocks.SCULK_SHRIEKER)) {
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
	public boolean method_40787() {
		return false;
	}
}
