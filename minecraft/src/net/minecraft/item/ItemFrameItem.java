package net.minecraft.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemFrameItem extends DecorationItem {
	public ItemFrameItem(Item.Settings settings) {
		super(EntityType.ITEM_FRAME, settings);
	}

	@Override
	protected boolean method_7834(PlayerEntity playerEntity, Direction direction, ItemStack itemStack, BlockPos blockPos) {
		return !World.method_8518(blockPos) && playerEntity.method_7343(blockPos, direction, itemStack);
	}
}
