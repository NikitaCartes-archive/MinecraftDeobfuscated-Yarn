package net.minecraft.item;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.PortalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class FlintAndSteelItem extends Item {
	public FlintAndSteelItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		PlayerEntity playerEntity = itemUsageContext.getPlayer();
		IWorld iWorld = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockPos blockPos2 = blockPos.offset(itemUsageContext.getSide());
		if (canIgnite(iWorld.getBlockState(blockPos2), iWorld, blockPos2)) {
			iWorld.playSound(playerEntity, blockPos2, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
			BlockState blockState = ((FireBlock)Blocks.FIRE).getStateForPosition(iWorld, blockPos2);
			iWorld.setBlockState(blockPos2, blockState, 11);
			ItemStack itemStack = itemUsageContext.getStack();
			if (playerEntity instanceof ServerPlayerEntity) {
				Criterions.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos2, itemStack);
				itemStack.damage(1, playerEntity, playerEntityx -> playerEntityx.sendToolBreakStatus(itemUsageContext.getHand()));
			}

			return ActionResult.SUCCESS;
		} else {
			BlockState blockState = iWorld.getBlockState(blockPos);
			if (isIgnitable(blockState)) {
				iWorld.playSound(playerEntity, blockPos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
				iWorld.setBlockState(blockPos, blockState.with(Properties.LIT, Boolean.valueOf(true)), 11);
				if (playerEntity != null) {
					itemUsageContext.getStack().damage(1, playerEntity, playerEntityx -> playerEntityx.sendToolBreakStatus(itemUsageContext.getHand()));
				}

				return ActionResult.SUCCESS;
			} else {
				return ActionResult.FAIL;
			}
		}
	}

	public static boolean isIgnitable(BlockState blockState) {
		return blockState.getBlock() == Blocks.CAMPFIRE && !(Boolean)blockState.get(Properties.WATERLOGGED) && !(Boolean)blockState.get(Properties.LIT);
	}

	public static boolean canIgnite(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
		BlockState blockState2 = ((FireBlock)Blocks.FIRE).getStateForPosition(iWorld, blockPos);
		boolean bl = false;

		for (Direction direction : Direction.Type.HORIZONTAL) {
			if (iWorld.getBlockState(blockPos.offset(direction)).getBlock() == Blocks.OBSIDIAN
				&& ((PortalBlock)Blocks.NETHER_PORTAL).createAreaHelper(iWorld, blockPos) != null) {
				bl = true;
			}
		}

		return blockState.isAir() && (blockState2.canPlaceAt(iWorld, blockPos) || bl);
	}
}
