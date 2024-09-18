package net.minecraft.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class AmethystClusterBlock extends AmethystBlock implements Waterloggable {
	public static final MapCodec<AmethystClusterBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.FLOAT.fieldOf("height").forGetter(block -> block.height),
					Codec.FLOAT.fieldOf("aabb_offset").forGetter(block -> block.xzOffset),
					createSettingsCodec()
				)
				.apply(instance, AmethystClusterBlock::new)
	);
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final EnumProperty<Direction> FACING = Properties.FACING;
	private final float height;
	private final float xzOffset;
	protected final VoxelShape northShape;
	protected final VoxelShape southShape;
	protected final VoxelShape eastShape;
	protected final VoxelShape westShape;
	protected final VoxelShape upShape;
	protected final VoxelShape downShape;

	@Override
	public MapCodec<AmethystClusterBlock> getCodec() {
		return CODEC;
	}

	public AmethystClusterBlock(float height, float xzOffset, AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(false)).with(FACING, Direction.UP));
		this.upShape = Block.createCuboidShape((double)xzOffset, 0.0, (double)xzOffset, (double)(16.0F - xzOffset), (double)height, (double)(16.0F - xzOffset));
		this.downShape = Block.createCuboidShape(
			(double)xzOffset, (double)(16.0F - height), (double)xzOffset, (double)(16.0F - xzOffset), 16.0, (double)(16.0F - xzOffset)
		);
		this.northShape = Block.createCuboidShape(
			(double)xzOffset, (double)xzOffset, (double)(16.0F - height), (double)(16.0F - xzOffset), (double)(16.0F - xzOffset), 16.0
		);
		this.southShape = Block.createCuboidShape((double)xzOffset, (double)xzOffset, 0.0, (double)(16.0F - xzOffset), (double)(16.0F - xzOffset), (double)height);
		this.eastShape = Block.createCuboidShape(0.0, (double)xzOffset, (double)xzOffset, (double)height, (double)(16.0F - xzOffset), (double)(16.0F - xzOffset));
		this.westShape = Block.createCuboidShape(
			(double)(16.0F - height), (double)xzOffset, (double)xzOffset, 16.0, (double)(16.0F - xzOffset), (double)(16.0F - xzOffset)
		);
		this.height = height;
		this.xzOffset = xzOffset;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Direction direction = state.get(FACING);
		switch (direction) {
			case NORTH:
				return this.northShape;
			case SOUTH:
				return this.southShape;
			case EAST:
				return this.eastShape;
			case WEST:
				return this.westShape;
			case DOWN:
				return this.downShape;
			case UP:
			default:
				return this.upShape;
		}
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		Direction direction = state.get(FACING);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		return world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, direction);
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state,
		WorldView world,
		ScheduledTickView tickView,
		BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		Random random
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return direction == ((Direction)state.get(FACING)).getOpposite() && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		WorldAccess worldAccess = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		return this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(worldAccess.getFluidState(blockPos).getFluid() == Fluids.WATER)).with(FACING, ctx.getSide());
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, FACING);
	}
}
