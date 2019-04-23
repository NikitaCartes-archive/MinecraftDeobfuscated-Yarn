package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PistonBlock extends FacingBlock {
	public static final BooleanProperty EXTENDED = Properties.EXTENDED;
	protected static final VoxelShape EXTENDED_EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
	protected static final VoxelShape EXTENDED_WEST_SHAPE = Block.createCuboidShape(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape EXTENDED_SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
	protected static final VoxelShape EXTENDED_NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape EXTENDED_UP_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
	protected static final VoxelShape EXTENDED_DOWN_SHAPE = Block.createCuboidShape(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);
	private final boolean isSticky;

	public PistonBlock(boolean bl, Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.field_11043).with(EXTENDED, Boolean.valueOf(false)));
		this.isSticky = bl;
	}

	@Override
	public boolean canSuffocate(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return !(Boolean)blockState.get(EXTENDED);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		if ((Boolean)blockState.get(EXTENDED)) {
			switch ((Direction)blockState.get(FACING)) {
				case field_11033:
					return EXTENDED_DOWN_SHAPE;
				case field_11036:
				default:
					return EXTENDED_UP_SHAPE;
				case field_11043:
					return EXTENDED_NORTH_SHAPE;
				case field_11035:
					return EXTENDED_SOUTH_SHAPE;
				case field_11039:
					return EXTENDED_WEST_SHAPE;
				case field_11034:
					return EXTENDED_EAST_SHAPE;
			}
		} else {
			return VoxelShapes.fullCube();
		}
	}

	@Override
	public boolean isSimpleFullBlock(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (!world.isClient) {
			this.tryMove(world, blockPos, blockState);
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (!world.isClient) {
			this.tryMove(world, blockPos, blockState);
		}
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			if (!world.isClient && world.getBlockEntity(blockPos) == null) {
				this.tryMove(world, blockPos, blockState);
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(FACING, itemPlacementContext.getPlayerFacing().getOpposite()).with(EXTENDED, Boolean.valueOf(false));
	}

	private void tryMove(World world, BlockPos blockPos, BlockState blockState) {
		Direction direction = blockState.get(FACING);
		boolean bl = this.shouldExtend(world, blockPos, direction);
		if (bl && !(Boolean)blockState.get(EXTENDED)) {
			if (new PistonHandler(world, blockPos, direction, true).calculatePush()) {
				world.addBlockAction(blockPos, this, 0, direction.getId());
			}
		} else if (!bl && (Boolean)blockState.get(EXTENDED)) {
			BlockPos blockPos2 = blockPos.offset(direction, 2);
			BlockState blockState2 = world.getBlockState(blockPos2);
			int i = 1;
			if (blockState2.getBlock() == Blocks.field_10008 && blockState2.get(FACING) == direction) {
				BlockEntity blockEntity = world.getBlockEntity(blockPos2);
				if (blockEntity instanceof PistonBlockEntity) {
					PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
					if (pistonBlockEntity.isExtending()
						&& (pistonBlockEntity.getProgress(0.0F) < 0.5F || world.getTime() == pistonBlockEntity.getSavedWorldTime() || ((ServerWorld)world).isInsideTick())) {
						i = 2;
					}
				}
			}

			world.addBlockAction(blockPos, this, i, direction.getId());
		}
	}

	private boolean shouldExtend(World world, BlockPos blockPos, Direction direction) {
		for (Direction direction2 : Direction.values()) {
			if (direction2 != direction && world.isEmittingRedstonePower(blockPos.offset(direction2), direction2)) {
				return true;
			}
		}

		if (world.isEmittingRedstonePower(blockPos, Direction.field_11033)) {
			return true;
		} else {
			BlockPos blockPos2 = blockPos.up();

			for (Direction direction3 : Direction.values()) {
				if (direction3 != Direction.field_11033 && world.isEmittingRedstonePower(blockPos2.offset(direction3), direction3)) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public boolean onBlockAction(BlockState blockState, World world, BlockPos blockPos, int i, int j) {
		Direction direction = blockState.get(FACING);
		if (!world.isClient) {
			boolean bl = this.shouldExtend(world, blockPos, direction);
			if (bl && (i == 1 || i == 2)) {
				world.setBlockState(blockPos, blockState.with(EXTENDED, Boolean.valueOf(true)), 2);
				return false;
			}

			if (!bl && i == 0) {
				return false;
			}
		}

		if (i == 0) {
			if (!this.move(world, blockPos, direction, true)) {
				return false;
			}

			world.setBlockState(blockPos, blockState.with(EXTENDED, Boolean.valueOf(true)), 67);
			world.playSound(null, blockPos, SoundEvents.field_15134, SoundCategory.field_15245, 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
		} else if (i == 1 || i == 2) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos.offset(direction));
			if (blockEntity instanceof PistonBlockEntity) {
				((PistonBlockEntity)blockEntity).finish();
			}

			world.setBlockState(
				blockPos,
				Blocks.field_10008
					.getDefaultState()
					.with(PistonExtensionBlock.FACING, direction)
					.with(PistonExtensionBlock.TYPE, this.isSticky ? PistonType.field_12634 : PistonType.field_12637),
				3
			);
			world.setBlockEntity(
				blockPos, PistonExtensionBlock.createBlockEntityPiston(this.getDefaultState().with(FACING, Direction.byId(j & 7)), direction, false, true)
			);
			if (this.isSticky) {
				BlockPos blockPos2 = blockPos.add(direction.getOffsetX() * 2, direction.getOffsetY() * 2, direction.getOffsetZ() * 2);
				BlockState blockState2 = world.getBlockState(blockPos2);
				Block block = blockState2.getBlock();
				boolean bl2 = false;
				if (block == Blocks.field_10008) {
					BlockEntity blockEntity2 = world.getBlockEntity(blockPos2);
					if (blockEntity2 instanceof PistonBlockEntity) {
						PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity2;
						if (pistonBlockEntity.getFacing() == direction && pistonBlockEntity.isExtending()) {
							pistonBlockEntity.finish();
							bl2 = true;
						}
					}
				}

				if (!bl2) {
					if (i != 1
						|| blockState2.isAir()
						|| !isMovable(blockState2, world, blockPos2, direction.getOpposite(), false, direction)
						|| blockState2.getPistonBehavior() != PistonBehavior.field_15974 && block != Blocks.field_10560 && block != Blocks.field_10615) {
						world.clearBlockState(blockPos.offset(direction), false);
					} else {
						this.move(world, blockPos, direction, false);
					}
				}
			} else {
				world.clearBlockState(blockPos.offset(direction), false);
			}

			world.playSound(null, blockPos, SoundEvents.field_15228, SoundCategory.field_15245, 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
		}

		return true;
	}

	public static boolean isMovable(BlockState blockState, World world, BlockPos blockPos, Direction direction, boolean bl, Direction direction2) {
		Block block = blockState.getBlock();
		if (block == Blocks.field_10540) {
			return false;
		} else if (!world.getWorldBorder().contains(blockPos)) {
			return false;
		} else if (blockPos.getY() >= 0 && (direction != Direction.field_11033 || blockPos.getY() != 0)) {
			if (blockPos.getY() <= world.getHeight() - 1 && (direction != Direction.field_11036 || blockPos.getY() != world.getHeight() - 1)) {
				if (block != Blocks.field_10560 && block != Blocks.field_10615) {
					if (blockState.getHardness(world, blockPos) == -1.0F) {
						return false;
					}

					switch (blockState.getPistonBehavior()) {
						case field_15972:
							return false;
						case field_15971:
							return bl;
						case field_15970:
							return direction == direction2;
					}
				} else if ((Boolean)blockState.get(EXTENDED)) {
					return false;
				}

				return !block.hasBlockEntity();
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean move(World world, BlockPos blockPos, Direction direction, boolean bl) {
		BlockPos blockPos2 = blockPos.offset(direction);
		if (!bl && world.getBlockState(blockPos2).getBlock() == Blocks.field_10379) {
			world.setBlockState(blockPos2, Blocks.field_10124.getDefaultState(), 20);
		}

		PistonHandler pistonHandler = new PistonHandler(world, blockPos, direction, bl);
		if (!pistonHandler.calculatePush()) {
			return false;
		} else {
			List<BlockPos> list = pistonHandler.getMovedBlocks();
			List<BlockState> list2 = Lists.<BlockState>newArrayList();

			for (int i = 0; i < list.size(); i++) {
				BlockPos blockPos3 = (BlockPos)list.get(i);
				list2.add(world.getBlockState(blockPos3));
			}

			List<BlockPos> list3 = pistonHandler.getBrokenBlocks();
			int j = list.size() + list3.size();
			BlockState[] blockStates = new BlockState[j];
			Direction direction2 = bl ? direction : direction.getOpposite();
			Set<BlockPos> set = Sets.<BlockPos>newHashSet(list);

			for (int k = list3.size() - 1; k >= 0; k--) {
				BlockPos blockPos4 = (BlockPos)list3.get(k);
				BlockState blockState = world.getBlockState(blockPos4);
				BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? world.getBlockEntity(blockPos4) : null;
				dropStacks(blockState, world, blockPos4, blockEntity);
				world.setBlockState(blockPos4, Blocks.field_10124.getDefaultState(), 18);
				j--;
				blockStates[j] = blockState;
			}

			for (int k = list.size() - 1; k >= 0; k--) {
				BlockPos blockPos4 = (BlockPos)list.get(k);
				BlockState blockState = world.getBlockState(blockPos4);
				blockPos4 = blockPos4.offset(direction2);
				set.remove(blockPos4);
				world.setBlockState(blockPos4, Blocks.field_10008.getDefaultState().with(FACING, direction), 68);
				world.setBlockEntity(blockPos4, PistonExtensionBlock.createBlockEntityPiston((BlockState)list2.get(k), direction, bl, false));
				j--;
				blockStates[j] = blockState;
			}

			if (bl) {
				PistonType pistonType = this.isSticky ? PistonType.field_12634 : PistonType.field_12637;
				BlockState blockState2 = Blocks.field_10379.getDefaultState().with(PistonHeadBlock.FACING, direction).with(PistonHeadBlock.TYPE, pistonType);
				BlockState blockState = Blocks.field_10008
					.getDefaultState()
					.with(PistonExtensionBlock.FACING, direction)
					.with(PistonExtensionBlock.TYPE, this.isSticky ? PistonType.field_12634 : PistonType.field_12637);
				set.remove(blockPos2);
				world.setBlockState(blockPos2, blockState, 68);
				world.setBlockEntity(blockPos2, PistonExtensionBlock.createBlockEntityPiston(blockState2, direction, true, true));
			}

			for (BlockPos blockPos4 : set) {
				world.setBlockState(blockPos4, Blocks.field_10124.getDefaultState(), 66);
			}

			for (int k = list3.size() - 1; k >= 0; k--) {
				BlockState blockState2 = blockStates[j++];
				BlockPos blockPos5 = (BlockPos)list3.get(k);
				blockState2.method_11637(world, blockPos5, 2);
				world.updateNeighborsAlways(blockPos5, blockState2.getBlock());
			}

			for (int k = list.size() - 1; k >= 0; k--) {
				world.updateNeighborsAlways((BlockPos)list.get(k), blockStates[j++].getBlock());
			}

			if (bl) {
				world.updateNeighborsAlways(blockPos2, Blocks.field_10379);
			}

			return true;
		}
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
		builder.add(FACING, EXTENDED);
	}

	@Override
	public boolean hasSidedTransparency(BlockState blockState) {
		return (Boolean)blockState.get(EXTENDED);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
