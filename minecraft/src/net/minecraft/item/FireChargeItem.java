package net.minecraft.item;

import net.minecraft.block.Blocks;
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
		if (world.isRemote) {
			return ActionResult.SUCCESS;
		} else {
			BlockPos blockPos = itemUsageContext.getPos().method_10093(itemUsageContext.method_8038());
			if (world.getBlockState(blockPos).isAir()) {
				world.playSound(null, blockPos, SoundEvents.field_15013, SoundCategory.field_15245, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
				world.setBlockState(blockPos, ((FireBlock)Blocks.field_10036).method_10198(world, blockPos));
			}

			itemUsageContext.getItemStack().subtractAmount(1);
			return ActionResult.SUCCESS;
		}
	}
}
