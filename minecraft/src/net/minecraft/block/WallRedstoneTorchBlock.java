package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4538;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
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

public class WallRedstoneTorchBlock extends RedstoneTorchBlock {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty LIT_2 = RedstoneTorchBlock.LIT;

	protected WallRedstoneTorchBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.NORTH).with(LIT_2, Boolean.valueOf(true)));
	}

	@Override
	public String getTranslationKey() {
		return this.asItem().getTranslationKey();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return WallTorchBlock.getBoundingShape(blockState);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, class_4538 arg, BlockPos blockPos) {
		return Blocks.WALL_TORCH.canPlaceAt(blockState, arg, blockPos);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return Blocks.WALL_TORCH.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = Blocks.WALL_TORCH.getPlacementState(itemPlacementContext);
		return blockState == null ? null : this.getDefaultState().with(FACING, blockState.get(FACING));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.get(LIT_2)) {
			Direction direction = ((Direction)blockState.get(FACING)).getOpposite();
			double d = 0.27;
			double e = (double)blockPos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)direction.getOffsetX();
			double f = (double)blockPos.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2 + 0.22;
			double g = (double)blockPos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)direction.getOffsetZ();
			world.addParticle(DustParticleEffect.RED, e, f, g, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected boolean shouldUnpower(World world, BlockPos blockPos, BlockState blockState) {
		Direction direction = ((Direction)blockState.get(FACING)).getOpposite();
		return world.isEmittingRedstonePower(blockPos.offset(direction), direction);
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.get(LIT_2) && blockState.get(FACING) != direction ? 15 : 0;
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		return Blocks.WALL_TORCH.rotate(blockState, blockRotation);
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		return Blocks.WALL_TORCH.mirror(blockState, blockMirror);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT_2);
	}
}
