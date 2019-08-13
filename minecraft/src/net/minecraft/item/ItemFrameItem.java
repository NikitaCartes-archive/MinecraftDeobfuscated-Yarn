package net.minecraft.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemFrameItem extends DecorationItem {
	public ItemFrameItem(Item.Settings settings) {
		super(EntityType.field_6043, settings);
	}

	@Override
	protected boolean canPlaceOn(PlayerEntity playerEntity, Direction direction, ItemStack itemStack, BlockPos blockPos) {
		return !World.isHeightInvalid(blockPos) && playerEntity.canPlaceOn(blockPos, direction, itemStack);
	}
}
