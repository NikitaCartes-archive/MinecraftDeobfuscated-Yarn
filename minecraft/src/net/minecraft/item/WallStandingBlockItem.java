package net.minecraft.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

public class WallStandingBlockItem extends BlockItem {
	protected final Block wallBlock;

	public WallStandingBlockItem(Block standingBlock, Block wallBlock, Item.Settings settings) {
		super(standingBlock, settings);
		this.wallBlock = wallBlock;
	}

	@Nullable
	@Override
	protected BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = this.wallBlock.getPlacementState(context);
		BlockState blockState2 = null;
		WorldView worldView = context.getWorld();
		BlockPos blockPos = context.getBlockPos();

		for (Direction direction : context.getPlacementDirections()) {
			if (direction != Direction.UP) {
				BlockState blockState3 = direction == Direction.DOWN ? this.getBlock().getPlacementState(context) : blockState;
				if (blockState3 != null && blockState3.canPlaceAt(worldView, blockPos)) {
					blockState2 = blockState3;
					break;
				}
			}
		}

		return blockState2 != null && worldView.canPlace(blockState2, blockPos, ShapeContext.absent()) ? blockState2 : null;
	}

	@Override
	public void appendBlocks(Map<Block, Item> map, Item item) {
		super.appendBlocks(map, item);
		map.put(this.wallBlock, item);
	}
}
