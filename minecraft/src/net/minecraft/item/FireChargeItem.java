package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireChargeItem extends Item {
	public FireChargeItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			BlockPos blockPos = itemUsageContext.getBlockPos();
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.getBlock() == Blocks.CAMPFIRE) {
				if (!(Boolean)blockState.get(CampfireBlock.LIT) && !(Boolean)blockState.get(CampfireBlock.WATERLOGGED)) {
					this.playUseSound(world, blockPos);
					world.setBlockState(blockPos, blockState.with(CampfireBlock.LIT, Boolean.valueOf(true)));
				}
			} else {
				blockPos = blockPos.offset(itemUsageContext.getSide());
				if (world.getBlockState(blockPos).isAir()) {
					this.playUseSound(world, blockPos);
					world.setBlockState(blockPos, ((FireBlock)Blocks.FIRE).getStateForPosition(world, blockPos));
				}
			}

			itemUsageContext.getStack().decrement(1);
			return ActionResult.SUCCESS;
		}
	}

	private void playUseSound(World world, BlockPos blockPos) {
		world.playSound(null, blockPos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.0F);
	}
}
