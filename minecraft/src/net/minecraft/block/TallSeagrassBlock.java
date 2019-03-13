package net.minecraft.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class TallSeagrassBlock extends ReplaceableTallPlantBlock implements FluidFillable {
	public static final EnumProperty<DoubleBlockHalf> field_11616 = ReplaceableTallPlantBlock.field_11484;
	protected static final VoxelShape field_11615 = Block.method_9541(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

	public TallSeagrassBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_11615;
	}

	@Override
	protected boolean method_9695(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return Block.method_9501(blockState.method_11628(blockView, blockPos), Direction.UP) && blockState.getBlock() != Blocks.field_10092;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Blocks.field_10376);
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = super.method_9605(itemPlacementContext);
		if (blockState != null) {
			FluidState fluidState = itemPlacementContext.method_8045().method_8316(itemPlacementContext.method_8037().up());
			if (fluidState.method_15767(FluidTags.field_15517) && fluidState.getLevel() == 8) {
				return blockState;
			}
		}

		return null;
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		if (blockState.method_11654(field_11616) == DoubleBlockHalf.field_12609) {
			BlockState blockState2 = viewableWorld.method_8320(blockPos.down());
			return blockState2.getBlock() == this && blockState2.method_11654(field_11616) == DoubleBlockHalf.field_12607;
		} else {
			FluidState fluidState = viewableWorld.method_8316(blockPos);
			return super.method_9558(blockState, viewableWorld, blockPos) && fluidState.method_15767(FluidTags.field_15517) && fluidState.getLevel() == 8;
		}
	}

	@Override
	public FluidState method_9545(BlockState blockState) {
		return Fluids.WATER.method_15729(false);
	}

	@Override
	public boolean method_10310(BlockView blockView, BlockPos blockPos, BlockState blockState, Fluid fluid) {
		return false;
	}

	@Override
	public boolean method_10311(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		return false;
	}
}
