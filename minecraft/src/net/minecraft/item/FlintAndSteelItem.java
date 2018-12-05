package net.minecraft.item;

import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.PortalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
		BlockPos blockPos = itemUsageContext.getPos().method_10093(itemUsageContext.method_8038());
		if (method_7825(iWorld, blockPos)) {
			iWorld.playSound(playerEntity, blockPos, SoundEvents.field_15145, SoundCategory.field_15245, 1.0F, random.nextFloat() * 0.4F + 0.8F);
			BlockState blockState = ((FireBlock)Blocks.field_10036).method_10198(iWorld, blockPos);
			iWorld.setBlockState(blockPos, blockState, 11);
			ItemStack itemStack = itemUsageContext.getItemStack();
			if (playerEntity instanceof ServerPlayerEntity) {
				CriterionCriterions.PLACED_BLOCK.handle((ServerPlayerEntity)playerEntity, blockPos, itemStack);
			}

			if (playerEntity != null) {
				itemStack.applyDamage(1, playerEntity);
			}

			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAILURE;
		}
	}

	public static boolean method_7825(IWorld iWorld, BlockPos blockPos) {
		BlockState blockState = ((FireBlock)Blocks.field_10036).method_10198(iWorld, blockPos);
		boolean bl = false;

		for (Direction direction : Direction.class_2353.HORIZONTAL) {
			if (iWorld.getBlockState(blockPos.method_10093(direction)).getBlock() == Blocks.field_10540
				&& ((PortalBlock)Blocks.field_10316).method_10351(iWorld, blockPos) != null) {
				bl = true;
			}
		}

		return iWorld.isAir(blockPos) && (blockState.canPlaceAt(iWorld, blockPos) || bl);
	}
}
