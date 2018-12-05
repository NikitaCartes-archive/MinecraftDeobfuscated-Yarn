package net.minecraft.block;

import java.util.Random;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2760;
import net.minecraft.block.enums.StairShape;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class StairsBlock extends Block implements Waterloggable {
	public static final DirectionProperty field_11571 = HorizontalFacingBlock.field_11177;
	public static final EnumProperty<class_2760> field_11572 = Properties.field_12518;
	public static final EnumProperty<StairShape> field_11565 = Properties.STAIR_SHAPE;
	public static final BooleanProperty field_11573 = Properties.WATERLOGGED;
	protected static final VoxelShape field_11562 = SlabBlock.field_11499;
	protected static final VoxelShape field_11576 = SlabBlock.field_11500;
	protected static final VoxelShape field_11561 = Block.createCubeShape(0.0, 0.0, 0.0, 8.0, 8.0, 8.0);
	protected static final VoxelShape field_11578 = Block.createCubeShape(0.0, 0.0, 8.0, 8.0, 8.0, 16.0);
	protected static final VoxelShape field_11568 = Block.createCubeShape(0.0, 8.0, 0.0, 8.0, 16.0, 8.0);
	protected static final VoxelShape field_11563 = Block.createCubeShape(0.0, 8.0, 8.0, 8.0, 16.0, 16.0);
	protected static final VoxelShape field_11575 = Block.createCubeShape(8.0, 0.0, 0.0, 16.0, 8.0, 8.0);
	protected static final VoxelShape field_11569 = Block.createCubeShape(8.0, 0.0, 8.0, 16.0, 8.0, 16.0);
	protected static final VoxelShape field_11577 = Block.createCubeShape(8.0, 8.0, 0.0, 16.0, 16.0, 8.0);
	protected static final VoxelShape field_11567 = Block.createCubeShape(8.0, 8.0, 8.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape[] field_11566 = method_10672(field_11562, field_11561, field_11575, field_11578, field_11569);
	protected static final VoxelShape[] field_11564 = method_10672(field_11576, field_11568, field_11577, field_11563, field_11567);
	private static final int[] field_11570 = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
	private final Block baseBlock;
	private final BlockState baseBlockState;

	private static VoxelShape[] method_10672(VoxelShape voxelShape, VoxelShape voxelShape2, VoxelShape voxelShape3, VoxelShape voxelShape4, VoxelShape voxelShape5) {
		return (VoxelShape[])IntStream.range(0, 16)
			.mapToObj(i -> method_10671(i, voxelShape, voxelShape2, voxelShape3, voxelShape4, voxelShape5))
			.toArray(VoxelShape[]::new);
	}

	private static VoxelShape method_10671(
		int i, VoxelShape voxelShape, VoxelShape voxelShape2, VoxelShape voxelShape3, VoxelShape voxelShape4, VoxelShape voxelShape5
	) {
		VoxelShape voxelShape6 = voxelShape;
		if ((i & 1) != 0) {
			voxelShape6 = VoxelShapes.method_1084(voxelShape, voxelShape2);
		}

		if ((i & 2) != 0) {
			voxelShape6 = VoxelShapes.method_1084(voxelShape6, voxelShape3);
		}

		if ((i & 4) != 0) {
			voxelShape6 = VoxelShapes.method_1084(voxelShape6, voxelShape4);
		}

		if ((i & 8) != 0) {
			voxelShape6 = VoxelShapes.method_1084(voxelShape6, voxelShape5);
		}

		return voxelShape6;
	}

	protected StairsBlock(BlockState blockState, Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(field_11571, Direction.NORTH)
				.with(field_11572, class_2760.BOTTOM)
				.with(field_11565, StairShape.field_12710)
				.with(field_11573, Boolean.valueOf(false))
		);
		this.baseBlock = blockState.getBlock();
		this.baseBlockState = blockState;
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return true;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return (blockState.get(field_11572) == class_2760.TOP ? field_11566 : field_11564)[field_11570[this.method_10673(blockState)]];
	}

	private int method_10673(BlockState blockState) {
		return ((StairShape)blockState.get(field_11565)).ordinal() * 4 + ((Direction)blockState.get(field_11571)).getHorizontal();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		this.baseBlock.randomDisplayTick(blockState, world, blockPos, random);
	}

	@Override
	public void onBlockBreakStart(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		this.baseBlockState.onBlockBreakStart(world, blockPos, playerEntity);
	}

	@Override
	public void onBroken(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		this.baseBlock.onBroken(iWorld, blockPos, blockState);
	}

	@Override
	public float getBlastResistance() {
		return this.baseBlock.getBlastResistance();
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return this.baseBlock.getRenderLayer();
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return this.baseBlock.getTickRate(viewableWorld);
	}

	@Override
	public boolean canCollideWith() {
		return this.baseBlock.canCollideWith();
	}

	@Override
	public boolean canCollideWith(BlockState blockState) {
		return this.baseBlock.canCollideWith(blockState);
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (blockState.getBlock() != blockState.getBlock()) {
			this.baseBlockState.neighborUpdate(world, blockPos, Blocks.field_10124, blockPos);
			this.baseBlock.onBlockAdded(this.baseBlockState, world, blockPos, blockState2);
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			this.baseBlockState.onBlockRemoved(world, blockPos, blockState2, bl);
		}
	}

	@Override
	public void onSteppedOn(World world, BlockPos blockPos, Entity entity) {
		this.baseBlock.onSteppedOn(world, blockPos, entity);
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		this.baseBlock.scheduledTick(blockState, world, blockPos, random);
	}

	@Override
	public boolean method_9534(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		return this.baseBlockState.method_11629(world, blockPos, playerEntity, hand, Direction.DOWN, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos blockPos, Explosion explosion) {
		this.baseBlock.onDestroyedByExplosion(world, blockPos, explosion);
	}

	@Override
	public boolean hasSolidTopSurface(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.get(field_11572) == class_2760.TOP;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		Direction direction = itemPlacementContext.method_8038();
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getPos());
		BlockState blockState = this.getDefaultState()
			.with(field_11571, itemPlacementContext.method_8042())
			.with(
				field_11572,
				direction != Direction.DOWN && (direction == Direction.UP || !((double)itemPlacementContext.getHitY() > 0.5)) ? class_2760.BOTTOM : class_2760.TOP
			)
			.with(field_11573, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
		return blockState.with(field_11565, method_10675(blockState, itemPlacementContext.getWorld(), itemPlacementContext.getPos()));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.get(field_11573)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.method_15789(iWorld));
		}

		return direction.getAxis().isHorizontal()
			? blockState.with(field_11565, method_10675(blockState, iWorld, blockPos))
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	private static StairShape method_10675(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		Direction direction = blockState.get(field_11571);
		BlockState blockState2 = blockView.getBlockState(blockPos.method_10093(direction));
		if (method_10676(blockState2) && blockState.get(field_11572) == blockState2.get(field_11572)) {
			Direction direction2 = blockState2.get(field_11571);
			if (direction2.getAxis() != ((Direction)blockState.get(field_11571)).getAxis() && method_10678(blockState, blockView, blockPos, direction2.getOpposite())) {
				if (direction2 == direction.rotateYCounterclockwise()) {
					return StairShape.field_12708;
				}

				return StairShape.field_12709;
			}
		}

		BlockState blockState3 = blockView.getBlockState(blockPos.method_10093(direction.getOpposite()));
		if (method_10676(blockState3) && blockState.get(field_11572) == blockState3.get(field_11572)) {
			Direction direction3 = blockState3.get(field_11571);
			if (direction3.getAxis() != ((Direction)blockState.get(field_11571)).getAxis() && method_10678(blockState, blockView, blockPos, direction3)) {
				if (direction3 == direction.rotateYCounterclockwise()) {
					return StairShape.field_12712;
				}

				return StairShape.field_12713;
			}
		}

		return StairShape.field_12710;
	}

	private static boolean method_10678(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockState blockState2 = blockView.getBlockState(blockPos.method_10093(direction));
		return !method_10676(blockState2)
			|| blockState2.get(field_11571) != blockState.get(field_11571)
			|| blockState2.get(field_11572) != blockState.get(field_11572);
	}

	public static boolean method_10676(BlockState blockState) {
		return blockState.getBlock() instanceof StairsBlock;
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_11571, rotation.method_10503(blockState.get(field_11571)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		Direction direction = blockState.get(field_11571);
		StairShape stairShape = blockState.get(field_11565);
		switch (mirror) {
			case LEFT_RIGHT:
				if (direction.getAxis() == Direction.Axis.Z) {
					switch (stairShape) {
						case field_12712:
							return blockState.applyRotation(Rotation.ROT_180).with(field_11565, StairShape.field_12713);
						case field_12713:
							return blockState.applyRotation(Rotation.ROT_180).with(field_11565, StairShape.field_12712);
						case field_12708:
							return blockState.applyRotation(Rotation.ROT_180).with(field_11565, StairShape.field_12709);
						case field_12709:
							return blockState.applyRotation(Rotation.ROT_180).with(field_11565, StairShape.field_12708);
						default:
							return blockState.applyRotation(Rotation.ROT_180);
					}
				}
				break;
			case FRONT_BACK:
				if (direction.getAxis() == Direction.Axis.X) {
					switch (stairShape) {
						case field_12712:
							return blockState.applyRotation(Rotation.ROT_180).with(field_11565, StairShape.field_12712);
						case field_12713:
							return blockState.applyRotation(Rotation.ROT_180).with(field_11565, StairShape.field_12713);
						case field_12708:
							return blockState.applyRotation(Rotation.ROT_180).with(field_11565, StairShape.field_12709);
						case field_12709:
							return blockState.applyRotation(Rotation.ROT_180).with(field_11565, StairShape.field_12708);
						case field_12710:
							return blockState.applyRotation(Rotation.ROT_180);
					}
				}
		}

		return super.applyMirror(blockState, mirror);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11571, field_11572, field_11565, field_11573);
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.get(field_11573) ? Fluids.WATER.getState(false) : super.getFluidState(blockState);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		return false;
	}
}
