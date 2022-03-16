package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class SculkShriekerBlock extends BlockWithEntity implements Waterloggable {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	public static final BooleanProperty SHRIEKING = Properties.SHRIEKING;
	private static final BooleanProperty field_37608 = Properties.WATERLOGGED;

	public SculkShriekerBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(SHRIEKING, Boolean.valueOf(false)).with(field_37608, Boolean.valueOf(false)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(SHRIEKING);
		builder.add(field_37608);
	}

	@Override
	public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		return SHAPE;
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SculkShriekerBlockEntity(pos, state);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(field_37608)) {
			world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(field_37608) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
		super.onStacksDropped(state, world, pos, stack);
		this.dropExperienceWhenMined(world, pos, stack, ConstantIntProvider.create(5));
	}
}
