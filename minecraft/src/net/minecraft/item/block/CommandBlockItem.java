package net.minecraft.item.block;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;

public class CommandBlockItem extends BlockItem {
	public CommandBlockItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Nullable
	@Override
	protected BlockState getBlockState(ItemPlacementContext itemPlacementContext) {
		PlayerEntity playerEntity = itemPlacementContext.getPlayer();
		return playerEntity != null && !playerEntity.method_7338() ? null : super.getBlockState(itemPlacementContext);
	}
}
