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
		BlockPos blockPos2 = blockPos.offset(itemUsageContext.getFacing());
		if (method_7825(iWorld.getBlockState(blockPos2), iWorld, blockPos2)) {
			iWorld.playSound(playerEntity, blockPos2, SoundEvents.field_15145, SoundCategory.field_15245, 1.0F, random.nextFloat() * 0.4F + 0.8F);
			BlockState blockState = ((FireBlock)Blocks.field_10036).getStateForPosition(iWorld, blockPos2);
			iWorld.setBlockState(blockPos2, blockState, 11);
			ItemStack itemStack = itemUsageContext.getItemStack();
			if (playerEntity instanceof ServerPlayerEntity) {
				Criterions.PLACED_BLOCK.handle((ServerPlayerEntity)playerEntity, blockPos2, itemStack);
			}

			if (playerEntity != null) {
				itemStack.applyDamage(1, playerEntity);
			}

			return ActionResult.SUCCESS;
		} else {
			BlockState blockStatex = iWorld.getBlockState(blockPos);
			if (method_17439(blockStatex)) {
				iWorld.playSound(playerEntity, blockPos, SoundEvents.field_15145, SoundCategory.field_15245, 1.0F, random.nextFloat() * 0.4F + 0.8F);
				iWorld.setBlockState(blockPos, blockStatex.with(Properties.LIT, Boolean.valueOf(true)), 11);
				if (playerEntity != null) {
					itemUsageContext.getItemStack().applyDamage(1, playerEntity);
				}

				return ActionResult.SUCCESS;
			} else {
				return ActionResult.FAILURE;
			}
		}
	}

	public static boolean method_17439(BlockState blockState) {
		return blockState.getBlock() == Blocks.field_17350 && !(Boolean)blockState.get(Properties.WATERLOGGED) && !(Boolean)blockState.get(Properties.LIT);
	}

	public static boolean method_7825(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
		BlockState blockState2 = ((FireBlock)Blocks.field_10036).getStateForPosition(iWorld, blockPos);
		boolean bl = false;

		for (Direction direction : Direction.Type.HORIZONTAL) {
			if (iWorld.getBlockState(blockPos.offset(direction)).getBlock() == Blocks.field_10540
				&& ((PortalBlock)Blocks.field_10316).method_10351(iWorld, blockPos) != null) {
				bl = true;
			}
		}

		return blockState.isAir() && (blockState2.canPlaceAt(iWorld, blockPos) || bl);
	}
}
