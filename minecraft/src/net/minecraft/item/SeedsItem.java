package net.minecraft.item;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class SeedsItem extends Item {
	private final BlockState crop;

	public SeedsItem(Block block, Item.Settings settings) {
		super(settings);
		this.crop = block.getDefaultState();
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		IWorld iWorld = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getPos().up();
		if (itemUsageContext.getFacing() == Direction.UP && iWorld.isAir(blockPos) && this.crop.canPlaceAt(iWorld, blockPos)) {
			iWorld.setBlockState(blockPos, this.crop, 11);
			ItemStack itemStack = itemUsageContext.getItemStack();
			PlayerEntity playerEntity = itemUsageContext.getPlayer();
			if (playerEntity instanceof ServerPlayerEntity) {
				Criterions.PLACED_BLOCK.handle((ServerPlayerEntity)playerEntity, blockPos, itemStack);
			}

			itemStack.subtractAmount(1);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAILURE;
		}
	}
}
