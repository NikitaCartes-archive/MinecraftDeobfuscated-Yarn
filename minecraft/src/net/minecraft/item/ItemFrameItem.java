package net.minecraft.item;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemFrameItem extends DecorationItem {
	public ItemFrameItem(Item.Settings settings) {
		super(ItemFrameEntity.class, settings);
	}

	@Override
	protected boolean method_7834(PlayerEntity playerEntity, Direction direction, ItemStack itemStack, BlockPos blockPos) {
		return !World.isHeightInvalid(blockPos) && playerEntity.canPlaceBlock(blockPos, direction, itemStack);
	}
}
