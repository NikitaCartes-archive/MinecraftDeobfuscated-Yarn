package net.minecraft.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class TripwireHookBlock extends Block {
	public static final DirectionProperty field_11666 = HorizontalFacingBlock.field_11177;
	public static final BooleanProperty field_11671 = Properties.POWERED;
	public static final BooleanProperty field_11669 = Properties.ATTACHED;
	protected static final VoxelShape field_11665 = Block.createCubeShape(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
	protected static final VoxelShape field_11668 = Block.createCubeShape(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
	protected static final VoxelShape field_11670 = Block.createCubeShape(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
	protected static final VoxelShape field_11667 = Block.createCubeShape(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);

	public TripwireHookBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory.getDefaultState().with(field_11666, Direction.NORTH).with(field_11671, Boolean.valueOf(false)).with(field_11669, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		switch ((Direction)blockState.get(field_11666)) {
			case EAST:
			default:
				return field_11667;
			case WEST:
				return field_11670;
			case SOUTH:
				return field_11668;
			case NORTH:
				return field_11665;
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Direction direction = blockState.get(field_11666);
		BlockPos blockPos2 = blockPos.method_10093(direction.getOpposite());
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		boolean bl = method_9581(blockState2.getBlock());
		return !bl
			&& direction.getAxis().isHorizontal()
			&& Block.method_9501(blockState2.method_11628(viewableWorld, blockPos2), direction)
			&& !blockState2.emitsRedstonePower();
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return direction.getOpposite() == blockState.get(field_11666) && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.getDefaultState().with(field_11671, Boolean.valueOf(false)).with(field_11669, Boolean.valueOf(false));
		ViewableWorld viewableWorld = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getPos();
		Direction[] directions = itemPlacementContext.method_7718();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction2 = direction.getOpposite();
				blockState = blockState.with(field_11666, direction2);
				if (blockState.canPlaceAt(viewableWorld, blockPos)) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		this.method_10776(world, blockPos, blockState, false, false, -1, null);
	}

	public void method_10776(World world, BlockPos blockPos, BlockState blockState, boolean bl, boolean bl2, int i, @Nullable BlockState blockState2) {
		Direction direction = blockState.get(field_11666);
		boolean bl3 = (Boolean)blockState.get(field_11669);
		boolean bl4 = (Boolean)blockState.get(field_11671);
		boolean bl5 = !bl;
		boolean bl6 = false;
		int j = 0;
		BlockState[] blockStates = new BlockState[42];

		for (int k = 1; k < 42; k++) {
			BlockPos blockPos2 = blockPos.method_10079(direction, k);
			BlockState blockState3 = world.getBlockState(blockPos2);
			if (blockState3.getBlock() == Blocks.field_10348) {
				if (blockState3.get(field_11666) == direction.getOpposite()) {
					j = k;
				}
				break;
			}

			if (blockState3.getBlock() != Blocks.field_10589 && k != i) {
				blockStates[k] = null;
				bl5 = false;
			} else {
				if (k == i) {
					blockState3 = MoreObjects.firstNonNull(blockState2, blockState3);
				}

				boolean bl7 = !(Boolean)blockState3.get(TripwireBlock.field_11679);
				boolean bl8 = (Boolean)blockState3.get(TripwireBlock.field_11680);
				bl6 |= bl7 && bl8;
				blockStates[k] = blockState3;
				if (k == i) {
					world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
					bl5 &= bl7;
				}
			}
		}

		bl5 &= j > 1;
		bl6 &= bl5;
		BlockState blockState4 = this.getDefaultState().with(field_11669, Boolean.valueOf(bl5)).with(field_11671, Boolean.valueOf(bl6));
		if (j > 0) {
			BlockPos blockPos2x = blockPos.method_10079(direction, j);
			Direction direction2 = direction.getOpposite();
			world.setBlockState(blockPos2x, blockState4.with(field_11666, direction2), 3);
			this.method_10775(world, blockPos2x, direction2);
			this.method_10777(world, blockPos2x, bl5, bl6, bl3, bl4);
		}

		this.method_10777(world, blockPos, bl5, bl6, bl3, bl4);
		if (!bl) {
			world.setBlockState(blockPos, blockState4.with(field_11666, direction), 3);
			if (bl2) {
				this.method_10775(world, blockPos, direction);
			}
		}

		if (bl3 != bl5) {
			for (int l = 1; l < j; l++) {
				BlockPos blockPos3 = blockPos.method_10079(direction, l);
				BlockState blockState5 = blockStates[l];
				if (blockState5 != null) {
					world.setBlockState(blockPos3, blockState5.with(field_11669, Boolean.valueOf(bl5)), 3);
					if (!world.getBlockState(blockPos3).isAir()) {
					}
				}
			}
		}
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		this.method_10776(world, blockPos, blockState, false, true, -1, null);
	}

	private void method_10777(World world, BlockPos blockPos, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		if (bl2 && !bl4) {
			world.playSound(null, blockPos, SoundEvents.field_14674, SoundCategory.field_15245, 0.4F, 0.6F);
		} else if (!bl2 && bl4) {
			world.playSound(null, blockPos, SoundEvents.field_14787, SoundCategory.field_15245, 0.4F, 0.5F);
		} else if (bl && !bl3) {
			world.playSound(null, blockPos, SoundEvents.field_14859, SoundCategory.field_15245, 0.4F, 0.7F);
		} else if (!bl && bl3) {
			world.playSound(null, blockPos, SoundEvents.field_14595, SoundCategory.field_15245, 0.4F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F));
		}
	}

	private void method_10775(World world, BlockPos blockPos, Direction direction) {
		world.updateNeighborsAlways(blockPos, this);
		world.updateNeighborsAlways(blockPos.method_10093(direction.getOpposite()), this);
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			boolean bl2 = (Boolean)blockState.get(field_11669);
			boolean bl3 = (Boolean)blockState.get(field_11671);
			if (bl2 || bl3) {
				this.method_10776(world, blockPos, blockState, true, false, -1, null);
			}

			if (bl3) {
				world.updateNeighborsAlways(blockPos, this);
				world.updateNeighborsAlways(blockPos.method_10093(((Direction)blockState.get(field_11666)).getOpposite()), this);
			}

			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.get(field_11671) ? 15 : 0;
	}

	@Override
	public int method_9603(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		if (!(Boolean)blockState.get(field_11671)) {
			return 0;
		} else {
			return blockState.get(field_11666) == direction ? 15 : 0;
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.MIPPED_CUTOUT;
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_11666, rotation.method_10503(blockState.get(field_11666)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.applyRotation(mirror.method_10345(blockState.get(field_11666)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11666, field_11671, field_11669);
	}
}
