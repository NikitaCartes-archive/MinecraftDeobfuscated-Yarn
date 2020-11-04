package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.SwordItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class BambooBlock extends Block implements Fertilizable {
	protected static final VoxelShape SMALL_LEAVES_SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
	protected static final VoxelShape LARGE_LEAVES_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
	protected static final VoxelShape NO_LEAVES_SHAPE = Block.createCuboidShape(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
	public static final IntProperty AGE = Properties.AGE_1;
	public static final EnumProperty<BambooLeaves> LEAVES = Properties.BAMBOO_LEAVES;
	public static final IntProperty STAGE = Properties.STAGE;

	public BambooBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, Integer.valueOf(0)).with(LEAVES, BambooLeaves.NONE).with(STAGE, Integer.valueOf(0)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE, LEAVES, STAGE);
	}

	@Override
	public AbstractBlock.OffsetType getOffsetType() {
		return AbstractBlock.OffsetType.XZ;
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		VoxelShape voxelShape = state.get(LEAVES) == BambooLeaves.LARGE ? LARGE_LEAVES_SHAPE : SMALL_LEAVES_SHAPE;
		Vec3d vec3d = state.getModelOffset(world, pos);
		return voxelShape.offset(vec3d.x, vec3d.y, vec3d.z);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Vec3d vec3d = state.getModelOffset(world, pos);
		return NO_LEAVES_SHAPE.offset(vec3d.x, vec3d.y, vec3d.z);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		if (!fluidState.isEmpty()) {
			return null;
		} else {
			BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().down());
			if (blockState.isIn(BlockTags.BAMBOO_PLANTABLE_ON)) {
				if (blockState.isOf(Blocks.BAMBOO_SAPLING)) {
					return this.getDefaultState().with(AGE, Integer.valueOf(0));
				} else if (blockState.isOf(Blocks.BAMBOO)) {
					int i = blockState.get(AGE) > 0 ? 1 : 0;
					return this.getDefaultState().with(AGE, Integer.valueOf(i));
				} else {
					BlockState blockState2 = ctx.getWorld().getBlockState(ctx.getBlockPos().up());
					return !blockState2.isOf(Blocks.BAMBOO) && !blockState2.isOf(Blocks.BAMBOO_SAPLING)
						? Blocks.BAMBOO_SAPLING.getDefaultState()
						: this.getDefaultState().with(AGE, blockState2.get(AGE));
				}
			} else {
				return null;
			}
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!state.canPlaceAt(world, pos)) {
			world.breakBlock(pos, true);
		}
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return (Integer)state.get(STAGE) == 0;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if ((Integer)state.get(STAGE) == 0) {
			if (random.nextInt(3) == 0 && world.isAir(pos.up()) && world.getBaseLightLevel(pos.up(), 0) >= 9) {
				int i = this.countBambooBelow(world, pos) + 1;
				if (i < 16) {
					this.updateLeaves(state, world, pos, random, i);
				}
			}
		}
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return world.getBlockState(pos.down()).isIn(BlockTags.BAMBOO_PLANTABLE_ON);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (!state.canPlaceAt(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
		}

		if (direction == Direction.UP && newState.isOf(Blocks.BAMBOO) && (Integer)newState.get(AGE) > (Integer)state.get(AGE)) {
			world.setBlockState(pos, state.cycle(AGE), 2);
		}

		return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		int i = this.countBambooAbove(world, pos);
		int j = this.countBambooBelow(world, pos);
		return i + j + 1 < 16 && (Integer)world.getBlockState(pos.up(i)).get(STAGE) != 1;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		int i = this.countBambooAbove(world, pos);
		int j = this.countBambooBelow(world, pos);
		int k = i + j + 1;
		int l = 1 + random.nextInt(2);

		for (int m = 0; m < l; m++) {
			BlockPos blockPos = pos.up(i);
			BlockState blockState = world.getBlockState(blockPos);
			if (k >= 16 || (Integer)blockState.get(STAGE) == 1 || !world.isAir(blockPos.up())) {
				return;
			}

			this.updateLeaves(blockState, world, blockPos, random, k);
			i++;
			k++;
		}
	}

	@Override
	public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
		return player.getMainHandStack().getItem() instanceof SwordItem ? 1.0F : super.calcBlockBreakingDelta(state, player, world, pos);
	}

	protected void updateLeaves(BlockState state, World world, BlockPos pos, Random random, int height) {
		BlockState blockState = world.getBlockState(pos.down());
		BlockPos blockPos = pos.down(2);
		BlockState blockState2 = world.getBlockState(blockPos);
		BambooLeaves bambooLeaves = BambooLeaves.NONE;
		if (height >= 1) {
			if (!blockState.isOf(Blocks.BAMBOO) || blockState.get(LEAVES) == BambooLeaves.NONE) {
				bambooLeaves = BambooLeaves.SMALL;
			} else if (blockState.isOf(Blocks.BAMBOO) && blockState.get(LEAVES) != BambooLeaves.NONE) {
				bambooLeaves = BambooLeaves.LARGE;
				if (blockState2.isOf(Blocks.BAMBOO)) {
					world.setBlockState(pos.down(), blockState.with(LEAVES, BambooLeaves.SMALL), 3);
					world.setBlockState(blockPos, blockState2.with(LEAVES, BambooLeaves.NONE), 3);
				}
			}
		}

		int i = state.get(AGE) != 1 && !blockState2.isOf(Blocks.BAMBOO) ? 0 : 1;
		int j = (height < 11 || !(random.nextFloat() < 0.25F)) && height != 15 ? 0 : 1;
		world.setBlockState(pos.up(), this.getDefaultState().with(AGE, Integer.valueOf(i)).with(LEAVES, bambooLeaves).with(STAGE, Integer.valueOf(j)), 3);
	}

	protected int countBambooAbove(BlockView world, BlockPos pos) {
		int i = 0;

		while (i < 16 && world.getBlockState(pos.up(i + 1)).isOf(Blocks.BAMBOO)) {
			i++;
		}

		return i;
	}

	protected int countBambooBelow(BlockView world, BlockPos pos) {
		int i = 0;

		while (i < 16 && world.getBlockState(pos.down(i + 1)).isOf(Blocks.BAMBOO)) {
			i++;
		}

		return i;
	}
}
