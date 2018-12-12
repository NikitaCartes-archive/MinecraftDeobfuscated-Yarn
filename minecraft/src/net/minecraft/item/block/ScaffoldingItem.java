package net.minecraft.item.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ScaffoldingItem extends BlockItem {
	public ScaffoldingItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Override
	public ItemPlacementContext getPlacementContext(ItemPlacementContext itemPlacementContext) {
		BlockPos blockPos = itemPlacementContext.getPos();
		World world = itemPlacementContext.getWorld();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == this.getBlock() && itemPlacementContext.getFacing().getAxis() == Direction.Axis.Y) {
			BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos).setOffset(Direction.UP);

			while (blockState.getBlock() == this.getBlock() && !World.isHeightInvalid(mutable)) {
				blockState = world.getBlockState(mutable);
				if (blockState.isAir()) {
					return ItemPlacementContext.create(itemPlacementContext, mutable, Direction.UP);
				}

				mutable.setOffset(Direction.UP);
			}
		}

		return itemPlacementContext;
	}
}
