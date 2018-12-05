package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticle;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class RedstoneTorchWallBlock extends RedstoneTorchBlock {
	public static final DirectionProperty field_11443 = HorizontalFacingBlock.field_11177;
	public static final BooleanProperty field_11444 = RedstoneTorchBlock.field_11446;

	protected RedstoneTorchWallBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_11443, Direction.NORTH).with(field_11444, Boolean.valueOf(true)));
	}

	@Override
	public String getTranslationKey() {
		return this.getItem().getTranslationKey();
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return WallTorchBlock.getBoundingShape(blockState);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return Blocks.field_10099.canPlaceAt(blockState, viewableWorld, blockPos);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return Blocks.field_10099.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = Blocks.field_10099.getPlacementState(itemPlacementContext);
		return blockState == null ? null : this.getDefaultState().with(field_11443, blockState.get(field_11443));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.get(field_11444)) {
			Direction direction = ((Direction)blockState.get(field_11443)).getOpposite();
			double d = 0.27;
			double e = (double)blockPos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)direction.getOffsetX();
			double f = (double)blockPos.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2 + 0.22;
			double g = (double)blockPos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)direction.getOffsetZ();
			world.method_8406(DustParticle.field_11188, e, f, g, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected boolean method_10488(World world, BlockPos blockPos, BlockState blockState) {
		Direction direction = ((Direction)blockState.get(field_11443)).getOpposite();
		return world.method_8459(blockPos.method_10093(direction), direction);
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.get(field_11444) && blockState.get(field_11443) != direction ? 15 : 0;
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return Blocks.field_10099.applyRotation(blockState, rotation);
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return Blocks.field_10099.applyMirror(blockState, mirror);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11443, field_11444);
	}
}
