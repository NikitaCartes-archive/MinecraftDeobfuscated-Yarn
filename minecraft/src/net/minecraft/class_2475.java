package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.EnumProperty;

public class class_2475 extends TallPlantBlock {
	public static final EnumProperty<DoubleBlockHalf> field_11484 = TallPlantBlock.HALF;

	public class_2475(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean canReplace(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		boolean bl = super.canReplace(blockState, itemPlacementContext);
		return bl && itemPlacementContext.getItemStack().getItem() == this.getItem() ? false : bl;
	}
}
