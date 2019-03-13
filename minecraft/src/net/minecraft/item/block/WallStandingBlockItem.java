package net.minecraft.item.block;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;

public class WallStandingBlockItem extends BlockItem {
	protected final Block field_8918;

	public WallStandingBlockItem(Block block, Block block2, Item.Settings settings) {
		super(block, settings);
		this.field_8918 = block2;
	}

	@Nullable
	@Override
	protected BlockState method_7707(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.field_8918.method_9605(itemPlacementContext);
		BlockState blockState2 = null;
		ViewableWorld viewableWorld = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.method_8037();

		for (Direction direction : itemPlacementContext.method_7718()) {
			if (direction != Direction.UP) {
				BlockState blockState3 = direction == Direction.DOWN ? this.method_7711().method_9605(itemPlacementContext) : blockState;
				if (blockState3 != null && blockState3.method_11591(viewableWorld, blockPos)) {
					blockState2 = blockState3;
					break;
				}
			}
		}

		return blockState2 != null && viewableWorld.method_8628(blockState2, blockPos, VerticalEntityPosition.minValue()) ? blockState2 : null;
	}

	@Override
	public void method_7713(Map<Block, Item> map, Item item) {
		super.method_7713(map, item);
		map.put(this.field_8918, item);
	}
}
