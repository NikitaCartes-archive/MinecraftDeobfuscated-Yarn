package net.minecraft.item;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
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
		if (blockState.method_27851(
			BlockTags.CAMPFIRES, abstractBlockState -> abstractBlockState.contains(CampfireBlock.LIT) && abstractBlockState.contains(CampfireBlock.WATERLOGGED)
		)) {
			if (!(Boolean)blockState.get(CampfireBlock.LIT) && !(Boolean)blockState.get(CampfireBlock.WATERLOGGED)) {
				this.playUseSound(world, blockPos);
				world.setBlockState(blockPos, blockState.with(CampfireBlock.LIT, Boolean.valueOf(true)));
				bl = true;
			}
		} else {
			blockPos = blockPos.offset(context.getSide());
			BlockState blockState2 = AbstractFireBlock.getState(world, blockPos);
			if (world.getBlockState(blockPos).isAir() && blockState2.canPlaceAt(world, blockPos)) {
				this.playUseSound(world, blockPos);
				world.setBlockState(blockPos, blockState2);
				bl = true;
			}
		}

		if (bl) {
			context.getStack().decrement(1);
			return ActionResult.success(world.isClient);
		} else {
			return ActionResult.FAIL;
		}
	}

	private void playUseSound(World world, BlockPos pos) {
		world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.0F);
	}
}
