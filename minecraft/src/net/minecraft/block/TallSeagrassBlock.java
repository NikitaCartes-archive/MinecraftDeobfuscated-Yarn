package net.minecraft.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2402;
import net.minecraft.class_2475;
import net.minecraft.block.enums.BlockHalf;
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

public class TallSeagrassBlock extends class_2475 implements class_2402 {
	public static final EnumProperty<BlockHalf> PROPERTY_HALF = class_2475.field_11484;
	protected static final VoxelShape SHAPE = Block.createCubeShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

	public TallSeagrassBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return SHAPE;
	}

	@Override
	protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return Block.method_9501(blockState.method_11628(blockView, blockPos), Direction.UP) && blockState.getBlock() != Blocks.field_10092;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Blocks.field_10376);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = super.getPlacementState(itemPlacementContext);
		if (blockState != null) {
			FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getPos().up());
			if (fluidState.matches(FluidTags.field_15517) && fluidState.method_15761() == 8) {
				return blockState;
			}
		}

		return null;
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		if (blockState.get(PROPERTY_HALF) == BlockHalf.field_12609) {
			BlockState blockState2 = viewableWorld.getBlockState(blockPos.down());
			return blockState2.getBlock() == this && blockState2.get(PROPERTY_HALF) == BlockHalf.field_12607;
		} else {
			FluidState fluidState = viewableWorld.getFluidState(blockPos);
			return super.canPlaceAt(blockState, viewableWorld, blockPos) && fluidState.matches(FluidTags.field_15517) && fluidState.method_15761() == 8;
		}
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return Fluids.WATER.getState(false);
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
