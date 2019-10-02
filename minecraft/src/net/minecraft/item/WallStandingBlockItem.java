package net.minecraft.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

public class WallStandingBlockItem extends BlockItem {
	protected final Block wallBlock;

	public WallStandingBlockItem(Block block, Block block2, Item.Settings settings) {
		super(block, settings);
		this.wallBlock = block2;
	}

	@Nullable
	@Override
	protected BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.wallBlock.getPlacementState(itemPlacementContext);
		BlockState blockState2 = null;
		WorldView worldView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();

		for (Direction direction : itemPlacementContext.getPlacementDirections()) {
			if (direction != Direction.UP) {
				BlockState blockState3 = direction == Direction.DOWN ? this.getBlock().getPlacementState(itemPlacementContext) : blockState;
				if (blockState3 != null && blockState3.canPlaceAt(worldView, blockPos)) {
					blockState2 = blockState3;
					break;
				}
			}
		}

		return blockState2 != null && worldView.canPlace(blockState2, blockPos, EntityContext.absent()) ? blockState2 : null;
	}

	@Override
	public void appendBlocks(Map<Block, Item> map, Item item) {
		super.appendBlocks(map, item);
		map.put(this.wallBlock, item);
	}
}
