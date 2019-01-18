package net.minecraft.item.block;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;

public class WallStandingBlockItem extends BlockItem {
	protected final Block block;

	public WallStandingBlockItem(Block block, Block block2, Item.Settings settings) {
		super(block, settings);
		this.block = block2;
	}

	@Nullable
	@Override
	protected BlockState getBlockState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.block.getPlacementState(itemPlacementContext);
		BlockState blockState2 = null;
		ViewableWorld viewableWorld = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();

		for (Direction direction : itemPlacementContext.getPlacementFacings()) {
			if (direction != Direction.UP) {
				BlockState blockState3 = direction == Direction.DOWN ? this.getBlock().getPlacementState(itemPlacementContext) : blockState;
				if (blockState3 != null && blockState3.canPlaceAt(viewableWorld, blockPos)) {
					blockState2 = blockState3;
					break;
				}
			}
		}

		return blockState2 != null && viewableWorld.method_8628(blockState2, blockPos) ? blockState2 : null;
	}

	@Override
	public void registerBlockItemMap(Map<Block, Item> map, Item item) {
		super.registerBlockItemMap(map, item);
		map.put(this.block, item);
	}
}
