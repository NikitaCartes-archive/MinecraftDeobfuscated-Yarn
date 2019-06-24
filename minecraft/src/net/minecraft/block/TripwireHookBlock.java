package net.minecraft.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class TripwireHookBlock extends Block {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final BooleanProperty ATTACHED = Properties.ATTACHED;
	protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
	protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
	protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
	protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);

	public TripwireHookBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, Boolean.valueOf(false)).with(ATTACHED, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		switch ((Direction)blockState.get(FACING)) {
			case EAST:
			default:
				return WEST_SHAPE;
			case WEST:
				return EAST_SHAPE;
			case SOUTH:
				return NORTH_SHAPE;
			case NORTH:
				return SOUTH_SHAPE;
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Direction direction = blockState.get(FACING);
		BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		return direction.getAxis().isHorizontal() && Block.isSolidFullSquare(blockState2, viewableWorld, blockPos2, direction) && !blockState2.emitsRedstonePower();
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return direction.getOpposite() == blockState.get(FACING) && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.getDefaultState().with(POWERED, Boolean.valueOf(false)).with(ATTACHED, Boolean.valueOf(false));
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
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		this.update(world, blockPos, blockState, false, false, -1, null);
	}

	public void update(World world, BlockPos blockPos, BlockState blockState, boolean bl, boolean bl2, int i, @Nullable BlockState blockState2) {
		Direction direction = blockState.get(FACING);
		boolean bl3 = (Boolean)blockState.get(ATTACHED);
		boolean bl4 = (Boolean)blockState.get(POWERED);
		boolean bl5 = !bl;
		boolean bl6 = false;
		int j = 0;
		BlockState[] blockStates = new BlockState[42];

		for (int k = 1; k < 42; k++) {
			BlockPos blockPos2 = blockPos.offset(direction, k);
			BlockState blockState3 = world.getBlockState(blockPos2);
			if (blockState3.getBlock() == Blocks.TRIPWIRE_HOOK) {
				if (blockState3.get(FACING) == direction.getOpposite()) {
					j = k;
				}
				break;
			}

			if (blockState3.getBlock() != Blocks.TRIPWIRE && k != i) {
				blockStates[k] = null;
				bl5 = false;
			} else {
				if (k == i) {
					blockState3 = MoreObjects.firstNonNull(blockState2, blockState3);
				}

				boolean bl7 = !(Boolean)blockState3.get(TripwireBlock.DISARMED);
				boolean bl8 = (Boolean)blockState3.get(TripwireBlock.POWERED);
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
		BlockState blockState4 = this.getDefaultState().with(ATTACHED, Boolean.valueOf(bl5)).with(POWERED, Boolean.valueOf(bl6));
		if (j > 0) {
			BlockPos blockPos2x = blockPos.offset(direction, j);
			Direction direction2 = direction.getOpposite();
			world.setBlockState(blockPos2x, blockState4.with(FACING, direction2), 3);
			this.updateNeighborsOnAxis(world, blockPos2x, direction2);
			this.playSound(world, blockPos2x, bl5, bl6, bl3, bl4);
		}

		this.playSound(world, blockPos, bl5, bl6, bl3, bl4);
		if (!bl) {
			world.setBlockState(blockPos, blockState4.with(FACING, direction), 3);
			if (bl2) {
				this.updateNeighborsOnAxis(world, blockPos, direction);
			}
		}

		if (bl3 != bl5) {
			for (int l = 1; l < j; l++) {
				BlockPos blockPos3 = blockPos.offset(direction, l);
				BlockState blockState5 = blockStates[l];
				if (blockState5 != null) {
					world.setBlockState(blockPos3, blockState5.with(ATTACHED, Boolean.valueOf(bl5)), 3);
					if (!world.getBlockState(blockPos3).isAir()) {
					}
				}
			}
		}
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		this.update(world, blockPos, blockState, false, true, -1, null);
	}

	private void playSound(World world, BlockPos blockPos, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		if (bl2 && !bl4) {
			world.playSound(null, blockPos, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4F, 0.6F);
		} else if (!bl2 && bl4) {
			world.playSound(null, blockPos, SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4F, 0.5F);
		} else if (bl && !bl3) {
			world.playSound(null, blockPos, SoundEvents.BLOCK_TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4F, 0.7F);
		} else if (!bl && bl3) {
			world.playSound(null, blockPos, SoundEvents.BLOCK_TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F));
		}
	}

	private void updateNeighborsOnAxis(World world, BlockPos blockPos, Direction direction) {
		world.updateNeighborsAlways(blockPos, this);
		world.updateNeighborsAlways(blockPos.offset(direction.getOpposite()), this);
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			boolean bl2 = (Boolean)blockState.get(ATTACHED);
			boolean bl3 = (Boolean)blockState.get(POWERED);
			if (bl2 || bl3) {
				this.update(world, blockPos, blockState, true, false, -1, null);
			}

			if (bl3) {
				world.updateNeighborsAlways(blockPos, this);
				world.updateNeighborsAlways(blockPos.offset(((Direction)blockState.get(FACING)).getOpposite()), this);
			}

			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.get(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		if (!(Boolean)blockState.get(POWERED)) {
			return 0;
		} else {
			return blockState.get(FACING) == direction ? 15 : 0;
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
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
		builder.add(FACING, POWERED, ATTACHED);
	}
}
