package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class WallTorchBlock extends TorchBlock {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	private static final Map<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.NORTH,
			Block.createCuboidShape(5.5, 3.0, 11.0, 10.5, 13.0, 16.0),
			Direction.SOUTH,
			Block.createCuboidShape(5.5, 3.0, 0.0, 10.5, 13.0, 5.0),
			Direction.WEST,
			Block.createCuboidShape(11.0, 3.0, 5.5, 16.0, 13.0, 10.5),
			Direction.EAST,
			Block.createCuboidShape(0.0, 3.0, 5.5, 5.0, 13.0, 10.5)
		)
	);

	protected WallTorchBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public String getTranslationKey() {
		return this.asItem().getTranslationKey();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return getBoundingShape(blockState);
	}

	public static VoxelShape getBoundingShape(BlockState blockState) {
		return (VoxelShape)BOUNDING_SHAPES.get(blockState.get(FACING));
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Direction direction = blockState.get(FACING);
		BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		return blockState2.isSideSolidFullSquare(viewableWorld, blockPos2, direction);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.getDefaultState();
		ViewableWorld viewableWorld = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		Direction[] directions = itemPlacementContext.getPlacementDirections();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction2 = direction.getOpposite();
				blockState = blockState.with(FACING, direction2);
				if (blockState.canPlaceAt(viewableWorld, blockPos)) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return direction.getOpposite() == blockState.get(FACING) && !blockState.canPlaceAt(iWorld, blockPos) ? Blocks.AIR.getDefaultState() : blockState;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		Direction direction = blockState.get(FACING);
		double d = (double)blockPos.getX() + 0.5;
		double e = (double)blockPos.getY() + 0.7;
		double f = (double)blockPos.getZ() + 0.5;
		double g = 0.22;
		double h = 0.27;
		Direction direction2 = direction.getOpposite();
		world.addParticle(ParticleTypes.SMOKE, d + 0.27 * (double)direction2.getOffsetX(), e + 0.22, f + 0.27 * (double)direction2.getOffsetZ(), 0.0, 0.0, 0.0);
		world.addParticle(ParticleTypes.FLAME, d + 0.27 * (double)direction2.getOffsetX(), e + 0.22, f + 0.27 * (double)direction2.getOffsetZ(), 0.0, 0.0, 0.0);
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		return blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
