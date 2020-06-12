package net.minecraft.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class FlintAndSteelItem extends Item {
	public FlintAndSteelItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity playerEntity = context.getPlayer();
		WorldAccess worldAccess = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = worldAccess.getBlockState(blockPos);
		if (CampfireBlock.method_30035(blockState)) {
			worldAccess.playSound(playerEntity, blockPos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
			worldAccess.setBlockState(blockPos, blockState.with(Properties.LIT, Boolean.valueOf(true)), 11);
			if (playerEntity != null) {
				context.getStack().damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
			}

			return ActionResult.success(worldAccess.isClient());
		} else {
			BlockPos blockPos2 = blockPos.offset(context.getSide());
			if (AbstractFireBlock.method_30032(worldAccess, blockPos2)) {
				worldAccess.playSound(playerEntity, blockPos2, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
				BlockState blockState2 = AbstractFireBlock.getState(worldAccess, blockPos2);
				worldAccess.setBlockState(blockPos2, blockState2, 11);
				ItemStack itemStack = context.getStack();
				if (playerEntity instanceof ServerPlayerEntity) {
					Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos2, itemStack);
					itemStack.damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
				}

				return ActionResult.success(worldAccess.isClient());
			} else {
				return ActionResult.FAIL;
			}
		}
	}
}
