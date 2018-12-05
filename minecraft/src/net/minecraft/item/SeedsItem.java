package net.minecraft.item;

import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class SeedsItem extends Item {
	private final BlockState field_8911;

	public SeedsItem(Block block, Item.Settings settings) {
		super(settings);
		this.field_8911 = block.getDefaultState();
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		IWorld iWorld = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getPos().up();
		if (itemUsageContext.method_8038() == Direction.UP && iWorld.isAir(blockPos) && this.field_8911.canPlaceAt(iWorld, blockPos)) {
			iWorld.setBlockState(blockPos, this.field_8911, 11);
			ItemStack itemStack = itemUsageContext.getItemStack();
			PlayerEntity playerEntity = itemUsageContext.getPlayer();
			if (playerEntity instanceof ServerPlayerEntity) {
				CriterionCriterions.PLACED_BLOCK.handle((ServerPlayerEntity)playerEntity, blockPos, itemStack);
			}

			itemStack.subtractAmount(1);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAILURE;
		}
	}
}
