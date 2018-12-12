package net.minecraft.block;

import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class TripwireBlock extends Block {
	public static final BooleanProperty field_11680 = Properties.POWERED;
	public static final BooleanProperty field_11683 = Properties.ATTACHED;
	public static final BooleanProperty field_11679 = Properties.DISARMED;
	public static final BooleanProperty field_11675 = ConnectedPlantBlock.NORTH;
	public static final BooleanProperty field_11673 = ConnectedPlantBlock.EAST;
	public static final BooleanProperty field_11678 = ConnectedPlantBlock.SOUTH;
	public static final BooleanProperty field_11674 = ConnectedPlantBlock.WEST;
	private static final Map<Direction, BooleanProperty> field_11676 = HorizontalConnectedBlock.FACING_PROPERTIES;
	protected static final VoxelShape field_11682 = Block.createCubeShape(0.0, 1.0, 0.0, 16.0, 2.5, 16.0);
	protected static final VoxelShape field_11681 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	private final TripwireHookBlock field_11677;

	public TripwireBlock(TripwireHookBlock tripwireHookBlock, Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(field_11680, Boolean.valueOf(false))
				.with(field_11683, Boolean.valueOf(false))
				.with(field_11679, Boolean.valueOf(false))
				.with(field_11675, Boolean.valueOf(false))
				.with(field_11673, Boolean.valueOf(false))
				.with(field_11678, Boolean.valueOf(false))
				.with(field_11674, Boolean.valueOf(false))
		);
		this.field_11677 = tripwireHookBlock;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.get(field_11683) ? field_11682 : field_11681;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getPos();
		return this.getDefaultState()
			.with(field_11675, Boolean.valueOf(this.method_10778(blockView.getBlockState(blockPos.north()), Direction.NORTH)))
			.with(field_11673, Boolean.valueOf(this.method_10778(blockView.getBlockState(blockPos.east()), Direction.EAST)))
			.with(field_11678, Boolean.valueOf(this.method_10778(blockView.getBlockState(blockPos.south()), Direction.SOUTH)))
			.with(field_11674, Boolean.valueOf(this.method_10778(blockView.getBlockState(blockPos.west()), Direction.WEST)));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return direction.getAxis().isHorizontal()
			? blockState.with((Property)field_11676.get(direction), Boolean.valueOf(this.method_10778(blockState2, direction)))
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			this.method_10779(world, blockPos, blockState);
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			this.method_10779(world, blockPos, blockState.with(field_11680, Boolean.valueOf(true)));
		}
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		if (!world.isClient && !playerEntity.getMainHandStack().isEmpty() && playerEntity.getMainHandStack().getItem() == Items.field_8868) {
			world.setBlockState(blockPos, blockState.with(field_11679, Boolean.valueOf(true)), 4);
		}

		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	private void method_10779(World world, BlockPos blockPos, BlockState blockState) {
		for (Direction direction : new Direction[]{Direction.SOUTH, Direction.WEST}) {
			for (int i = 1; i < 42; i++) {
				BlockPos blockPos2 = blockPos.offset(direction, i);
				BlockState blockState2 = world.getBlockState(blockPos2);
				if (blockState2.getBlock() == this.field_11677) {
					if (blockState2.get(TripwireHookBlock.FACING) == direction.getOpposite()) {
						this.field_11677.method_10776(world, blockPos2, blockState2, false, true, i, blockState);
					}
					break;
				}

				if (blockState2.getBlock() != this) {
					break;
				}
			}
		}
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!world.isClient) {
			if (!(Boolean)blockState.get(field_11680)) {
				this.method_10780(world, blockPos);
			}
		}
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			if ((Boolean)world.getBlockState(blockPos).get(field_11680)) {
				this.method_10780(world, blockPos);
			}
		}
	}

	private void method_10780(World world, BlockPos blockPos) {
		BlockState blockState = world.getBlockState(blockPos);
		boolean bl = (Boolean)blockState.get(field_11680);
		boolean bl2 = false;
		List<? extends Entity> list = world.getVisibleEntities(null, blockState.getBoundingShape(world, blockPos).getBoundingBox().offset(blockPos));
		if (!list.isEmpty()) {
			for (Entity entity : list) {
				if (!entity.canAvoidTraps()) {
					bl2 = true;
					break;
				}
			}
		}

		if (bl2 != bl) {
			blockState = blockState.with(field_11680, Boolean.valueOf(bl2));
			world.setBlockState(blockPos, blockState, 3);
			this.method_10779(world, blockPos, blockState);
		}

		if (bl2) {
			world.getBlockTickScheduler().schedule(new BlockPos(blockPos), this, this.getTickRate(world));
		}
	}

	public boolean method_10778(BlockState blockState, Direction direction) {
		Block block = blockState.getBlock();
		return block == this.field_11677 ? blockState.get(TripwireHookBlock.FACING) == direction.getOpposite() : block == this;
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		switch (rotation) {
			case ROT_180:
				return blockState.with(field_11675, blockState.get(field_11678))
					.with(field_11673, blockState.get(field_11674))
					.with(field_11678, blockState.get(field_11675))
					.with(field_11674, blockState.get(field_11673));
			case ROT_270:
				return blockState.with(field_11675, blockState.get(field_11673))
					.with(field_11673, blockState.get(field_11678))
					.with(field_11678, blockState.get(field_11674))
					.with(field_11674, blockState.get(field_11675));
			case ROT_90:
				return blockState.with(field_11675, blockState.get(field_11674))
					.with(field_11673, blockState.get(field_11675))
					.with(field_11678, blockState.get(field_11673))
					.with(field_11674, blockState.get(field_11678));
			default:
				return blockState;
		}
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return blockState.with(field_11675, blockState.get(field_11678)).with(field_11678, blockState.get(field_11675));
			case FRONT_BACK:
				return blockState.with(field_11673, blockState.get(field_11674)).with(field_11674, blockState.get(field_11673));
			default:
				return super.applyMirror(blockState, mirror);
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11680, field_11683, field_11679, field_11675, field_11673, field_11674, field_11678);
	}
}
