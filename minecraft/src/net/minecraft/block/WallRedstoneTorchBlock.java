package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class WallRedstoneTorchBlock extends RedstoneTorchBlock {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty LIT_2 = RedstoneTorchBlock.LIT;

	protected WallRedstoneTorchBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(LIT_2, Boolean.valueOf(true)));
	}

	@Override
	public String getTranslationKey() {
		return this.asItem().getTranslationKey();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return WallTorchBlock.getBoundingShape(state);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return Blocks.WALL_TORCH.canPlaceAt(state, world, pos);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return Blocks.WALL_TORCH.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = Blocks.WALL_TORCH.getPlacementState(ctx);
		return blockState == null ? null : this.getDefaultState().with(FACING, blockState.get(FACING));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Boolean)state.get(LIT_2)) {
			Direction direction = ((Direction)state.get(FACING)).getOpposite();
			double d = 0.27;
			double e = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)direction.getOffsetX();
			double f = (double)pos.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2 + 0.22;
			double g = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)direction.getOffsetZ();
			world.addParticle(this.field_22155, e, f, g, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected boolean shouldUnpower(World world, BlockPos pos, BlockState state) {
		Direction direction = ((Direction)state.get(FACING)).getOpposite();
		return world.isEmittingRedstonePower(pos.offset(direction), direction);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
		return state.get(LIT_2) && state.get(FACING) != facing ? 15 : 0;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return Blocks.WALL_TORCH.rotate(state, rotation);
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return Blocks.WALL_TORCH.mirror(state, mirror);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT_2);
	}
}
