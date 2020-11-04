package net.minecraft.item;

import java.util.Random;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireChargeItem extends Item {
	public FireChargeItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		boolean bl = false;
		if (!CampfireBlock.method_30035(blockState) && !CandleBlock.canBeLit(blockState) && !CandleCakeBlock.canBeLit(blockState)) {
			blockPos = blockPos.offset(context.getSide());
			if (AbstractFireBlock.method_30032(world, blockPos, context.getPlayerFacing())) {
				this.playUseSound(world, blockPos);
				world.setBlockState(blockPos, AbstractFireBlock.getState(world, blockPos));
				bl = true;
			}
		} else {
			this.playUseSound(world, blockPos);
			world.setBlockState(blockPos, blockState.with(Properties.LIT, Boolean.valueOf(true)));
			bl = true;
		}

		if (bl) {
			context.getStack().decrement(1);
			return ActionResult.success(world.isClient);
		} else {
			return ActionResult.FAIL;
		}
	}

	private void playUseSound(World world, BlockPos pos) {
		Random random = world.getRandom();
		world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
	}
}
