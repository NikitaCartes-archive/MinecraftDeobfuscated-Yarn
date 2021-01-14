package net.minecraft.block;

import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.SignType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class SignBlock extends AbstractSignBlock {
	public static final IntProperty ROTATION = Properties.ROTATION;

	public SignBlock(AbstractBlock.Settings settings, SignType signType) {
		super(settings, signType);
		this.setDefaultState(this.stateManager.getDefaultState().with(ROTATION, Integer.valueOf(0)).with(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return world.getBlockState(pos.down()).getMaterial().isSolid();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return this.getDefaultState()
			.with(ROTATION, Integer.valueOf(MathHelper.floor((double)((180.0F + ctx.getPlayerYaw()) * 16.0F / 360.0F) + 0.5) & 15))
			.with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return direction == Direction.DOWN && !this.canPlaceAt(state, world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(ROTATION, Integer.valueOf(rotation.rotate((Integer)state.get(ROTATION), 16)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.with(ROTATION, Integer.valueOf(mirror.mirror((Integer)state.get(ROTATION), 16)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(ROTATION, WATERLOGGED);
	}
}
