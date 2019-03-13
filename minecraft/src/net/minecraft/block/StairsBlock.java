package net.minecraft.block;

import java.util.Random;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
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
import net.minecraft.util.hit.BlockHitResult;
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
	public static final EnumProperty<BlockHalf> field_11572 = Properties.field_12518;
	public static final EnumProperty<StairShape> field_11565 = Properties.field_12503;
	public static final BooleanProperty field_11573 = Properties.field_12508;
	protected static final VoxelShape field_11562 = SlabBlock.field_11499;
	protected static final VoxelShape field_11576 = SlabBlock.field_11500;
	protected static final VoxelShape field_11561 = Block.method_9541(0.0, 0.0, 0.0, 8.0, 8.0, 8.0);
	protected static final VoxelShape field_11578 = Block.method_9541(0.0, 0.0, 8.0, 8.0, 8.0, 16.0);
	protected static final VoxelShape field_11568 = Block.method_9541(0.0, 8.0, 0.0, 8.0, 16.0, 8.0);
	protected static final VoxelShape field_11563 = Block.method_9541(0.0, 8.0, 8.0, 8.0, 16.0, 16.0);
	protected static final VoxelShape field_11575 = Block.method_9541(8.0, 0.0, 0.0, 16.0, 8.0, 8.0);
	protected static final VoxelShape field_11569 = Block.method_9541(8.0, 0.0, 8.0, 16.0, 8.0, 16.0);
	protected static final VoxelShape field_11577 = Block.method_9541(8.0, 8.0, 0.0, 16.0, 16.0, 8.0);
	protected static final VoxelShape field_11567 = Block.method_9541(8.0, 8.0, 8.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape[] field_11566 = method_10672(field_11562, field_11561, field_11575, field_11578, field_11569);
	protected static final VoxelShape[] field_11564 = method_10672(field_11576, field_11568, field_11577, field_11563, field_11567);
	private static final int[] field_11570 = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
	private final Block baseBlock;
	private final BlockState field_11574;

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
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11571, Direction.NORTH)
				.method_11657(field_11572, BlockHalf.BOTTOM)
				.method_11657(field_11565, StairShape.field_12710)
				.method_11657(field_11573, Boolean.valueOf(false))
		);
		this.baseBlock = blockState.getBlock();
		this.field_11574 = blockState;
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return true;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return (blockState.method_11654(field_11572) == BlockHalf.TOP ? field_11566 : field_11564)[field_11570[this.method_10673(blockState)]];
	}

	private int method_10673(BlockState blockState) {
		return ((StairShape)blockState.method_11654(field_11565)).ordinal() * 4 + ((Direction)blockState.method_11654(field_11571)).getHorizontal();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		this.baseBlock.method_9496(blockState, world, blockPos, random);
	}

	@Override
	public void method_9606(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		this.field_11574.method_11636(world, blockPos, playerEntity);
	}

	@Override
	public void method_9585(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		this.baseBlock.method_9585(iWorld, blockPos, blockState);
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
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (blockState.getBlock() != blockState.getBlock()) {
			this.field_11574.method_11622(world, blockPos, Blocks.field_10124, blockPos);
			this.baseBlock.method_9615(this.field_11574, world, blockPos, blockState2);
		}
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			this.field_11574.method_11600(world, blockPos, blockState2, bl);
		}
	}

	@Override
	public void method_9591(World world, BlockPos blockPos, Entity entity) {
		this.baseBlock.method_9591(world, blockPos, entity);
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		this.baseBlock.method_9588(blockState, world, blockPos, random);
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		return this.field_11574.method_11629(world, playerEntity, hand, blockHitResult);
	}

	@Override
	public void method_9586(World world, BlockPos blockPos, Explosion explosion) {
		this.baseBlock.method_9586(world, blockPos, explosion);
	}

	@Override
	public boolean method_9561(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.method_11654(field_11572) == BlockHalf.TOP;
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		Direction direction = itemPlacementContext.method_8038();
		BlockPos blockPos = itemPlacementContext.method_8037();
		FluidState fluidState = itemPlacementContext.method_8045().method_8316(blockPos);
		BlockState blockState = this.method_9564()
			.method_11657(field_11571, itemPlacementContext.method_8042())
			.method_11657(
				field_11572,
				direction != Direction.DOWN && (direction == Direction.UP || !(itemPlacementContext.method_17698().y - (double)blockPos.getY() > 0.5))
					? BlockHalf.BOTTOM
					: BlockHalf.TOP
			)
			.method_11657(field_11573, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
		return blockState.method_11657(field_11565, method_10675(blockState, itemPlacementContext.method_8045(), blockPos));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.method_11654(field_11573)) {
			iWorld.method_8405().method_8676(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return direction.getAxis().isHorizontal()
			? blockState.method_11657(field_11565, method_10675(blockState, iWorld, blockPos))
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	private static StairShape method_10675(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		Direction direction = blockState.method_11654(field_11571);
		BlockState blockState2 = blockView.method_8320(blockPos.method_10093(direction));
		if (method_10676(blockState2) && blockState.method_11654(field_11572) == blockState2.method_11654(field_11572)) {
			Direction direction2 = blockState2.method_11654(field_11571);
			if (direction2.getAxis() != ((Direction)blockState.method_11654(field_11571)).getAxis()
				&& method_10678(blockState, blockView, blockPos, direction2.getOpposite())) {
				if (direction2 == direction.rotateYCounterclockwise()) {
					return StairShape.field_12708;
				}

				return StairShape.field_12709;
			}
		}

		BlockState blockState3 = blockView.method_8320(blockPos.method_10093(direction.getOpposite()));
		if (method_10676(blockState3) && blockState.method_11654(field_11572) == blockState3.method_11654(field_11572)) {
			Direction direction3 = blockState3.method_11654(field_11571);
			if (direction3.getAxis() != ((Direction)blockState.method_11654(field_11571)).getAxis() && method_10678(blockState, blockView, blockPos, direction3)) {
				if (direction3 == direction.rotateYCounterclockwise()) {
					return StairShape.field_12712;
				}

				return StairShape.field_12713;
			}
		}

		return StairShape.field_12710;
	}

	private static boolean method_10678(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockState blockState2 = blockView.method_8320(blockPos.method_10093(direction));
		return !method_10676(blockState2)
			|| blockState2.method_11654(field_11571) != blockState.method_11654(field_11571)
			|| blockState2.method_11654(field_11572) != blockState.method_11654(field_11572);
	}

	public static boolean method_10676(BlockState blockState) {
		return blockState.getBlock() instanceof StairsBlock;
	}

	@Override
	public BlockState method_9598(BlockState blockState, Rotation rotation) {
		return blockState.method_11657(field_11571, rotation.method_10503(blockState.method_11654(field_11571)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, Mirror mirror) {
		Direction direction = blockState.method_11654(field_11571);
		StairShape stairShape = blockState.method_11654(field_11565);
		switch (mirror) {
			case LEFT_RIGHT:
				if (direction.getAxis() == Direction.Axis.Z) {
					switch (stairShape) {
						case field_12712:
							return blockState.rotate(Rotation.ROT_180).method_11657(field_11565, StairShape.field_12713);
						case field_12713:
							return blockState.rotate(Rotation.ROT_180).method_11657(field_11565, StairShape.field_12712);
						case field_12708:
							return blockState.rotate(Rotation.ROT_180).method_11657(field_11565, StairShape.field_12709);
						case field_12709:
							return blockState.rotate(Rotation.ROT_180).method_11657(field_11565, StairShape.field_12708);
						default:
							return blockState.rotate(Rotation.ROT_180);
					}
				}
				break;
			case FRONT_BACK:
				if (direction.getAxis() == Direction.Axis.X) {
					switch (stairShape) {
						case field_12712:
							return blockState.rotate(Rotation.ROT_180).method_11657(field_11565, StairShape.field_12712);
						case field_12713:
							return blockState.rotate(Rotation.ROT_180).method_11657(field_11565, StairShape.field_12713);
						case field_12708:
							return blockState.rotate(Rotation.ROT_180).method_11657(field_11565, StairShape.field_12709);
						case field_12709:
							return blockState.rotate(Rotation.ROT_180).method_11657(field_11565, StairShape.field_12708);
						case field_12710:
							return blockState.rotate(Rotation.ROT_180);
					}
				}
		}

		return super.method_9569(blockState, mirror);
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11571, field_11572, field_11565, field_11573);
	}

	@Override
	public FluidState method_9545(BlockState blockState) {
		return blockState.method_11654(field_11573) ? Fluids.WATER.method_15729(false) : super.method_9545(blockState);
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
