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
		World world = itemUsageContext.method_8045();
		if (world.isClient) {
			return ActionResult.field_5812;
		} else {
			BlockPos blockPos = itemUsageContext.getBlockPos();
			BlockState blockState = world.method_8320(blockPos);
			if (blockState.getBlock() == Blocks.field_17350) {
				if (!(Boolean)blockState.method_11654(CampfireBlock.field_17352) && !(Boolean)blockState.method_11654(CampfireBlock.field_17354)) {
					this.method_18453(world, blockPos);
					world.method_8501(blockPos, blockState.method_11657(CampfireBlock.field_17352, Boolean.valueOf(true)));
				}
			} else {
				blockPos = blockPos.offset(itemUsageContext.getSide());
				if (world.method_8320(blockPos).isAir()) {
					this.method_18453(world, blockPos);
					world.method_8501(blockPos, ((FireBlock)Blocks.field_10036).method_10198(world, blockPos));
				}
			}

			itemUsageContext.getStack().decrement(1);
			return ActionResult.field_5812;
		}
	}

	private void method_18453(World world, BlockPos blockPos) {
		world.playSound(null, blockPos, SoundEvents.field_15013, SoundCategory.field_15245, 1.0F, (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.0F);
	}
}
