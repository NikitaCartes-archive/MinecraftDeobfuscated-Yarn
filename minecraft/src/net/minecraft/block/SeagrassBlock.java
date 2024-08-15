package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class SeagrassBlock extends PlantBlock implements Fertilizable, FluidFillable {
	public static final MapCodec<SeagrassBlock> CODEC = createCodec(SeagrassBlock::new);
	protected static final float field_31242 = 6.0F;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

	@Override
	public MapCodec<SeagrassBlock> getCodec() {
		return CODEC;
	}

	protected SeagrassBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return floor.isSideSolidFullSquare(world, pos, Direction.UP) && !floor.isOf(Blocks.MAGMA_BLOCK);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return fluidState.isIn(FluidTags.WATER) && fluidState.getLevel() == 8 ? super.getPlacementState(ctx) : null;
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		BlockState blockState = super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		if (!blockState.isAir()) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return blockState;
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
		return world.getBlockState(pos.up()).isOf(Blocks.WATER);
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return Fluids.WATER.getStill(false);
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		BlockState blockState = Blocks.TALL_SEAGRASS.getDefaultState();
		BlockState blockState2 = blockState.with(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);
		BlockPos blockPos = pos.up();
		world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
		world.setBlockState(blockPos, blockState2, Block.NOTIFY_LISTENERS);
	}

	@Override
	public boolean canFillWithFluid(@Nullable PlayerEntity player, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
		return false;
	}
}
