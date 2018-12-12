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
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PistonBlock extends FacingBlock {
	public static final BooleanProperty field_12191 = Properties.EXTENDED;
	protected static final VoxelShape field_12188 = Block.createCubeShape(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
	protected static final VoxelShape field_12184 = Block.createCubeShape(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_12186 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
	protected static final VoxelShape field_12189 = Block.createCubeShape(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_12185 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
	protected static final VoxelShape field_12190 = Block.createCubeShape(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);
	private final boolean isSticky;

	public PistonBlock(boolean bl, Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_10927, Direction.NORTH).with(field_12191, Boolean.valueOf(false)));
		this.isSticky = bl;
	}

	@Override
	public boolean canSuffocate(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return !(Boolean)blockState.get(field_12191);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		if ((Boolean)blockState.get(field_12191)) {
			switch ((Direction)blockState.get(field_10927)) {
				case DOWN:
					return field_12190;
				case UP:
				default:
					return field_12185;
				case NORTH:
					return field_12189;
				case SOUTH:
					return field_12186;
				case WEST:
					return field_12184;
				case EAST:
					return field_12188;
			}
		} else {
			return VoxelShapes.fullCube();
		}
	}

	@Override
	public boolean hasSolidTopSurface(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return !(Boolean)blockState.get(field_12191) || blockState.get(field_10927) == Direction.DOWN;
	}

	@Override
	public boolean isSimpleFullBlock(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (!world.isClient) {
			this.method_11483(world, blockPos, blockState);
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (!world.isClient) {
			this.method_11483(world, blockPos, blockState);
		}
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			if (!world.isClient && world.getBlockEntity(blockPos) == null) {
				this.method_11483(world, blockPos, blockState);
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(field_10927, itemPlacementContext.getPlayerFacing().getOpposite()).with(field_12191, Boolean.valueOf(false));
	}

	private void method_11483(World world, BlockPos blockPos, BlockState blockState) {
		Direction direction = blockState.get(field_10927);
		boolean bl = this.method_11482(world, blockPos, direction);
		if (bl && !(Boolean)blockState.get(field_12191)) {
			if (new PistonHandler(world, blockPos, direction, true).calculatePush()) {
				world.addBlockAction(blockPos, this, 0, direction.getId());
			}
		} else if (!bl && (Boolean)blockState.get(field_12191)) {
			BlockPos blockPos2 = blockPos.offset(direction, 2);
			BlockState blockState2 = world.getBlockState(blockPos2);
			int i = 1;
			if (blockState2.getBlock() == Blocks.field_10008 && blockState2.get(field_10927) == direction) {
				BlockEntity blockEntity = world.getBlockEntity(blockPos2);
				if (blockEntity instanceof PistonBlockEntity) {
					PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
					if (pistonBlockEntity.isExtending()
						&& (pistonBlockEntity.getProgress(0.0F) < 0.5F || world.getTime() == pistonBlockEntity.getSavedWorldTime() || ((ServerWorld)world).method_14177())) {
						i = 2;
					}
				}
			}

			world.addBlockAction(blockPos, this, i, direction.getId());
		}
	}

	private boolean method_11482(World world, BlockPos blockPos, Direction direction) {
		for (Direction direction2 : Direction.values()) {
			if (direction2 != direction && world.isEmittingRedstonePower(blockPos.offset(direction2), direction2)) {
				return true;
			}
		}

		if (world.isEmittingRedstonePower(blockPos, Direction.DOWN)) {
			return true;
		} else {
			BlockPos blockPos2 = blockPos.up();

			for (Direction direction3 : Direction.values()) {
				if (direction3 != Direction.DOWN && world.isEmittingRedstonePower(blockPos2.offset(direction3), direction3)) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public boolean onBlockAction(BlockState blockState, World world, BlockPos blockPos, int i, int j) {
		Direction direction = blockState.get(field_10927);
		if (!world.isClient) {
			boolean bl = this.method_11482(world, blockPos, direction);
			if (bl && (i == 1 || i == 2)) {
				world.setBlockState(blockPos, blockState.with(field_12191, Boolean.valueOf(true)), 2);
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

			world.setBlockState(blockPos, blockState.with(field_12191, Boolean.valueOf(true)), 67);
			world.playSound(null, blockPos, SoundEvents.field_15134, SoundCategory.field_15245, 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
		} else if (i == 1 || i == 2) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos.offset(direction));
			if (blockEntity instanceof PistonBlockEntity) {
				((PistonBlockEntity)blockEntity).method_11513();
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
				blockPos, PistonExtensionBlock.createBlockEntityPiston(this.getDefaultState().with(field_10927, Direction.byId(j & 7)), direction, false, true)
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
							pistonBlockEntity.method_11513();
							bl2 = true;
						}
					}
				}

				if (!bl2) {
					if (i != 1
						|| blockState2.isAir()
						|| !isMovable(blockState2, world, blockPos2, direction.getOpposite(), false, direction)
						|| blockState2.getPistonBehavior() != PistonBehavior.field_15974 && block != Blocks.field_10560 && block != Blocks.field_10615) {
						world.clearBlockState(blockPos.offset(direction));
					} else {
						this.move(world, blockPos, direction, false);
					}
				}
			} else {
				world.clearBlockState(blockPos.offset(direction));
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
		} else if (blockPos.getY() >= 0 && (direction != Direction.DOWN || blockPos.getY() != 0)) {
			if (blockPos.getY() <= world.getHeight() - 1 && (direction != Direction.UP || blockPos.getY() != world.getHeight() - 1)) {
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
				} else if ((Boolean)blockState.get(field_12191)) {
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
				world.setBlockState(blockPos4, Blocks.field_10008.getDefaultState().with(field_10927, direction), 68);
				world.setBlockEntity(blockPos4, PistonExtensionBlock.createBlockEntityPiston((BlockState)list2.get(k), direction, bl, false));
				j--;
				blockStates[j] = blockState;
			}

			if (bl) {
				PistonType pistonType = this.isSticky ? PistonType.field_12634 : PistonType.field_12637;
				BlockState blockState2 = Blocks.field_10379.getDefaultState().with(PistonHeadBlock.field_10927, direction).with(PistonHeadBlock.field_12224, pistonType);
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
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_10927, rotation.method_10503(blockState.get(field_10927)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.applyRotation(mirror.getRotation(blockState.get(field_10927)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_10927, field_12191);
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return (Boolean)blockState.get(field_12191);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		return false;
	}
}
