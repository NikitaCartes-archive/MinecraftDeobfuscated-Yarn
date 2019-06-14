package net.minecraft.block;

import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class SignBlock extends AbstractSignBlock {
	public static final IntProperty field_11559 = Properties.field_12532;

	public SignBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11559, Integer.valueOf(0)).method_11657(field_11491, Boolean.valueOf(false)));
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return viewableWorld.method_8320(blockPos.down()).method_11620().isSolid();
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		FluidState fluidState = itemPlacementContext.method_8045().method_8316(itemPlacementContext.getBlockPos());
		return this.method_9564()
			.method_11657(field_11559, Integer.valueOf(MathHelper.floor((double)((180.0F + itemPlacementContext.getPlayerYaw()) * 16.0F / 360.0F) + 0.5) & 15))
			.method_11657(field_11491, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return direction == Direction.field_11033 && !this.method_9558(blockState, iWorld, blockPos)
			? Blocks.field_10124.method_9564()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_11559, Integer.valueOf(blockRotation.rotate((Integer)blockState.method_11654(field_11559), 16)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.method_11657(field_11559, Integer.valueOf(blockMirror.mirror((Integer)blockState.method_11654(field_11559), 16)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11559, field_11491);
	}
}
