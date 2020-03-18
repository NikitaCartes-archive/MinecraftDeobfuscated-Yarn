package net.minecraft.block;

import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.EnumProperty;

public class ReplaceableTallPlantBlock extends TallPlantBlock {
	public static final EnumProperty<DoubleBlockHalf> HALF = TallPlantBlock.HALF;

	public ReplaceableTallPlantBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		boolean bl = super.canReplace(state, context);
		return bl && context.getStack().getItem() == this.asItem() ? false : bl;
	}
}
