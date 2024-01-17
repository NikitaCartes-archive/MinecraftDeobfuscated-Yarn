package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.ToIntFunction;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class GlowLichenBlock extends MultifaceGrowthBlock implements Fertilizable, Waterloggable {
	public static final MapCodec<GlowLichenBlock> CODEC = createCodec(GlowLichenBlock::new);
	private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	private final LichenGrower grower = new LichenGrower(this);

	@Override
	public MapCodec<GlowLichenBlock> getCodec() {
		return CODEC;
	}

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
		return state -> MultifaceGrowthBlock.hasAnyDirection(state) ? luminance : 0;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(WATERLOGGED);
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected boolean canReplace(BlockState state, ItemPlacementContext context) {
		return !context.getStack().isOf(Items.GLOW_LICHEN) || super.canReplace(state, context);
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
		return Direction.stream().anyMatch(direction -> this.grower.canGrow(state, world, pos, direction.getOpposite()));
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		this.grower.grow(state, world, pos, random);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
		return state.getFluidState().isEmpty();
	}

	@Override
	public LichenGrower getGrower() {
		return this.grower;
	}
}
