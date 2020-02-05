package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class WeepingVinesPlantBlock extends Block {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
	private final WeepingVinesBlock field_22173;

	public WeepingVinesPlantBlock(WeepingVinesBlock weepingVinesBlock, Block.Settings settings) {
		super(settings);
		this.field_22173 = weepingVinesBlock;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return SHAPE;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!state.canPlaceAt(world, pos)) {
			world.breakBlock(pos, true);
		}

		super.scheduledTick(state, world, pos, random);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (facing == Direction.UP && !state.canPlaceAt(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
		}

		if (facing == Direction.DOWN) {
			Block block = neighborState.getBlock();
			if (block != this && block != this.field_22173) {
				return this.field_22173.method_24430(world);
			}
		}

		return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.up();
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		return block == this || blockState.isSideSolidFullSquare(world, blockPos, Direction.DOWN);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(Blocks.WEEPING_VINES);
	}
}
