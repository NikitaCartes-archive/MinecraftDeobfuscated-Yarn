package net.minecraft.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ItemFrameItem extends DecorationItem {
	public ItemFrameItem(Item.Settings settings) {
		super(EntityType.ITEM_FRAME, settings);
	}

	@Override
	protected boolean canPlaceOn(PlayerEntity player, Direction side, ItemStack stack, BlockPos pos) {
		return !player.world.isOutOfHeightLimit(pos) && player.canPlaceOn(pos, side, stack);
	}
}
