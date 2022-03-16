package net.minecraft.block;

import java.util.Random;
import java.util.function.ToIntFunction;
import net.minecraft.class_7118;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class GlowLichenBlock extends AbstractLichenBlock implements Fertilizable, Waterloggable {
	private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	private final class_7118 field_37585 = new class_7118(this);

	public GlowLichenBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(false)));
	}

	/**
	 * {@return a function that receives a {@link BlockState} and returns the luminance for the state}
	 * If the lichen has no visible sides, it supplies 0.
	 * 
	 * @apiNote The return value is meant to be passed to
	 * {@link AbstractBlock.Settings#luminance} builder method.
	 * 
	 * @param luminance luminance supplied when the lichen has at least one visible side
	 */
	public static ToIntFunction<BlockState> getLuminanceSupplier(int luminance) {
		return state -> AbstractLichenBlock.hasAnyDirection(state) ? luminance : 0;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(WATERLOGGED);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return !context.getStack().isOf(Items.GLOW_LICHEN) || super.canReplace(state, context);
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return Direction.stream().anyMatch(direction -> this.field_37585.method_41443(state, world, pos, direction.getOpposite()));
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		this.field_37585.method_41450(state, world, pos, random);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
		return state.getFluidState().isEmpty();
	}

	@Override
	public class_7118 method_41432() {
		return this.field_37585;
	}
}
