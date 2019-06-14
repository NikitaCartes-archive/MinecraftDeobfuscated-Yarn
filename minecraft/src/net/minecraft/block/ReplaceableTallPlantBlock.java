package net.minecraft.block;

import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.EnumProperty;

public class ReplaceableTallPlantBlock extends TallPlantBlock {
	public static final EnumProperty<DoubleBlockHalf> field_11484 = TallPlantBlock.field_10929;

	public ReplaceableTallPlantBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean method_9616(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		boolean bl = super.method_9616(blockState, itemPlacementContext);
		return bl && itemPlacementContext.getStack().getItem() == this.asItem() ? false : bl;
	}
}
