package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import net.minecraft.state.StateManager;
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

	public PistonBlock(boolean isSticky, Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(EXTENDED, Boolean.valueOf(false)));
		this.isSticky = isSticky;
	}

	@Override
	public boolean canSuffocate(BlockState state, BlockView view, BlockPos pos) {
		return !(Boolean)state.get(EXTENDED);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		if ((Boolean)state.get(EXTENDED)) {
			switch ((Direction)state.get(FACING)) {
				case DOWN:
					return EXTENDED_DOWN_SHAPE;
				case UP:
				default:
					return EXTENDED_UP_SHAPE;
				case NORTH:
					return EXTENDED_NORTH_SHAPE;
				case SOUTH:
					return EXTENDED_SOUTH_SHAPE;
				case WEST:
					return EXTENDED_WEST_SHAPE;
				case EAST:
					return EXTENDED_EAST_SHAPE;
			}
		} else {
			return VoxelShapes.fullCube();
		}
	}

	@Override
	public boolean isSimpleFullBlock(BlockState state, BlockView view, BlockPos pos) {
		return false;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (!world.isClient) {
			this.tryMove(world, pos, state);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		if (!world.isClient) {
			this.tryMove(world, pos, state);
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
		if (oldState.getBlock() != state.getBlock()) {
			if (!world.isClient && world.getBlockEntity(pos) == null) {
				this.tryMove(world, pos, state);
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite()).with(EXTENDED, Boolean.valueOf(false));
	}

	private void tryMove(World world, BlockPos pos, BlockState state) {
		Direction direction = state.get(FACING);
		boolean bl = this.shouldExtend(world, pos, direction);
		if (bl && !(Boolean)state.get(EXTENDED)) {
			if (new PistonHandler(world, pos, direction, true).calculatePush()) {
				world.addBlockAction(pos, this, 0, direction.getId());
			}
		} else if (!bl && (Boolean)state.get(EXTENDED)) {
			BlockPos blockPos = pos.offset(direction, 2);
			BlockState blockState = world.getBlockState(blockPos);
			int i = 1;
			if (blockState.getBlock() == Blocks.MOVING_PISTON && blockState.get(FACING) == direction) {
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity instanceof PistonBlockEntity) {
					PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
					if (pistonBlockEntity.isExtending()
						&& (pistonBlockEntity.getProgress(0.0F) < 0.5F || world.getTime() == pistonBlockEntity.getSavedWorldTime() || ((ServerWorld)world).isInsideTick())) {
						i = 2;
					}
				}
			}

			world.addBlockAction(pos, this, i, direction.getId());
		}
	}

	private boolean shouldExtend(World world, BlockPos pos, Direction pistonFace) {
		for (Direction direction : Direction.values()) {
			if (direction != pistonFace && world.isEmittingRedstonePower(pos.offset(direction), direction)) {
				return true;
			}
		}

		if (world.isEmittingRedstonePower(pos, Direction.DOWN)) {
			return true;
		} else {
			BlockPos blockPos = pos.up();

			for (Direction direction2 : Direction.values()) {
				if (direction2 != Direction.DOWN && world.isEmittingRedstonePower(blockPos.offset(direction2), direction2)) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public boolean onBlockAction(BlockState state, World world, BlockPos pos, int type, int data) {
		Direction direction = state.get(FACING);
		if (!world.isClient) {
			boolean bl = this.shouldExtend(world, pos, direction);
			if (bl && (type == 1 || type == 2)) {
				world.setBlockState(pos, state.with(EXTENDED, Boolean.valueOf(true)), 2);
				return false;
			}

			if (!bl && type == 0) {
				return false;
			}
		}

		if (type == 0) {
			if (!this.move(world, pos, direction, true)) {
				return false;
			}

			world.setBlockState(pos, state.with(EXTENDED, Boolean.valueOf(true)), 67);
			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
		} else if (type == 1 || type == 2) {
			BlockEntity blockEntity = world.getBlockEntity(pos.offset(direction));
			if (blockEntity instanceof PistonBlockEntity) {
				((PistonBlockEntity)blockEntity).finish();
			}

			BlockState blockState = Blocks.MOVING_PISTON
				.getDefaultState()
				.with(PistonExtensionBlock.FACING, direction)
				.with(PistonExtensionBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
			world.setBlockState(pos, blockState, 20);
			world.setBlockEntity(
				pos, PistonExtensionBlock.createBlockEntityPiston(this.getDefaultState().with(FACING, Direction.byId(data & 7)), direction, false, true)
			);
			world.updateNeighbors(pos, blockState.getBlock());
			if (this.isSticky) {
				BlockPos blockPos = pos.add(direction.getOffsetX() * 2, direction.getOffsetY() * 2, direction.getOffsetZ() * 2);
				BlockState blockState2 = world.getBlockState(blockPos);
				Block block = blockState2.getBlock();
				boolean bl2 = false;
				if (block == Blocks.MOVING_PISTON) {
					BlockEntity blockEntity2 = world.getBlockEntity(blockPos);
					if (blockEntity2 instanceof PistonBlockEntity) {
						PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity2;
						if (pistonBlockEntity.getFacing() == direction && pistonBlockEntity.isExtending()) {
							pistonBlockEntity.finish();
							bl2 = true;
						}
					}
				}

				if (!bl2) {
					if (type != 1
						|| blockState2.isAir()
						|| !isMovable(blockState2, world, blockPos, direction.getOpposite(), false, direction)
						|| blockState2.getPistonBehavior() != PistonBehavior.NORMAL && block != Blocks.PISTON && block != Blocks.STICKY_PISTON) {
						world.removeBlock(pos.offset(direction), false);
					} else {
						this.move(world, pos, direction, false);
					}
				}
			} else {
				world.removeBlock(pos.offset(direction), false);
			}

			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
		}

		return true;
	}

	public static boolean isMovable(BlockState state, World world, BlockPos pos, Direction motionDir, boolean canBreak, Direction pistonDir) {
		Block block = state.getBlock();
		if (block == Blocks.OBSIDIAN) {
			return false;
		} else if (!world.getWorldBorder().contains(pos)) {
			return false;
		} else if (pos.getY() >= 0 && (motionDir != Direction.DOWN || pos.getY() != 0)) {
			if (pos.getY() <= world.getHeight() - 1 && (motionDir != Direction.UP || pos.getY() != world.getHeight() - 1)) {
				if (block != Blocks.PISTON && block != Blocks.STICKY_PISTON) {
					if (state.getHardness(world, pos) == -1.0F) {
						return false;
					}

					switch (state.getPistonBehavior()) {
						case BLOCK:
							return false;
						case DESTROY:
							return canBreak;
						case PUSH_ONLY:
							return motionDir == pistonDir;
					}
				} else if ((Boolean)state.get(EXTENDED)) {
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

	private boolean move(World world, BlockPos pos, Direction dir, boolean retract) {
		BlockPos blockPos = pos.offset(dir);
		if (!retract && world.getBlockState(blockPos).getBlock() == Blocks.PISTON_HEAD) {
			world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 20);
		}

		PistonHandler pistonHandler = new PistonHandler(world, pos, dir, retract);
		if (!pistonHandler.calculatePush()) {
			return false;
		} else {
			Map<BlockPos, BlockState> map = Maps.<BlockPos, BlockState>newHashMap();
			List<BlockPos> list = pistonHandler.getMovedBlocks();
			List<BlockState> list2 = Lists.<BlockState>newArrayList();

			for (int i = 0; i < list.size(); i++) {
				BlockPos blockPos2 = (BlockPos)list.get(i);
				BlockState blockState = world.getBlockState(blockPos2);
				list2.add(blockState);
				map.put(blockPos2, blockState);
			}

			List<BlockPos> list3 = pistonHandler.getBrokenBlocks();
			int j = list.size() + list3.size();
			BlockState[] blockStates = new BlockState[j];
			Direction direction = retract ? dir : dir.getOpposite();

			for (int k = list3.size() - 1; k >= 0; k--) {
				BlockPos blockPos3 = (BlockPos)list3.get(k);
				BlockState blockState2 = world.getBlockState(blockPos3);
				BlockEntity blockEntity = blockState2.getBlock().hasBlockEntity() ? world.getBlockEntity(blockPos3) : null;
				dropStacks(blockState2, world, blockPos3, blockEntity);
				world.setBlockState(blockPos3, Blocks.AIR.getDefaultState(), 18);
				j--;
				blockStates[j] = blockState2;
			}

			for (int k = list.size() - 1; k >= 0; k--) {
				BlockPos blockPos3 = (BlockPos)list.get(k);
				BlockState blockState2 = world.getBlockState(blockPos3);
				blockPos3 = blockPos3.offset(direction);
				map.remove(blockPos3);
				world.setBlockState(blockPos3, Blocks.MOVING_PISTON.getDefaultState().with(FACING, dir), 68);
				world.setBlockEntity(blockPos3, PistonExtensionBlock.createBlockEntityPiston((BlockState)list2.get(k), dir, retract, false));
				j--;
				blockStates[j] = blockState2;
			}

			if (retract) {
				PistonType pistonType = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
				BlockState blockState3 = Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, dir).with(PistonHeadBlock.TYPE, pistonType);
				BlockState blockState2 = Blocks.MOVING_PISTON
					.getDefaultState()
					.with(PistonExtensionBlock.FACING, dir)
					.with(PistonExtensionBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
				map.remove(blockPos);
				world.setBlockState(blockPos, blockState2, 68);
				world.setBlockEntity(blockPos, PistonExtensionBlock.createBlockEntityPiston(blockState3, dir, true, true));
			}

			BlockState blockState4 = Blocks.AIR.getDefaultState();

			for (BlockPos blockPos4 : map.keySet()) {
				world.setBlockState(blockPos4, blockState4, 82);
			}

			for (Entry<BlockPos, BlockState> entry : map.entrySet()) {
				BlockPos blockPos5 = (BlockPos)entry.getKey();
				BlockState blockState5 = (BlockState)entry.getValue();
				blockState5.method_11637(world, blockPos5, 2);
				blockState4.updateNeighborStates(world, blockPos5, 2);
				blockState4.method_11637(world, blockPos5, 2);
			}

			for (int l = list3.size() - 1; l >= 0; l--) {
				BlockState blockState2 = blockStates[j++];
				BlockPos blockPos5 = (BlockPos)list3.get(l);
				blockState2.method_11637(world, blockPos5, 2);
				world.updateNeighborsAlways(blockPos5, blockState2.getBlock());
			}

			for (int l = list.size() - 1; l >= 0; l--) {
				world.updateNeighborsAlways((BlockPos)list.get(l), blockStates[j++].getBlock());
			}

			if (retract) {
				world.updateNeighborsAlways(blockPos, Blocks.PISTON_HEAD);
			}

			return true;
		}
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, EXTENDED);
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return (Boolean)state.get(EXTENDED);
	}

	@Override
	public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
		return false;
	}
}
