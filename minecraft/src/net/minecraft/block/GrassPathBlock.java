package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class GrassPathBlock extends Block {
	protected static final VoxelShape SHAPE = FarmlandBlock.SHAPE;

	protected GrassPathBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return !this.getDefaultState().canPlaceAt(ctx.getWorld(), ctx.getBlockPos())
			? Block.pushEntitiesUpBeforeBlockChange(this.getDefaultState(), Blocks.DIRT.getDefaultState(), ctx.getWorld(), ctx.getBlockPos())
			: super.getPlacementState(ctx);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (facing == Direction.UP && !state.canPlaceAt(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
		}

		return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		FarmlandBlock.setToDirt(state, world, pos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.up());
		return !blockState.getMaterial().isSolid() || blockState.getBlock() instanceof FenceGateBlock;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
		return SHAPE;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType env) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasInWallOverlay(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}
}
