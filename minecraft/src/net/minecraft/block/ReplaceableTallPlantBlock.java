package net.minecraft.block;

import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.EnumProperty;

public class ReplaceableTallPlantBlock extends TallPlantBlock {
	public static final EnumProperty<DoubleBlockHalf> HALF = TallPlantBlock.HALF;

	public ReplaceableTallPlantBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean canReplace(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		boolean bl = super.canReplace(blockState, itemPlacementContext);
		return bl && itemPlacementContext.getStack().getItem() == this.asItem() ? false : bl;
	}
}
