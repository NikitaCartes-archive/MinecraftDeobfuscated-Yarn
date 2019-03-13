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
	public ActionResult method_7884(ItemUsageContext itemUsageContext) {
		PlayerEntity playerEntity = itemUsageContext.getPlayer();
		IWorld iWorld = itemUsageContext.method_8045();
		BlockPos blockPos = itemUsageContext.method_8037();
		BlockPos blockPos2 = blockPos.method_10093(itemUsageContext.method_8038());
		if (method_7825(iWorld.method_8320(blockPos2), iWorld, blockPos2)) {
			iWorld.method_8396(playerEntity, blockPos2, SoundEvents.field_15145, SoundCategory.field_15245, 1.0F, random.nextFloat() * 0.4F + 0.8F);
			BlockState blockState = ((FireBlock)Blocks.field_10036).method_10198(iWorld, blockPos2);
			iWorld.method_8652(blockPos2, blockState, 11);
			ItemStack itemStack = itemUsageContext.getItemStack();
			if (playerEntity instanceof ServerPlayerEntity) {
				Criterions.PLACED_BLOCK.method_9087((ServerPlayerEntity)playerEntity, blockPos2, itemStack);
			}

			if (playerEntity != null) {
				itemStack.applyDamage(1, playerEntity);
			}

			return ActionResult.field_5812;
		} else {
			BlockState blockStatex = iWorld.method_8320(blockPos);
			if (method_17439(blockStatex)) {
				iWorld.method_8396(playerEntity, blockPos, SoundEvents.field_15145, SoundCategory.field_15245, 1.0F, random.nextFloat() * 0.4F + 0.8F);
				iWorld.method_8652(blockPos, blockStatex.method_11657(Properties.field_12548, Boolean.valueOf(true)), 11);
				if (playerEntity != null) {
					itemUsageContext.getItemStack().applyDamage(1, playerEntity);
				}

				return ActionResult.field_5812;
			} else {
				return ActionResult.field_5814;
			}
		}
	}

	public static boolean method_17439(BlockState blockState) {
		return blockState.getBlock() == Blocks.field_17350
			&& !(Boolean)blockState.method_11654(Properties.field_12508)
			&& !(Boolean)blockState.method_11654(Properties.field_12548);
	}

	public static boolean method_7825(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
		BlockState blockState2 = ((FireBlock)Blocks.field_10036).method_10198(iWorld, blockPos);
		boolean bl = false;

		for (Direction direction : Direction.Type.HORIZONTAL) {
			if (iWorld.method_8320(blockPos.method_10093(direction)).getBlock() == Blocks.field_10540
				&& ((PortalBlock)Blocks.field_10316).method_10351(iWorld, blockPos) != null) {
				bl = true;
			}
		}

		return blockState.isAir() && (blockState2.method_11591(iWorld, blockPos) || bl);
	}
}
