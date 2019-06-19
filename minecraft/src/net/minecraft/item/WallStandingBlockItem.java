package net.minecraft.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;

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
		ViewableWorld viewableWorld = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();

		for (Direction direction : itemPlacementContext.getPlacementDirections()) {
			if (direction != Direction.field_11036) {
				BlockState blockState3 = direction == Direction.field_11033 ? this.getBlock().getPlacementState(itemPlacementContext) : blockState;
				if (blockState3 != null && blockState3.canPlaceAt(viewableWorld, blockPos)) {
					blockState2 = blockState3;
					break;
				}
			}
		}

		return blockState2 != null && viewableWorld.canPlace(blockState2, blockPos, EntityContext.absent()) ? blockState2 : null;
	}

	@Override
	public void appendBlocks(Map<Block, Item> map, Item item) {
		super.appendBlocks(map, item);
		map.put(this.wallBlock, item);
	}
}
