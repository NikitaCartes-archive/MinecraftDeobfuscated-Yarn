package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;

public class CommandBlockItem extends BlockItem {
	public CommandBlockItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Nullable
	@Override
	protected BlockState getPlacementState(ItemPlacementContext context) {
		PlayerEntity playerEntity = context.getPlayer();
		return playerEntity != null && !playerEntity.isCreativeLevelTwoOp() ? null : super.getPlacementState(context);
	}
}
